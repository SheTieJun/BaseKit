package me.shetj.base.network.request

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.shetj.base.network.callBack.CallBack
import me.shetj.base.network.callBack.CallBackProxy
import me.shetj.base.network.func.ApiResultFunc
import me.shetj.base.network.func.HandleFuc
import me.shetj.base.network.func.HttpResponseFunc
import me.shetj.base.network.func.RetryExceptionFunc
import me.shetj.base.network.kt.RxUtil
import me.shetj.base.network.model.ApiResult
import me.shetj.base.network.subscriber.CallBackSubscriber
import okhttp3.ResponseBody
import timber.log.Timber

class GetRequest(url: String) : BaseRequest<GetRequest>(url) {

    //    fun <T> execute(callback: CallBack<T>): Observable<T> {
//        return build().generateRequest()
//                .map(ApiResultFunc<T>(callback.getType()))
//                .compose(if (isSyncRequest) RxUtil._main() else RxUtil._io_main())
//                .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
//    }
    fun <T> execute(callback: CallBack<T>): Disposable {
        val callBackProxy = object :CallBackProxy<ApiResult<T>,T>(callback){

        }
        return build().generateRequest()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ApiResultFunc<T>(callBackProxy.getType()))
                .map(HandleFuc<T>())
                .doOnSubscribe { disposable: Disposable -> Timber.i("+++doOnSubscribe+++%s", disposable.isDisposed) }
                .doFinally { Timber.i("+++doFinally+++") }
                .onErrorResumeNext(HttpResponseFunc<T>())
                .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
                .subscribeWith(CallBackSubscriber<T>(callBackProxy.callBack))
    }

    override fun generateRequest(): Observable<ResponseBody> {
        return apiManager!!.get(this.url, params.urlParamsMap)
    }
}