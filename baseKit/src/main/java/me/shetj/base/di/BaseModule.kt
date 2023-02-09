/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.di

import java.io.File
import java.util.concurrent.TimeUnit
import me.shetj.base.BaseKit
import me.shetj.base.ktx.isTrue
import me.shetj.base.network.https.HttpsUtils
import me.shetj.base.network.interceptor.HeadersInterceptor
import me.shetj.base.network.interceptor.HttpLoggingInterceptor
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network.ohter.OkHttpDns
import me.shetj.base.network_coroutine.KCApiService
import me.shetj.base.network_coroutine.cache.KCCache
import me.shetj.base.network_coroutine.cache.LruDiskCache
import me.shetj.base.saver.SaverDatabase
import me.shetj.base.tools.app.AppUtils
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.base.tools.json.GsonKit
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.scope.get
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun getHttpModule(): Module {
  return module {
      single<OkHttpClient> {
          get<OkHttpClient.Builder>().build()
      }

      single {
          HttpLoggingInterceptor("OkHttp").apply {
              setLevel(HttpLoggingInterceptor.Level.BODY)
              setLogEnable(true)
          }
      }

      single {

          val timeout = 20000L // 默认的超时时间20秒

          OkHttpClient.Builder().apply {
              connectTimeout(timeout, TimeUnit.MILLISECONDS)
              readTimeout(timeout, TimeUnit.MILLISECONDS)
              writeTimeout(timeout, TimeUnit.MILLISECONDS)
              addInterceptor(
                  HeadersInterceptor(
                      HttpHeaders().apply {
                          put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, HttpHeaders.acceptLanguage)
                          put(HttpHeaders.HEAD_KEY_USER_AGENT, HttpHeaders.userAgent)
                      }
                  )
              )
              hostnameVerifier { _, _ -> true } // 主机验证
              val sslParams: HttpsUtils.SSLParams = HttpsUtils.getSslSocketFactory(null, null, null)
              sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
              addInterceptor(get(HttpLoggingInterceptor::class.java))
              cache(Cache(File(EnvironmentStorage.getPath(packagePath = "base")), 1024 * 1024 * 12))
              dns(OkHttpDns.getInstance())
          }
      }

      single {
          Retrofit.Builder().apply {
              addConverterFactory(GsonConverterFactory.create(GsonKit.gson))
              validateEagerly(BaseKit.isDebug.isTrue()) // 在开始的时候直接开始检测所有的方法
          }
      }

      single<KCApiService> {
          get<Retrofit.Builder>().apply {
              // 创建具体的ApiService的时候，才复制具体的client 和base 以及其他的变更
              client(get())
              baseUrl(BaseKit.baseUrl ?: "https://github.com/")
          }.build().create(KCApiService::class.java)
      }

      single {
          LruDiskCache(BaseKit.app.cacheDir, AppUtils.appVersionCode, 1024 * 1024 * 100)
      }

      single {
          KCCache()
      }
  }
}

fun getDBModule():Module{
    return  module {
        single(createdAtStart = false) { SaverDatabase.getInstance(androidApplication()) }

        // try to override existing definition. 覆盖其他实例
        // (override = true) -> FIX:Please use override option or check for definition '[Single:'me.shetj.base.saver.SaverDao']'
        single(createdAtStart = false) { get<SaverDatabase>().saverDao() }
    }
}

