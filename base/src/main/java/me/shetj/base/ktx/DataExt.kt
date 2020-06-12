package me.shetj.base.ktx

import android.content.Context
import android.os.Bundle
import android.os.Message
import androidx.core.text.parseAsHtml
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.json.EmptyUtils
import me.shetj.base.tools.json.GsonKit
import timber.log.Timber
import kotlin.random.Random

//region 转化成message
@JvmOverloads
inline fun Any.toMessage(code: Int = 1, crossinline action: (Message.() -> Unit) = {}): Message {
    return Message.obtain().apply {
        what = code
        obj = this@toMessage
        action.invoke(this)
    }
}

/**
 * 获取参数
 */
inline fun <reified T> Bundle.getDataOrNull(key:String): T? {
    return getSerializable(key) as? T
}

//endregion 转化成message

//region Json相关

fun Any?.toJson() = this?.let { GsonKit.objectToJson(this) }

inline fun <reified T> String.toBean() = GsonKit.jsonToBean(this, T::class.java)

inline fun <reified T> String.toBeanList() = GsonKit.jsonToList(this, T::class.java)

fun String.toStringMap() = GsonKit.jsonToStringMap(this)

//endregion Json相关

//region String 相关
fun String.toMD5() = ArmsUtils.encodeToMD5(this)

fun String.fromHtml() = parseAsHtml()

/**
 * 把1~9 之间加0
 */
fun Int.unitFormat(): String {
    return if (this in 0..9) "0$this" else "" + this
}


fun Any.isEmpty() = EmptyUtils.isEmpty(this)

@JvmOverloads
fun String.copy(context: Context, action: (() -> Unit) = {}) {
    //获取剪贴板管理器：
    ArmsUtils.copyText(context, this)
    action.invoke()
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


//region 获取随机数
fun getRandomString(num: Int): String {
    var linkNo = ""
    // 用字符数组的方式随机
    val model = "0aAbBc1CdDeE2fFgGh3HiIjJ4kKlLm5MnNoO6pPqQr7RsStT8uUvVw9WxXyY0zZ"
    val m = model.toCharArray()
    var j = 0
    while (j < num) {
        val c = m[Random.nextInt(62)]
        //随机数之间没有重复的
        if (linkNo.contains(c.toString())) {
            continue
        }
        linkNo += c
        j++
    }
    return linkNo
}
//endregion