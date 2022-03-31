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
package me.shetj.base.network_coroutine

import me.shetj.base.network.interceptor.HttpLoggingInterceptor
import me.shetj.base.network_coroutine.cache.KCCache
import okhttp3.OkHttpClient
import org.koin.java.KoinJavaComponent
import retrofit2.Retrofit

/**
 * @author stj
 * @Date 2021/10/22-14:36
 * @Email 375105540@qq.com
 */

object HttpKit {

    fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        return KoinJavaComponent.get(OkHttpClient.Builder::class.java)
    }

    // 对外暴露 Retrofit,方便自定义
    fun getRetrofitBuilder(): Retrofit.Builder {
        return KoinJavaComponent.get(Retrofit.Builder::class.java)
    }

    fun getOkHttpClient(): OkHttpClient {
        return KoinJavaComponent.get(OkHttpClient::class.java)
    }

    fun debugHttp(isPrintLog: Boolean) {
        KoinJavaComponent.get<HttpLoggingInterceptor>(HttpLoggingInterceptor::class.java)
            .setLogEnable(isPrintLog)
    }

    fun getKCCache():KCCache{
        return KoinJavaComponent.get(KCCache::class.java)
    }
}
