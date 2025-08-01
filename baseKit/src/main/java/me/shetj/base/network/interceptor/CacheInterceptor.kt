package me.shetj.base.network.interceptor

import me.shetj.base.tools.app.NetworkUtils
import me.shetj.base.tools.app.Utils
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * "public"	所有内容都将被缓存(客户端和代理服务器都可缓存)<BR/>

"private"	内容只缓存到私有缓存中(仅客户端可以缓存，代理服务器不可缓存)<BR/>

"no-cache"	no-cache是会被缓存的，只不过每次在向客户端（浏览器）提供响应数据时，缓存都要向服务器评估缓存响应的有效性。<BR/>

"no-store"	所有内容都不会被缓存到缓存或 Internet 临时文件中<BR/>

"max-age=xxx (xxx is numeric)"	缓存的内容将在 xxx 秒后失效, 这个选项只在HTTP 1.1可用, 并如果和Last-Modified一起使用时, 优先级较高<BR/>

"max-stale"和"max-age"一样，只能设置在请求头里面。<BR/>

同时设置max-stale和max-age，缓存失效的时间按最长的算。(这个其实不用纠结) <BR/>
 */
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var cacheControl = request.cacheControl.toString()

        val availableNet = NetworkUtils.isAvailable(Utils.app)

        if (availableNet && cacheControl.isNotEmpty()) {
            request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }

        if (cacheControl.isEmpty() || "no-store" == cacheControl) {
            cacheControl = "no-store"
        } else if (availableNet) {
            cacheControl = "public,max-age=0"
        }
        val response = chain.proceed(request)
        return response.newBuilder().header("cache-Control", cacheControl)
            .removeHeader("Pragma").build()
    }
}
