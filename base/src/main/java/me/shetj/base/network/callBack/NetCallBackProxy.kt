package me.shetj.base.network.callBack

import com.google.gson.internal.`$Gson$Types`
import me.shetj.base.network.kt.ClassUtils
import me.shetj.base.network.model.ApiResult
import me.shetj.base.network.model.CacheResult
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

//使用代理的方式，把R 默认转换成 ApiResult<R> 来接收数据
abstract class NetCallBackProxy<T : ApiResult<R>, R>(private var mCallBack: NetCallBack<R>) : IType<T> {
    val callBack: NetCallBack<R>
        get() = mCallBack

    override fun getType(): Type { //CallBack代理方式，获取需要解析的Type
        var typeArguments: Type? = null
        if (mCallBack != null) {
            val rawType: Type = mCallBack.getRawType() //如果用户的信息是返回List需单独处理
            typeArguments = if (MutableList::class.java.isAssignableFrom(ClassUtils.getClass(rawType, 0))
                    || MutableMap::class.java.isAssignableFrom(ClassUtils.getClass(rawType, 0))) {
                mCallBack.getType()
            } else if (CacheResult::class.java.isAssignableFrom(ClassUtils.getClass(rawType, 0))) {
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
        // $Gson$Types.newParameterizedTypeWithOwner接收三个参数
        // ownerType:所处类的`Type，如果不是内部类或嵌套类则均为null。比如Response不是任何类的子类，因此此处为null
        // rawType:真实类型，在此处就是ApiResult<T>的类型
        // typeArguments:类型参数，此处就是Response<T>中泛型T的真实类型的Type，比如已知是Response<String>，
        // 那么typeArguments就是String.class或String.class.getGenericSuperclass()，其实是一样的
        // 转化成果rawType<typeArguments>
        // Type type = com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, clazz); = ArrayList<clazz>
        return `$Gson$Types`.newParameterizedTypeWithOwner(null, rawType, typeArguments)
    }

}