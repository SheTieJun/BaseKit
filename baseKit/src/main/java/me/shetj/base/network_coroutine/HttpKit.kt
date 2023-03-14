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
 * 网络相关：单例，用来修改具体的
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

    fun getKCCache(): KCCache {
        return KoinJavaComponent.get(KCCache::class.java)
    }
}
