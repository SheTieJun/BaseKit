package me.shetj.base.kt

import android.content.Context
import android.os.Message
import androidx.core.text.parseAsHtml
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.json.EmptyUtils
import me.shetj.base.tools.json.GsonKit
import timber.log.Timber

/************************ 转化成message****************************************/

@JvmOverloads
fun <T> T.toMessage(code:Int = 1,action: (Message.() -> Unit?)? =null): Message {
    return  Message.obtain().apply {
        what = code
        obj = this@toMessage
        action?.let { it(this) }
    }
}

/***************************Json 相关**********************************/

fun Any.toJson() = GsonKit.objectToJson(this)

fun <T> String.toBean(clazz: Class<T>) = GsonKit.jsonToBean(this,clazz)

fun <T> String.toList(clazz: Class<T>) = GsonKit.jsonToList(this,clazz)

fun  String.toMap() = GsonKit.jsonToMap(this)

/**************************String 相关**********************************/

fun String.toMD5() = ArmsUtils.encodeToMD5(this)

fun String.fromHtml() = parseAsHtml()


fun Any.isEmpty() = EmptyUtils.isEmpty(this)

@JvmOverloads
fun String.copy(context :Context,action: (() -> Unit?)? =null){
        //获取剪贴板管理器：
        ArmsUtils.copyText(context,this)
        action?.let { it }
    }


fun String.log(){
    Timber.i(this)
}

fun Throwable.log(){
    Timber.i(this)
}