package me.shetj.base.ktx

import android.content.Context
import android.content.res.AssetManager
import android.text.Spanned
import androidx.core.graphics.toColorInt
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import me.shetj.base.BaseKit
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.debug.DebugFunc
import me.shetj.base.tools.file.StringUtils
import me.shetj.base.tools.json.GsonKit
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun String?.isPhone() = this?.let { StringUtils.isPhone(it) } ?: false

fun String?.isIdCard() = this?.let { StringUtils.isIdCard(it) } ?: false

val String?.md5: String?
    get() = this?.let { ArmsUtils.encodeToMD5(it) }

fun String?.fromHtml() = this?.parseAsHtml()

val String?.toColor: Int?
    get() = try {
        this?.toColorInt()
    } catch (e: java.lang.IllegalArgumentException) {
        null
    }

//region Json相关

fun highString(
    description: String,
    highStrings: List<String>?,
    color: String = "#FFBB22"
): Spanned {
    var replaceString = description
    highStrings?.forEach { highString ->
        replaceString =
            replaceString.replace(highString, "<font color=\"$color\">$highString</font>")
    }
    return HtmlCompat.fromHtml(replaceString, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun Any?.toJson() = this?.let { GsonKit.objectToJson(this) }

inline fun <reified T> String?.toBean() = this?.let { GsonKit.jsonToBean(it, T::class.java) }

inline fun <reified T> String?.toList() = this?.let { GsonKit.jsonToList(it, T::class.java) }

@Suppress("UnsafeCallOnNullableType")
inline fun <reified T> String.convertToT() = if (T::class.java != String::class.java) {
    this.toBean()!!
} else {
    this as T
}

fun String?.toMap() = this?.let { GsonKit.jsonToStringMap(it) }

//endregion Json相关

@JvmOverloads
fun String?.copy(context: Context, action: (() -> Unit) = {}) {
    // 获取剪贴板管理器：
    this?.let {
        ArmsUtils.copyText(context, it)
        action.invoke()
    }
}

//region log 相关
fun String?.logI(tag: String = BaseKit.TAG) {
    Timber.tag(tag).i(this.toString())
}

fun String?.logE(tag: String = BaseKit.TAG) {
    Timber.tag(tag).e(this.toString())
}

fun Throwable.logE(tag: String = BaseKit.TAG) {
    Timber.tag(tag).e(this)
}

fun String?.logD(tag: String = BaseKit.TAG) {
    Timber.tag(tag).d(this.toString())
}

fun String?.logW(tag: String = BaseKit.TAG) {
    Timber.tag(tag).w(this.toString())
}

fun String?.logV(tag: String = BaseKit.TAG) {
    Timber.tag(tag).v(this.toString())
}

fun String?.logWtf(tag: String = BaseKit.TAG) {
    Timber.tag(tag).wtf(this.toString())
}

fun Throwable?.logW(tag: String = BaseKit.TAG) {
    Timber.tag(tag).w(this)
}

fun String?.logUILife() {
    if (BaseKit.isLogUILife()) {
        this.logD("UI-Life")
    }
}

/**
 * 数据对象，因为可能数据过多导致打印问题，所以转成json
 */
fun String?.logJson(tag: String = BaseKit.TAG) {
    if (this.isNullOrEmpty()) {
        Timber.tag(tag).e("this Empty/Null json content")
        return
    }
    val message: String = try {
        if (startsWith("{")) {
            val jsonObject = JSONObject(this)
            jsonObject.toString(2) // 最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
        } else if (startsWith("[")) {
            val jsonArray = JSONArray(this)
            jsonArray.toString(2)
        } else {
            this
        }
    } catch (e: JSONException) {
        this
    }
    message.logI()
}

/**
 * Log chunked
 * 用于超长日志输出问题，分段打印日志
 * @param size
 */
fun String?.logChunked(size: Int = 200) {
    this?.let {
        if (it.length > size) {
            // 分开输出
            it.chunked(size).joinToString("\n").logI()
        } else {
            it.logI()
        }
    }
}

/**
 * 输出到文件
 */
fun String?.logOutPut() {
    DebugFunc.getInstance().saveLogToFile(this)
}

//endregion

/**
 * val json = getAssetsJson(context, "country.json")
 */
fun getAssetsString(context: Context, fileName: String): String {
    val stringBuilder = StringBuilder()
    try {
        val assetManager: AssetManager = context.assets
        val bf = BufferedReader(
            InputStreamReader(
                assetManager.open(fileName)
            )
        )
        var line: String?
        while (bf.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}
