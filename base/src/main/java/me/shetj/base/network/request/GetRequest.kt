package me.shetj.base.network.request

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import me.shetj.base.network.callBack.CallBack
import me.shetj.base.network.func.ApiResultFunc
import me.shetj.base.network.func.RetryExceptionFunc
import me.shetj.base.network.kt.RxUtil
import me.shetj.base.network.subscriber.CallBackSubscriber
import okhttp3.ResponseBody

class GetRequest(url: String) : BaseRequest<GetRequest>(url) {

    //    fun <T> execute(callback: CallBack<T>): Observable<T> {
//        return build().generateRequest()
//                .map(ApiResultFunc<T>(callback.getType()))
//                .compose(if (isSyncRequest) RxUtil._main() else RxUtil._io_main())
//                .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
//    }
    fun <T> execute(callback: CallBack<T>): Disposable {
        return build().generateRequest()
                .map(ApiResultFunc<T>(callback.getType()))
                .compose(if (isSyncRequest) RxUtil._main() else RxUtil._io_main())
                .retryWhen(RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
                .subscribeWith(CallBackSubscriber<T>(callback))
    }

    override fun generateRequest(): Observable<ResponseBody> {
        return apiManager!!.get(this.url, params.urlParamsMap)
    }
}