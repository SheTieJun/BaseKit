package me.shetj.base.ktx

import kotlin.reflect.typeOf

@ExperimentalStdlibApi
inline fun <reified T> renderType(): String {
    val type = typeOf<T>()
    return type.toString()
}
