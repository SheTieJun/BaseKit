package me.shetj.base.network.request

import io.reactivex.Observable
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import okhttp3.ResponseBody


class DeleteRequest(url: String) : BaseBodyRequest<DeleteRequest>(url) {

    override fun generateRequest(): Observable<ResponseBody>? {
        return when {
            this.request != null -> { //自定义的请求体
                apiManager!!.deleteBody(url, this.request)
            }
            json != null -> { //Json
                val body = json.createJson()
                apiManager!!.deleteJson(url, body)
            }
            this.obj != null -> { //自定义的请求object
                apiManager!!.deleteBody(url, obj)
            }
            string != null -> { //文本内容
                val body = RequestBody.create(mediaType, string!!)
                apiManager!!.deleteBody(url, body)
            }
            else -> {
                apiManager!!.delete(url, params.urlParamsMap)
            }
        }
    }
}