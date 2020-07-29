package me.shetj.base.ktx

import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A<C>  -> getClazz(this) -> C
 * 获取当前类的第一个泛型
 */
@Suppress("UNCHECKED_CAST")
fun <C> getClazz(obj: Any,position: Int = 0): Class<C> {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<C>
}

/**
 * 通过class<T>、T的无参数的构造函数，创建对象T
 */
fun <T> getObjByClassArg(obj: Any,position:Int = 0):T{
    return  getClazz<T>(obj,position).newInstance()
}

fun getParameterizedType(type: Type,typeArguments:Type): ParameterizedType? {
    // Type type = com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, clazz); = ArrayList<clazz>
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, type, typeArguments)
}