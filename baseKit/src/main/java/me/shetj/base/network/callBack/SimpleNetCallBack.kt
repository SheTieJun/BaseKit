package me.shetj.base.network.callBack

import android.content.Context

abstract class SimpleNetCallBack<T>(context: Context) : NetCallBack<T>(context) {
    override fun onStart() {}
    override fun onComplete() {}
    override fun onError(e: Exception) {}
    override fun onSuccess(data: T) {}
}