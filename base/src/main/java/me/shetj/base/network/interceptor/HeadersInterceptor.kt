package me.shetj.base.network.interceptor

import me.shetj.base.network.model.HttpHeaders
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

class HeadersInterceptor(private val headers: HttpHeaders) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        if (headers.headersMap?.isEmpty() == true) return chain.proceed(builder.build())
        try {
            headers.headersMap?.forEach {
                builder.header(it.key, it.value).build()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return chain.proceed(builder.build())
    }

}