package me.shetj.base.network.callBack

import com.google.gson.internal.`$Gson$Types`
import me.shetj.base.network.kt.ClassUtils
import me.shetj.base.network.model.ApiResult
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class CallBackProxy<T : ApiResult<R>, R>(private var mCallBack: CallBack<R>) : IType<T> {
    val callBack: CallBack<R>
        get() = mCallBack

    override fun getType(): Type { //CallBack代理方式，获取需要解析的Type
        var typeArguments: Type? = null
        if (mCallBack != null) {
            val rawType: Type = mCallBack.getRawType() //如果用户的信息是返回List需单独处理
            typeArguments = if (MutableList::class.java.isAssignableFrom(ClassUtils.getClass(rawType, 0)) || MutableMap::class.java.isAssignableFrom(ClassUtils.getClass(rawType, 0))) {
                mCallBack.getType()
            } else if (ClassUtils::class.java.isAssignableFrom(ClassUtils.getClass(rawType, 0))) {
                val type = mCallBack.getType()
                ClassUtils.getParameterizedType(type, 0)
            } else {
                val type = mCallBack.getType()
                ClassUtils.getClass(type, 0)
            }
        }
        if (typeArguments == null) {
            typeArguments = ResponseBody::class.java
        }
        var rawType: Type = ClassUtils.findNeedType(javaClass)
        if (rawType is ParameterizedType) {
            rawType = rawType.rawType
        }
        return `$Gson$Types`.newParameterizedTypeWithOwner(null, rawType, typeArguments)
    }

}