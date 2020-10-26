package me.shetj.base.ktx

import android.content.Context
import android.graphics.Color
import androidx.core.text.parseAsHtml
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.file.StringUtils
import me.shetj.base.tools.json.GsonKit
import timber.log.Timber


fun String?.isPhone() = this?.let { StringUtils.isPhone(it) }

fun String?.isIdCard() = this?.let { StringUtils.isIdCard(it) }

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
    Timber.tag("base").i(this)
}

fun String?.loge() {
    Timber.tag("base").e(this)
}

fun String?.logd() {
    Timber.tag("base").d(this)
}

//endregion
