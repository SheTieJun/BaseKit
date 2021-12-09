package me.shetj.base.di

import me.shetj.base.S
import me.shetj.base.network.RxHttp.Companion.DEFAULT_MILLISECONDS
import me.shetj.base.network_coroutine.cache.LruDiskCache
import me.shetj.base.network.https.HttpsUtils
import me.shetj.base.network.interceptor.HeadersInterceptor
import me.shetj.base.network.interceptor.HttpLoggingInterceptor
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network.ohter.OkHttpDns
import me.shetj.base.network_coroutine.KCApiService
import me.shetj.base.saver.SaverDatabase
import me.shetj.base.tools.app.AppUtils
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.base.tools.json.GsonKit
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.core.scope.get
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

val dbModule = module {

    single(createdAtStart = false) { SaverDatabase.getInstance(androidApplication()) }

    //try to override existing definition. 覆盖其他实例
    //(override = true) -> FIX:Please use override option or check for definition '[Single:'me.shetj.base.saver.SaverDao']'
    single(createdAtStart = false) { get<SaverDatabase>().saverDao() }

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
        OkHttpClient.Builder().apply {
            connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            addInterceptor(HeadersInterceptor(HttpHeaders().apply {
                put(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, HttpHeaders.acceptLanguage)
                put(HttpHeaders.HEAD_KEY_USER_AGENT, HttpHeaders.userAgent)
            }))
            hostnameVerifier { _, _ -> true } //主机验证
            val sslParams: HttpsUtils.SSLParams = HttpsUtils.getSslSocketFactory(null, null, null)
            sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            addInterceptor(get(HttpLoggingInterceptor::class.java))
            cache(Cache(File(EnvironmentStorage.getPath(packagePath = "base")),1024*1024*12))
            dns(OkHttpDns.getInstance())
        }
    }

    single {
        Retrofit.Builder().apply {
            addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            addConverterFactory(GsonConverterFactory.create(GsonKit.gson))
            client(get())
            baseUrl(S.baseUrl ?: "https://me.shetj.com")
            validateEagerly(S.isDebug) //在开始的时候直接开始检测所有的方法
        }
    }



    single<KCApiService> {
        get<Retrofit.Builder>().build().create(KCApiService::class.java)
    }

    single <LruDiskCache>{
        LruDiskCache(S.app.cacheDir,AppUtils.appVersionCode,1024*1024*100)
    }


}