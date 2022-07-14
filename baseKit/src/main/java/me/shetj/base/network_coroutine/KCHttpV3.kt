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

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import me.shetj.base.ktx.logD
import me.shetj.base.ktx.toBean
import me.shetj.base.network.exception.ApiException
import me.shetj.base.network.exception.ApiException.ERROR.OK_CACHE_EXCEPTION
import me.shetj.base.network.exception.ApiException.ERROR.TIMEOUT_ERROR
import me.shetj.base.network.exception.CacheException
import me.shetj.base.network.kt.createJson
import me.shetj.base.network_coroutine.cache.CacheMode
import okhttp3.RequestBody
import org.koin.java.KoinJavaComponent.get

/**
 * 协程 Http请求
 * 感觉可能用的不多，所以就只写这几个方法了
 * [me.shetj.base.network_coroutine.RequestOption]控制缓存
 */
object KCHttpV3 {

    const val TAG = "KCHttpV3"

    val apiService: KCApiService = get(KCApiService::class.java)

    @JvmOverloads
    suspend inline fun <reified T> get(
        url: String,
        maps: Map<String, String>? = HashMap(),
        noinline requestOption: (RequestOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            doNet(requestOption) {
                apiService.get(url, maps).string()
            }
        }
    }


    @JvmOverloads
    suspend inline fun <reified T> post(
        url: String,
        maps: Map<String, String>? = HashMap(),
        noinline requestOption: (RequestOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            doNet(requestOption) {
                apiService.post(url, maps).string()
            }
        }
    }

    @JvmOverloads
    suspend inline fun <reified T> postJson(
        url: String,
        json: String,
        noinline requestOption: (RequestOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            doNet(requestOption) {
                apiService.postJson(url, json.createJson()).string()
            }
        }
    }

    @JvmOverloads
    suspend inline fun <reified T> postBody(
        url: String,
        body: Any,
        noinline requestOption: (RequestOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            doNet(requestOption) {
                apiService.postBody(url, body).string()
            }
        }
    }

    @JvmOverloads
    suspend inline fun <reified T> postBody(
        url: String,
        body: RequestBody,
        noinline requestOption: (RequestOption.() -> Unit)? = null
    ): HttpResult<T> {
        return runCatching<T> {
            doNet(requestOption) {
                apiService.postBody(url, body).string()
            }
        }
    }

    /**
     * @param requestOption  请求选项
     * @param fromNetworkValue 如果没有使用缓存，就会通过改方法获取，来自网络内容
     */
    suspend inline fun <reified T> getDataFromApiOrCache(
        noinline requestOption: (RequestOption.() -> Unit)?,
        crossinline fromNetworkValue: suspend (timeout: Long, repeatNum: Int) -> String
    ): T {
        val cache = requestOption?.let { RequestOption().apply(requestOption) }
        val timeout = cache?.timeout ?: -1
        val repeatNum = cache?.repeatNum ?: 1

        return when (cache?.cacheMode) {
            CacheMode.DEFAULT -> {
                // 不使用自定义缓存,默认缓存规则，走OKhttp的Cache缓存
                fromNetworkValue(timeout, repeatNum)
            }
            CacheMode.FIRST_NET -> {
                // 先请求网络，请求网络失败后再加载缓存
                try {
                    fromNetworkValue(timeout, repeatNum)
                } catch (e: Exception) {
                    withContext(Dispatchers.IO) {
                        HttpKit.getKCCache().load(cache.cacheKey, cache.cacheTime)?.also {
                            "use cache key = ${cache.cacheKey} \n,value = $it ".logD(TAG)
                        }
                    } ?: throw ApiException.handleException(e)
                }
            }
            CacheMode.FIRST_CACHE -> {
                // 先加载缓存，缓存没有再去请求网络
                withContext(Dispatchers.IO) {
                    HttpKit.getKCCache().load(cache.cacheKey, cache.cacheTime)
                        ?.also {
                            "use cache :cacheKey = ${cache.cacheKey} \n,value = $it ".logD(TAG)
                        }
                } ?: kotlin.run {
                    fromNetworkValue(timeout, repeatNum).also {
                        saveCache(cache, it)
                    }
                }
            }
            CacheMode.ONLY_NET -> {
                // 仅加载网络，但数据依然会被缓存
                fromNetworkValue(timeout, repeatNum).also {
                    saveCache(cache, it)
                }
            }
            CacheMode.ONLY_CACHE -> {
                // 只读取缓存
                withContext(Dispatchers.IO) {
                    HttpKit.getKCCache().load(cache.cacheKey, cache.cacheTime)?.also {
                        "use cache : cacheKey = ${cache.cacheKey} \n,value = $it ".logD(TAG)
                    }
                } ?: throw CacheException(
                    OK_CACHE_EXCEPTION,
                    "cacheKey = '${cache.cacheKey}' no cache"
                )
            }
            CacheMode.CACHE_NET_DISTINCT -> {
                /* 先使用缓存，不管是否存在，仍然请求网络，会先把缓存回调给你，
                     * 络请求回来发现数据是一样的就不会再返回，否则再返回
                     */
                val cacheInfo = withContext(Dispatchers.IO) {
                    HttpKit.getKCCache().load(cache.cacheKey, cache.cacheTime)
                }
                val apiInfo = fromNetworkValue(timeout, repeatNum)

                if (cacheInfo != apiInfo) {
                    saveCache(cache, apiInfo)
                    apiInfo
                } else {
                    throw CacheException(OK_CACHE_EXCEPTION, "the same data,so not update")
                }
            }
            else -> {
                fromNetworkValue(timeout, repeatNum).also {
                    saveCache(cache, it)
                }
            }
        }.convertToT()
    }

