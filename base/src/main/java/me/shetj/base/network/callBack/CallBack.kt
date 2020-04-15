package me.shetj.base.network.callBack

import android.content.Context
import me.shetj.base.network.kt.ClassUtils
import java.lang.reflect.Type

abstract class CallBack<T>(val context: Context) : IType<T> {

    abstract fun onStart()
    abstract fun onComplete()
    abstract fun onError(e: Exception)
    abstract fun onSuccess(data: T)

    override fun getType(): Type {
        return ClassUtils.findNeedClass(javaClass) //获取当前类型
    }

    open fun getRawType(): Type { //获取需要解析的泛型T raw类型
        return ClassUtils.findRawType(javaClass)
    }
}