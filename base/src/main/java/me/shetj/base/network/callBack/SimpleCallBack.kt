package me.shetj.base.network.callBack

abstract class SimpleCallBack<T> : CallBack<T>() {
    override fun onStart() {}
    override fun onComplete() {}
    override fun onError(e: Exception) {}
    override fun onSuccess(data: T) {}
}