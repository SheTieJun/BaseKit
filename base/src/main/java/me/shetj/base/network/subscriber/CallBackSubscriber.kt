package me.shetj.base.network.subscriber

import me.shetj.base.network.callBack.NetCallBack
import me.shetj.base.network.exception.ApiException

class CallBackSubscriber<T>(private val mCallBack: NetCallBack<T>)
    : BaseSubscriber<T>(mCallBack.context) {

    override fun onStart() {
        super.onStart()
        mCallBack.onStart()
    }

    override fun onError(e: ApiException) {
        mCallBack.onError(e)
    }

    override fun onNext(t: T) {
        super.onNext(t)
        mCallBack.onSuccess(t)
    }

    override fun onComplete() {
        super.onComplete()
        mCallBack.onComplete()
    }
}