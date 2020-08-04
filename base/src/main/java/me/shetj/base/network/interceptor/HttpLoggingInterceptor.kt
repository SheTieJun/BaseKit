package me.shetj.base.network.interceptor

import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import timber.log.Timber
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 *
 * 描述：设置日志拦截器
 * 提供了详细、易懂的日志打印<br></br>
 */
class HttpLoggingInterceptor : Interceptor {
    @Volatile
    var level = Level.NONE
        private set
    private var tag: String
    private var isLogEnable = false

    enum class Level {
        NONE,  //不打印log
        BASIC,  //只打印 请求首行 和 响应首行
        HEADERS,  //打印请求和响应的所有 Header
        BODY
    }

    fun log(message: String?) {
        Timber.tag(tag).i(message)
    }

    constructor(tag: String) {
        this.tag = tag
    }

    constructor(tag: String, isLogEnable: Boolean) {
        this.tag = tag
        this.isLogEnable = isLogEnable
    }

    fun setLevel(level: Level): HttpLoggingInterceptor {
        this.level = level
        return this
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        //请求日志拦截
        logForRequest(request, chain.connection())

        //执行请求，计算请求时间
        val startNs = System.nanoTime()
        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            log("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        //响应日志拦截
        return logForResponse(response, tookMs)
    }

    @Throws(IOException::class)
    private fun logForRequest(request: Request, connection: Connection?) {
        log("-------------------------------request-------------------------------")
        val logBody = level == Level.BODY
        val logHeaders = level == Level.BODY || level == Level.HEADERS
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
        try {
            val requestStartMessage = "--> " + request.method() + ' ' + URLDecoder.decode(request.url().url().toString(), UTF8.name()) + ' ' + protocol
            log(requestStartMessage)
            if (logHeaders) {
                val headers = request.headers()
                var i = 0
                val count = headers.size()
                while (i < count) {
                    log("\t" + headers.name(i) + ": " + headers.value(i))
                    i++
                }
                //log(" ");
                if (logBody && hasRequestBody) {
                    if (isPlaintext(requestBody!!.contentType())) {
                        bodyToString(request)
                    } else {
                        log("\tbody: maybe [file part] , too large too print , ignored!")
                    }
                }
            }
        } catch (e: Exception) {
            e(e)
        } finally {
            log("--> END " + request.method())
        }
    }

    private fun logForResponse(response: Response, tookMs: Long): Response {
        log("-------------------------------response-------------------------------")
        val builder = response.newBuilder()
        val clone = builder.build()
        var responseBody = clone.body()
        val logBody = level == Level.BODY
        val logHeaders = level == Level.BODY || level == Level.HEADERS
        try {
            log("<-- " + clone.code() + ' ' + clone.message() + ' ' + URLDecoder.decode(clone.request().url().url().toString(), UTF8.name()) + " (" + tookMs + "ms）")
            if (logHeaders) {
//                log(" ");
//                Headers headers = clone.headers();
//                for (int i = 0, count = headers.size(); i < count; i++) {
//                    log("\t" + headers.name(i) + ": " + headers.value(i));
//                }
//                log(" ");
                if (logBody && HttpHeaders.hasBody(clone)) {
                    if (isPlaintext(responseBody!!.contentType())) {
                        val body = responseBody.string()
                        log("\tbody:$body")
                        responseBody = ResponseBody.create(responseBody.contentType(), body)
                        return response.newBuilder().body(responseBody).build()
                    } else {
                        log("\tbody: maybe [file part] , too large too print , ignored!")
                    }
                }
                log(" ")
            }
        } catch (e: Exception) {
            e(e)
        } finally {
            log("<-- END HTTP")
        }
        return response
    }

    private fun bodyToString(request: Request) {
        try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()!!.writeTo(buffer)
            var charset = UTF8
            val contentType = copy.body()!!.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            log("\tbody:" + URLDecoder.decode(buffer.readString(charset), UTF8.name()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun e(t: Throwable) {
        if (isLogEnable) t.printStackTrace()
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        fun isPlaintext(mediaType: MediaType?): Boolean {
            if (mediaType == null) return false
            if (mediaType.type() == "text") {
                return true
            }
            var subtype = mediaType.subtype()
            subtype = subtype.toLowerCase()
            if (subtype.contains("x-www-form-urlencoded") ||
                    subtype.contains("json") ||
                    subtype.contains("xml") ||
                    subtype.contains("html")) //
                return true
            return false
        }
    }
}