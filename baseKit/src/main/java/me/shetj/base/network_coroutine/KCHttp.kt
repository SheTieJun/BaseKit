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


package me.shetj.base.network_coroutine

import me.shetj.base.network.exception.ApiException
import okhttp3.RequestBody


//endregion

@Deprecated("use KCHttpV2 instead of", replaceWith = ReplaceWith("KCHttpV2"))
object KCHttp {

    suspend inline fun <reified T> get(
        url: String,
        maps: Map<String, String>? = HashMap(),
        error: OnError = {}
    ): T? {
        return KCHttpV2.get<T>(url, maps).onFailure {
            error.invoke(ApiException.handleException(it))
        }.getOrNull()
    }

    suspend inline fun <reified T> post(
        url: String,
        maps: Map<String, String>? = HashMap(),
        error: OnError = {}
    ): T? {
        return KCHttpV2.post<T>(url, maps).onFailure {
            error.invoke(ApiException.handleException(it))
        }.getOrNull()
    }


    suspend inline fun <reified T> postJson(url: String, json: String, error: OnError = {}): T? {
        return KCHttpV2.postJson<T>(url, json).onFailure {
            error.invoke(ApiException.handleException(it))
        }.getOrNull()
    }


    suspend inline fun <reified T> postBody(url: String, body: Any, error: OnError = {}): T? {
        return KCHttpV2.postBody<T>(url, body).onFailure {
            error.invoke(ApiException.handleException(it))
        }.getOrNull()
    }


    suspend inline fun <reified T> postBody(
        url: String,
        body: RequestBody,
        error: OnError = {}
    ): T? {
        return KCHttpV2.postBody<T>(url, body).onFailure {
            error.invoke(ApiException.handleException(it))
        }.getOrNull()
    }

    @JvmOverloads
    suspend fun download(
        url: String, outputFile: String, error: download_error = {},
        process: download_process = { _, _, _ -> },
        success: download_success = { }
    ) {
        KCHttpV2.download(url, outputFile, error, process, success)
    }
}
