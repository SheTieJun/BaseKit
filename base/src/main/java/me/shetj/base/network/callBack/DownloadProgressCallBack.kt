package me.shetj.base.network.callBack

import android.content.Context

abstract class DownloadProgressCallBack<T>(context: Context) : NetCallBack<T>(context) {
    override fun onSuccess(data: T) {}
    abstract fun update(bytesRead: Long, contentLength: Long, done: Boolean)
    abstract fun onComplete(path: String?)
    fun onCompleted() {}
}