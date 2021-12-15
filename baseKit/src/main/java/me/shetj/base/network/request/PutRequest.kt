/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.network.request

import io.reactivex.rxjava3.core.Observable
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import okhttp3.ResponseBody

class PutRequest(url: String) : BaseBodyRequest<PutRequest>(url) {

    override fun generateRequest(): Observable<ResponseBody> {
        return when {
            this.request != null -> { // 自定义的请求体
                apiManager!!.putBody(url, this.request)
            }
            json != null -> { // Json
                val body = json?.createJson()
                apiManager!!.putJson(url, body)
            }
            this.obj != null -> { // 自定义的请求object
                apiManager!!.putBody(url, obj)
            }
            string != null -> { // 文本内容
                val body = RequestBody.create(mediaType, string!!)
                apiManager!!.putBody(url, body)
            }
            else -> {
                apiManager!!.put(url, params.urlParamsMap)
            }
        }
    }
}
