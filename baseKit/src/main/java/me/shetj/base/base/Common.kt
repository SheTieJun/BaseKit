package me.shetj.base.base


typealias onSuccess<T> = T?.() -> Unit

typealias onFail = (ex: Exception?) -> Unit

