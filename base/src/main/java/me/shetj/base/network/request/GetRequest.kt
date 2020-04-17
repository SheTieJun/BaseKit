package me.shetj.base.network.request

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.shetj.base.network.callBack.NetCallBack
import me.shetj.base.network.callBack.NetCallBackProxy
import me.shetj.base.network.func.ApiResultFunc
import me.shetj.base.network.func.HandleFuc
import me.shetj.base.network.func.HttpResponseFunc
import me.shetj.base.network.func.RetryExceptionFunc
import me.shetj.base.network.model.ApiResult
import me.shetj.base.network.subscriber.CallBackSubscriber
import okhttp3.ResponseBody
import timber.log.Timber

class GetRequest(url: String) : BaseRequest<GetRequest>(url) {

    /**
     * 执行，自定义数据类型
     */
    fun <T> executeCus(type: Class<T>): Observable<T> {
        return build().generateRequest()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ApiResultFunc<T>(type))
                .map(HandleFuc<T>())
                .doOnSubscribe { disposable: Disposable -> Timber.i("+++doOnSubscribe+++%s", disposable.isDisposed) }
                .doFinally { Timber.i("+++doFinally+++") }
                .onErrorResumeNext(HttpResponseFunc<T>())
                .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
    }

    /**
     * 执行，自定义数据类型
     */
    fun <T> executeCus(callback: NetCallBack<T>): Disposable {
        return build().generateRequest()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ApiResultFunc<T>(callback.getType()))
                .map(HandleFuc<T>())
                .doOnSubscribe { disposable: Disposable -> Timber.i("+++doOnSubscribe+++%s", disposable.isDisposed) }
                .doFinally { Timber.i("+++doFinally+++") }
                .onErrorResumeNext(HttpResponseFunc<T>())
                .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
                .subscribeWith(CallBackSubscriber<T>(callback))
    }

    /**
     * ApiResult<T> 类型的扩张类型
     */
    fun <T> execute(callback: NetCallBack<T>): Disposable {
        //T -> ApiResult<T>
        val callBackProxy = object :NetCallBackProxy<ApiResult<T>,T>(callback){

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