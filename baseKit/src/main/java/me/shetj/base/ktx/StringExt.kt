package me.shetj.base.ktx

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Color
import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.debug.DebugFunc
import me.shetj.base.tools.file.StringUtils
import me.shetj.base.tools.json.GsonKit
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


fun String?.isPhone() = this?.let { StringUtils.isPhone(it) }?:false

fun String?.isIdCard() = this?.let { StringUtils.isIdCard(it) }?:false

val String?.md5 :String?
   get() = this?.let { ArmsUtils.encodeToMD5(it) }

fun String?.fromHtml() = this?.parseAsHtml()

val String?.toColor: Int?
    get() = try {
        Color.parseColor(this)
    } catch (e: java.lang.IllegalArgumentException) {
        null
    }

//region Json相关


fun highString(description: String, highStrings: List<String>?): Spanned {
    var replaceString = description
    highStrings?.forEach { highString ->
        replaceString = replaceString.replace(highString, "<font color=\"#FFBB22\">$highString</font>")
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
    //获取剪贴板管理器：
    this?.let {
        ArmsUtils.copyText(context, it)
        action.invoke()
    }
}

//region log 相关
fun String?.logi() {
    Log.i("base",this.toString())
}

fun String?.loge() {
    Timber.tag("base").e(this.toString())
}

fun String?.logd() {
    Timber.tag("base").e(this.toString())
}

fun String?.logOutOut() {
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
