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

    override fun generateRequest(): Observable<ResponseBody> {
        return apiManager!!.get(this.url, params.urlParamsMap)
    }
}