/*
 * MIT License
 *
 * Copyright (c) 2021 SheTieJun
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

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.shetj.base.ktx.toBean
import me.shetj.base.network.exception.ApiException
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import org.koin.java.KoinJavaComponent.get

/**
 * 不使用缓存缓存
 */
object KCHttpV2 {

    val apiService: KCApiService = get(KCApiService::class.java)

    suspend inline fun <reified T> get(url: String, maps: Map<String, String>? = HashMap()): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.get(url, maps).string()
            funTo(data)
        }
    }


    suspend inline fun <reified T> post(url: String, maps: Map<String, String>? = HashMap()): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.post(url, maps).string()
            funTo(data)
        }
    }


    suspend inline fun <reified T> postJson(url: String, json: String): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.postJson(url, json.createJson()).string()
            funTo(data)
        }
    }


    suspend inline fun <reified T> postBody(url: String, body: Any): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.postBody(url, body).string()
            funTo(data)
        }
    }


    suspend inline fun <reified T> postBody(url: String, body: RequestBody): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.postBody(url, body).string()
            funTo(data)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @JvmOverloads
    suspend fun download(
        url: String, outputFile: String,
        onError: download_error = {},
        onProcess: download_process = { _, _, _ -> },
        onSuccess: download_success = { }
    ) {
        KCHttpV3.download(url, outputFile,onError, onProcess, onSuccess)
    }

    inline fun <reified T> funTo(data: String) = if (T::class.java != String::class.java) {
        data.toBean()!!
    } else {
        data as T
    }
}