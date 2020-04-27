package me.shetj.base.network.request

import io.reactivex.Observable
import okhttp3.ResponseBody

class GetRequest(url: String) : BaseRequest<GetRequest>(url) {

    override fun generateRequest(): Observable<ResponseBody> {
        return apiManager!!.get(this.url, params.urlParamsMap)
    }
}