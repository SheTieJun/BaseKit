package me.shetj.base.network.model

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import me.shetj.base.BuildConfig
import me.shetj.base.ktx.toJson
import me.shetj.base.tools.app.ArmsUtils.Companion.getString
import me.shetj.base.tools.app.NetworkUtils
import me.shetj.base.tools.app.Utils.Companion.app
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HttpHeaders : Serializable {
    var headersMap: LinkedHashMap<String, String>? = null

    private fun init() {
        headersMap = LinkedHashMap()
    }

    constructor() {
        init()
    }

    constructor(key: String?, value: String?) {
        init()
        put(key, value)
    }

    fun put(key: String?, value: String?) {
        if (key != null && value != null) {
            headersMap!!.remove(key)
            headersMap!![key] = value
        }
    }

    fun put(headers: HttpHeaders?) {
        headers?.headersMap?.forEach {
            headersMap?.remove(it.key)
            headersMap?.put(it.key, it.value)
        }
    }

    val isEmpty: Boolean
        get() = headersMap!!.isEmpty()

    operator fun get(key: String): String? {
        return headersMap!![key]
    }

    fun remove(key: String): String {
        return headersMap!!.remove(key)!!
    }

    fun clear() {
        headersMap!!.clear()
    }

    val names: Set<String>
        get() = headersMap!!.keys

    fun toJSONString(): String {
        return headersMap?.toJson() ?: ""
    }

    override fun toString(): String {
        return "HttpHeaders{headersMap=$headersMap}"
    }

    companion object {
        const val FORMAT_HTTP_DATA = "EEE, dd MMM y HH:mm:ss 'GMT'"
        val GMT_TIME_ZONE = TimeZone.getTimeZone("GMT")
        const val HEAD_KEY_RESPONSE_CODE = "ResponseCode"
        const val HEAD_KEY_RESPONSE_MESSAGE = "ResponseMessage"
        const val HEAD_KEY_ACCEPT = "Accept"
        const val HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding"
        const val HEAD_VALUE_ACCEPT_ENCODING = "gzip, deflate"
        const val HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language"
        const val HEAD_KEY_CONTENT_TYPE = "Content-Type"
        const val HEAD_KEY_CONTENT_LENGTH = "Content-Length"
        const val HEAD_KEY_CONTENT_ENCODING = "Content-Encoding"
        const val HEAD_KEY_CONTENT_DISPOSITION = "Content-Disposition"
        const val HEAD_KEY_CONTENT_RANGE = "Content-Range"
        const val HEAD_KEY_CACHE_CONTROL = "Cache-Control"
        const val HEAD_KEY_CONNECTION = "Connection"
        const val HEAD_VALUE_CONNECTION_KEEP_ALIVE = "keep-alive"
        const val HEAD_VALUE_CONNECTION_CLOSE = "close"
        const val HEAD_KEY_DATE = "Date"
        const val HEAD_KEY_EXPIRES = "Expires"
        const val HEAD_KEY_E_TAG = "ETag"
        const val HEAD_KEY_PRAGMA = "Pragma"
        const val HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since"
        const val HEAD_KEY_IF_NONE_MATCH = "If-None-Match"
        const val HEAD_KEY_LAST_MODIFIED = "Last-Modified"
        const val HEAD_KEY_LOCATION = "Location"
        const val HEAD_KEY_USER_AGENT = "User-Agent"
        const val HEAD_KEY_COOKIE = "Cookie"
        const val HEAD_KEY_COOKIE2 = "Cookie2"
        const val HEAD_KEY_SET_COOKIE = "Set-Cookie"
        const val HEAD_KEY_SET_COOKIE2 = "Set-Cookie2"

        @SuppressLint("ConstantLocale")
        private val USER_AGENT =
                String.format(
                        " SystemName/%s SystemVersion/%s Device/%s NetType/%s Language/%s DeviceName/%s SdkVersion/%d Flavor/%s ",
                        "Android",
                        Build.VERSION.RELEASE,
                        Build.MODEL,
                        NetworkUtils.getNetWorkTypeName(app.applicationContext),
                        Locale.getDefault().language + "_" + Locale.getDefault().country,
                        checkNameAndValue(Build.MANUFACTURER),
                        Build.VERSION.SDK_INT,
                        BuildConfig.BUILD_TYPE)

        /**
         * Accept-Language: zh-CN,zh;q=0.8
         */
        var acceptLanguage: String? = null
            get() {
                if (TextUtils.isEmpty(field)) {
                    val locale = Locale.getDefault()
                    val language = locale.language
                    val country = locale.country
                    val acceptLanguageBuilder = StringBuilder(language)
                    if (!TextUtils.isEmpty(country)) acceptLanguageBuilder.append('-').append(country).append(',').append(language).append(";q=0.8")
                    field = acceptLanguageBuilder.toString()
                    return field
                }
                return field
            }
        var userAgent: String? = null
            @SuppressLint("PrivateApi")
            get() {
                if (TextUtils.isEmpty(field)) {
                    var webUserAgent: String? = null
                    try {
                        val sysResCls = Class.forName("com.android.internal.R\$string")
                        val webUserAgentField = sysResCls.getDeclaredField("web_user_agent")
                        val resId = webUserAgentField[null] as Int
                        webUserAgent = getString(app.applicationContext, resId)
                    } catch (_: Exception) {
                    }
                    if (TextUtils.isEmpty(webUserAgent)) {
                        webUserAgent = USER_AGENT
                    }
                    field = webUserAgent
                    return field
                }
                return field
            }

        fun getDate(gmtTime: String?): Long {
            return try {
                parseGMTToMillis(gmtTime)
            } catch (e: ParseException) {
                0
            }
        }

        fun getDate(milliseconds: Long): String {
            return formatMillisToGMT(milliseconds)
        }

        fun getExpiration(expiresTime: String?): Long {
            return try {
                parseGMTToMillis(expiresTime)
            } catch (e: ParseException) {
                -1
            }
        }

        fun getLastModified(lastModified: String?): Long {
            return try {
                parseGMTToMillis(lastModified)
            } catch (e: ParseException) {
                0
            }
        }

        fun getCacheControl(cacheControl: String?, pragma: String?): String? {
            // first http1.1, second http1.0
            return cacheControl ?: pragma
        }

        @Throws(ParseException::class)
        fun parseGMTToMillis(gmtTime: String?): Long {
            if (TextUtils.isEmpty(gmtTime)) return 0
            val formatter = SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US)
            formatter.timeZone = GMT_TIME_ZONE
            val date = formatter.parse(gmtTime!!)
            return date?.time ?: 0
        }

        fun formatMillisToGMT(milliseconds: Long): String {
            val date = Date(milliseconds)
            val simpleDateFormat = SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US)
            simpleDateFormat.timeZone = GMT_TIME_ZONE
            return simpleDateFormat.format(date)
        }

        private fun checkNameAndValue(value: String?): String? {
            var valueClone = value
            if (value == null) return Build.UNKNOWN
            var i = 0
            val length = value.length
            while (i < length) {
                val c = value[i]
                if (c <= '\u001f' || c >= '\u007f') {
                    valueClone = Build.UNKNOWN
                    break
                }
                i++
            }
            return valueClone
        }
    }
}