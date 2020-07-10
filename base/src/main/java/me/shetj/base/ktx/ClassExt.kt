package me.shetj.base.ktx

import java.lang.reflect.ParameterizedType

/**
 * Class<C>  -> getClazz(this) -> C
 */
fun <C> getClazz(obj: Any): C {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as C
}