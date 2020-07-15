package me.shetj.base.ktx

import java.lang.reflect.ParameterizedType

/**
 * Class<C>  -> getClazz(this) -> C
 * 获取当前类的第一个泛型
 */
@Suppress("UNCHECKED_CAST")
fun <C> getClazz(obj: Any): C {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as C
}