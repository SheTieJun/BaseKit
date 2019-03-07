package me.shetj.base.http.callback

import androidx.annotation.Keep

import com.zhouyou.http.callback.CallBack
import com.zhouyou.http.exception.ApiException

@Keep
class EasyCallBack<T> : CallBack<T>() {
    override fun onStart() {

    }

    override fun onCompleted() {

    }

    override fun onError(e: ApiException) {

    }

    override fun onSuccess(o: T) {

    }
}
