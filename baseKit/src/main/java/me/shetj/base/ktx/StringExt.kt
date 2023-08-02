package me.shetj.base.ktx

import android.content.Context
import android.content.res.AssetManager
import android.text.Spanned
import androidx.core.graphics.toColorInt
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import me.shetj.base.BaseKit
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.debug.DebugFunc
import me.shetj.base.tools.file.StringUtils
import me.shetj.base.tools.json.GsonKit
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

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

fun Throwable?.logW(tag: String = BaseKit.TAG) {
    Timber.tag(tag).w(this)
}

/**
 * 打印json,因为可能数据过多，所以分行打印
 */
fun String?.logJson(tag: String = BaseKit.TAG) {
    if (this.isNullOrEmpty()) {
        Timber.tag(tag).e("Empty/Null json content")
        return
    }
    var isJson = false
    val message: String = try {
        if (startsWith("{")) {
            isJson = true
            val jsonObject = JSONObject(this)
            jsonObject.toString(2)//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
        } else if (startsWith("[")) {
            val jsonArray = JSONArray(this)
            isJson = true
            jsonArray.toString(2)
        } else {
            this
        }
    } catch (e: JSONException) {
        isJson = false
        this
    }

    if (isJson){
        printLine(tag, true)
        val lines = message.split(System.lineSeparator())
        lines.forEach {
            Timber.tag(tag).i(it)
        }
        printLine(tag, false)
    }else{
        Timber.tag(tag).i(message)
    }

}

private fun printLine(tag: String, isTop: Boolean) {
    if (isTop) {
        Timber.tag(tag).i("----------------------------------------------------------------------------------------")
    } else {
        Timber.tag(tag).i("----------------------------------------------------------------------------------------")
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
fun getAssetsJson(context: Context, fileName: String): String {
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
