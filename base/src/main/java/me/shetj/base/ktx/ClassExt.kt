package me.shetj.base.ktx

import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Class<C>  -> getClazz(this) -> C
 * 获取当前类的第一个泛型
 */
@Suppress("UNCHECKED_CAST")
fun <C> getClazz(obj: Any): C {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as C
}


fun getParameterizedType(type: Type,typeArguments:Type): ParameterizedType? {
    // Type type = com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, clazz); = ArrayList<clazz>
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, type, typeArguments)
}