    @JvmOverloads
    suspend fun download(
        url: String,
        outputFile: String,
        onError: download_error = {},
        onProcess: download_process = { _, _, _ -> },
        onSuccess: download_success = { }
    ) {
        flow {
            try {
                val body = apiService.downloadFile(url)
                val contentLength = body.contentLength()
                val inputStream = body.byteStream()
                val file = File(outputFile)
                val outputStream = FileOutputStream(file)

                var currentLength = 0
                var emitProgress = 0f

                val bufferSize = 1024 * 8
                val buffer = ByteArray(bufferSize)
                val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
                var readLength: Int
                while (bufferedInputStream.read(buffer, 0, bufferSize)
                        .also { readLength = it } != -1
                ) {
                    outputStream.write(buffer, 0, readLength)
                    currentLength += readLength
                    val progress = currentLength.toFloat() / contentLength.toFloat()
                    //每次超过%1才进行更新
                    if (progress - emitProgress > 0.01) {
                        emitProgress = progress
                        emit(
                            HttpResult.progress(
                                currentLength.toLong(),
                                contentLength,
                                emitProgress
                            )
                        )
                    }
                }
                bufferedInputStream.close()
                outputStream.close()
                inputStream.close()
                emit(HttpResult.success(file))
            } catch (e: Exception) {
                emit(HttpResult.failure<File>(ApiException.handleException(e)))
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                it.fold(onFailure = { e ->
                    e?.let { it1 -> onError(it1) }
                }, onSuccess = { file ->
                    onSuccess(file)
                }, onLoading = { progress ->
                    onProcess(progress.currentLength, progress.length, progress.process)
                })
            }
    }

    inline fun <reified T> String.convertToT() = if (T::class.java != String::class.java) {
        this.toBean()!!
    } else {
        this as T
    }


    /**
     * @param doNetWork 请求网络
     */
    suspend inline fun <reified T> doNet(
        noinline option: (RequestOption.() -> Unit)? = null,
        crossinline doNetWork: suspend () -> String
    ) = withContext(Dispatchers.IO) {
        getDataFromApiOrCache<T>(option,
            fromNetworkValue = { timeout, repeatNum ->
                retryRequest(timeout = timeout, repeatNum = repeatNum) {
                    doNetWork.invoke()
                }
            })
    }

    /**
     * 重试逻辑
     * @param repeatNum 重试次数
     * @param initialDelay 重试延迟,每次重试延迟多【次数*[factor]】秒,
     * @param timeout 协程的超时处理，这里超过retrofit的超时就意义不大了
     * @param factor 用处理重试延迟的样本
     */
    suspend fun retryRequest(
        repeatNum: Int = 3,
        initialDelay: Long = 1000,//1 second
        timeout: Long = 10000,//10 second
        factor: Double = 2.0,
        block: suspend () -> String
    ): String {
        if (repeatNum <= 0) return block()
        var currentDelay = initialDelay
        repeat(repeatNum - 1) {
            try {
                return requestWithTimeout(timeout, block)
            } catch (e: Exception) {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(timeout)
            }
        }
        try {
            return requestWithTimeout(timeout, block)
        } catch (e: TimeoutCancellationException) {
            throw ApiException(e, TIMEOUT_ERROR).apply {
                this.setDisplayMessage("coroun Timeout Cancel, Timeout $timeout, maybe has more time ")
            }
        } catch (e: Exception) {
            throw ApiException.handleException(e)
        }
    }

    /**
     * 自定义超时处理
     */
    private suspend fun requestWithTimeout(
        timeout: Long,
        block: suspend () -> String
    ): String {
        return if (timeout <= 0L) {
            block()
        } else {
            withTimeout(timeout) {
                block()
            }
        }
    }

    /**
     * use by [me.shetj.base.network_coroutine.KCHttpV3]
     */
    suspend fun saveCache(requestOption: RequestOption?, data: String) {
        if (!requestOption?.cacheKey.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                HttpKit.getKCCache().save(requestOption?.cacheKey, data)
            }
        }
    }
}
