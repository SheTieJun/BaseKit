package me.shetj.base.network.callBack

import android.content.Context

abstract class SimpleCallBack<T> (context: Context): CallBack<T>(context) {
    override fun onStart() {}
    override fun onComplete() {}
    override fun onError(e: Exception) {}
    override fun onSuccess(data: T) {}
}