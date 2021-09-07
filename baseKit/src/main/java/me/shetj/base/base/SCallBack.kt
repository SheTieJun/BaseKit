@file:JvmName("BaseCallBackKt")

package me.shetj.base.base

typealias OnSuccess<T> = (data:T?) -> Unit

typealias OnFail = (ex: Exception?) -> Unit

class SCallBack<T>{

    var onFail:OnFail?=null
    var onSuccess:OnSuccess<T>?=null

    companion object{
        fun <T> build(block:SCallBack<T>.() ->Unit) =SCallBack<T>().apply(block)
    }
}