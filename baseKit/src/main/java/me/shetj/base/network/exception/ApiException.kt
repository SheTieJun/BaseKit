package me.shetj.base.network.exception

import android.net.ParseException
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializer
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import retrofit2.HttpException
import java.io.NotSerializableException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

@Suppress("DEPRECATION")
class ApiException(throwable: Throwable, val code: Int) : Exception(throwable) {
    var displayMessage: String? = null
        private set
    override var message: String? = null
        private set

    fun setDisplayMessage(msg: String) {
        displayMessage = "$msg(code:$code)"
    }

    /**
     * 约定异常
     */
    object ERROR {
        /**
         * 未知错误
         */
        const val UNKNOWN = 1000

        /**
         * 解析错误
         */
        const val PARSE_ERROR = UNKNOWN + 1

        /**
         * 网络错误
         */
        const val NETWORD_ERROR = PARSE_ERROR + 1

        /**
         * 协议出错
         */
        const val HTTP_ERROR = NETWORD_ERROR + 1

        /**
         * 证书出错
         */
        const val SSL_ERROR = HTTP_ERROR + 1

        /**
         * 连接超时
         */
        const val TIMEOUT_ERROR = SSL_ERROR + 1

        /**
         * 调用错误
         */
        const val INVOKE_ERROR = TIMEOUT_ERROR + 1

        /**
         * 类转换错误
         */
        const val CAST_ERROR = INVOKE_ERROR + 1

        /**
         * 请求取消
         */
        const val REQUEST_CANCEL = CAST_ERROR + 1

        /**
         * 未知主机错误
         */
        const val UNKNOWNHOST_ERROR = REQUEST_CANCEL + 1

        /**
         * 空指针错误
         */
        const val NULLPOINTER_EXCEPTION = UNKNOWNHOST_ERROR + 1

        /**
         * 缓存错误
         */
        const val OK_CACHE_EXCEPTION = NULLPOINTER_EXCEPTION + 1
    }

    companion object {
        // 对应HTTP的状态码
        private const val BADREQUEST = 400
        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
        private const val METHOD_NOT_ALLOWED = 405
        private const val REQUEST_TIMEOUT = 408
        private const val INTERNAL_SERVER_ERROR = 500
        private const val BAD_GATEWAY = 502
        private const val SERVICE_UNAVAILABLE = 503
        private const val GATEWAY_TIMEOUT = 504
        const val UNKNOWN = 1000
        const val PARSE_ERROR = 1001
        fun handleException(e: Throwable): ApiException {
            return when (e) {
                is HttpException -> {
                    ApiException(e, e.code()).apply {
                        message = e.message
                    }
                }
                is ServerException -> {
                    ApiException(e, e.errCode).apply {
                        message = e.message
                    }
                }
                is CacheException -> {
                    ApiException(e, ERROR.OK_CACHE_EXCEPTION).apply {
                        message = "缓存处理异常：" + e.message
                    }
                }
                is JsonParseException,
                is JSONException,
                is JsonSyntaxException,
                is NotSerializableException,
                is ParseException -> {
                    ApiException(e, ERROR.PARSE_ERROR).apply {
                        message = "解析错误"
                    }
                }
                is ClassCastException -> {
                    ApiException(e, ERROR.CAST_ERROR).apply {
                        message = "类型转换错误"
                    }
                }
                is ConnectException -> {
                    ApiException(e, ERROR.NETWORD_ERROR).apply {
                        message = "连接失败"
                    }
                }
                is SSLHandshakeException -> {
                    ApiException(e, ERROR.SSL_ERROR).apply {
                        message = "证书验证失败"
                    }
                }
                is SocketTimeoutException -> {
                    ApiException(e, ERROR.TIMEOUT_ERROR).apply {
                        message = "连接超时"
                    }
                }
                is UnknownHostException -> {
                    ApiException(e, ERROR.UNKNOWNHOST_ERROR).apply {
                        message = "无法解析该域名"
                    }
                }
                is NullPointerException -> {
                    ApiException(e, ERROR.NULLPOINTER_EXCEPTION).apply {
                        message = "NullPointerException"
                    }
                }
                is ApiException -> e
                else -> {
                    ApiException(e, ERROR.UNKNOWN).apply {
                        message = "未知错误:${e.message}"
                    }
                }
            }
        }
    }

    init {
        message = throwable.message
    }
}
