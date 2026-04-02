package me.shetj.base.network.interceptor

import me.shetj.base.netcoroutine.HttpKit
import me.shetj.base.network.model.HttpHeaders
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import java.io.IOException

/**
 * Received cookies interceptor
 * 用于支持一些公开的API,他们使用的是cookie来进行权限验证
 */
class ReceivedCookiesInterceptor(private var enable: Boolean = false) : Interceptor {

    fun setEnable(enable: Boolean) {
        this.enable = enable
    }

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (enable) {
            val cookies = originalResponse.headers(HttpHeaders.HEAD_KEY_SET_COOKIE)
            if (cookies.isNotEmpty()) {
                for (header in cookies) {
                    HttpKit.addCookie(header)
                }
            }
        }
        return originalResponse
    }
}
