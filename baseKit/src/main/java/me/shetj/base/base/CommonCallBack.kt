package me.shetj.base.base

typealias OnSuccess<T> = (data:T?) -> Unit

typealias OnFail = (ex: Exception?) -> Unit

class CommonCallBack<T>{
    var onFail:OnFail?=null
    var onSuccess:OnSuccess<T>?=null

    companion object{
        fun <T> build(block:CommonCallBack<T>.() ->Unit) =CommonCallBack<T>().apply(block)
    }
}