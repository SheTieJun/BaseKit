package me.shetj.base.network_coroutine

import me.shetj.base.BaseKit
import me.shetj.base.network.interceptor.HttpLoggingInterceptor
import me.shetj.base.network.interceptor.ReceivedCookiesInterceptor
import me.shetj.base.network.model.HttpHeaders
import me.shetj.base.network_coroutine.cache.KCCache
import me.shetj.base.tools.file.SPUtils
import me.shetj.base.tools.json.GsonKit
import okhttp3.OkHttpClient
import org.koin.java.KoinJavaComponent
import retrofit2.Retrofit

/**
 * @author stj
 * @Date 2021/10/22-14:36
 * @Email 375105540@qq.com
 * 网络相关：单例，用来更新具体的修改，比如需要是否打印日志
 */

object HttpKit {

    private val headers by lazy { getHttHeaders() }

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

    private fun getHttHeaders(): HttpHeaders {
        return KoinJavaComponent.get(HttpHeaders::class.java)
    }

    fun enableReceivedCookies(enable: Boolean) {
        KoinJavaComponent.get<ReceivedCookiesInterceptor>(ReceivedCookiesInterceptor::class.java)
            .setEnable(enable)
    }


    fun addCookie(cookie: String) {
        headers.put(HttpHeaders.HEAD_KEY_COOKIE, cookie)
        saveCookie()
    }

    private fun saveCookie() {
        SPUtils.put(BaseKit.app, HttpHeaders.HEAD_KEY_COOKIE, headers.toJSONString())
    }

    fun loadCookie() {
        enableReceivedCookies(true)
        (SPUtils.get(BaseKit.app, HttpHeaders.HEAD_KEY_COOKIE, "") as? String)?.let {
            GsonKit.jsonToStringMap(it)?.forEach { (key, value) ->
                headers.put(key, value)
            }
        }
    }


}
