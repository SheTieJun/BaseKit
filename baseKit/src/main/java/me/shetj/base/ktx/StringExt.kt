/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.ktx

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Color
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.debug.DebugFunc
import me.shetj.base.tools.file.StringUtils
import me.shetj.base.tools.json.GsonKit
import timber.log.Timber

fun String?.isPhone() = this?.let { StringUtils.isPhone(it) } ?: false

fun String?.isIdCard() = this?.let { StringUtils.isIdCard(it) } ?: false

val String?.md5: String?
    get() = this?.let { ArmsUtils.encodeToMD5(it) }

fun String?.fromHtml() = this?.parseAsHtml()

val String?.toColor: Int?
    get() = try {
        Color.parseColor(this)
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
fun String?.logI(tag: String = "base") {
    Timber.tag(tag).i(this.toString())
}

fun String?.logE(tag: String = "base") {
    Timber.tag(tag).e(this.toString())
}

fun Throwable.logE(tag: String = "base") {
    Timber.tag(tag).e(this)
}

fun String?.logD(tag: String = "base") {
    Timber.tag(tag).d(this.toString())
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
