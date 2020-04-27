package me.shetj.base.network.callBack

import android.content.Context
import me.shetj.base.network.kt.ClassUtils
import java.lang.reflect.Type

// T  = List<MusicBean>
abstract class NetCallBack<T>(val context: Context) : IType<T> {

    abstract fun onStart()
    abstract fun onComplete()
    abstract fun onError(e: Exception)
    abstract fun onSuccess(data: T)

    override fun getType(): Type {
        return ClassUtils.findNeedClass(javaClass) //获取当前类型 T java.util.List<? extends shetj.me.base.bean.MusicBean>
    }

    open fun getRawType(): Type { //获取需要解析的泛型 <T> raw类型 interface java.util.List
        return ClassUtils.findRawType(javaClass)
    }
}