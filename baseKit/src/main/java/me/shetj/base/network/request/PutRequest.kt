package me.shetj.base.network.request

import io.reactivex.rxjava3.core.Observable
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import okhttp3.ResponseBody


class PutRequest(url: String) : BaseBodyRequest<PutRequest>(url) {


    override fun generateRequest(): Observable<ResponseBody>? {
        return when {
            this.request != null -> { //自定义的请求体
                apiManager!!.putBody(url, this.request)
            }
            json != null -> { //Json
                val body = json?.createJson()
                apiManager!!.putJson(url, body)
            }
            this.obj != null -> { //自定义的请求object
                apiManager!!.putBody(url, obj)
            }
            string != null -> { //文本内容
                val body = RequestBody.create(mediaType, string!!)
                apiManager!!.putBody(url, body)
            }
            else -> {
                apiManager!!.put(url, params.urlParamsMap)
            }
        }
    }
}