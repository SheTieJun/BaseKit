package me.shetj.base.ktx

import java.lang.reflect.ParameterizedType

/**
 * Class<C>  -> getClazz(this) -> C
 */
@Suppress("UNCHECKED_CAST")
fun <C> getClazz(obj: Any): C {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as C
}