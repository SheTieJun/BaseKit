package me.shetj.base.network.callBack

import java.lang.reflect.Type

interface IType<T> {
    fun getType(): Type
}