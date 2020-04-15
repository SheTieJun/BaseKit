package me.shetj.base.kt

import android.content.Context
import android.os.Message
import androidx.core.text.parseAsHtml
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.json.EmptyUtils
import me.shetj.base.tools.json.GsonKit
import timber.log.Timber

//region 转化成message
@JvmOverloads
fun <T> T.toMessage(code: Int = 1, action: (Message.() -> Unit?)? = null): Message {
    return Message.obtain().apply {
        what = code
        obj = this@toMessage
        action?.invoke(this)
    }
}

//endregion 转化成message

//region Json相关

fun Any?.toJson() = this?.let { GsonKit.objectToJson(this) }

fun <T> String.toBean(clazz: Class<T>) = GsonKit.jsonToBean(this, clazz)

fun <T> String.toBeanList(clazz: Class<T>) = GsonKit.jsonToList(this, clazz)

fun String.toStringMap() = GsonKit.jsonToStringMap(this)

//endregion Json相关

//region String 相关
fun String.toMD5() = ArmsUtils.encodeToMD5(this)

fun String.fromHtml() = parseAsHtml()

fun Int.unitFormat(): String {
    var retStr: String? = null
    retStr = if (this in 0..9) "0$this" else "" + this
    return retStr
}


fun Any.isEmpty() = EmptyUtils.isEmpty(this)

@JvmOverloads
fun String.copy(context: Context, action: (() -> Unit?)? = null) {
    //获取剪贴板管理器：
    ArmsUtils.copyText(context, this)
    action?.invoke()
}

//endregion String 相关

//region log 相关
fun String.log() {
    Timber.i(this)
}

fun Throwable.log() {
    Timber.i(this)
}
//endregion log 相关