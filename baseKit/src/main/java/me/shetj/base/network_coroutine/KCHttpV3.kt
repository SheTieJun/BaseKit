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
import me.shetj.base.network_coroutine.RequestOption.Companion
import me.shetj.base.network_coroutine.cache.CacheMode
import okhttp3.RequestBody
import org.koin.java.KoinJavaComponent.get

/**
 * 协程 Http请求
 * - 感觉可能用的不多，所以就只写这几个方法了
 * 带重试
 */
object KCHttpV3 {

    const val TAG = "KCHttpV3"

    val apiService: KCApiService = get(KCApiService::class.java)

    suspend inline fun <reified T> get(
        url: String,
        maps: Map<String, String>? = HashMap(),
    ): HttpResult<T> {
        return runCatching<T> {
            doGet(url, maps).convertToT()
        }
    }


    suspend inline fun <reified T> post(
        url: String,
        maps: Map<String, String>? = HashMap(),
    ): HttpResult<T> {
        return runCatching<T> {
            doPost(url, maps).convertToT()
        }
    }


    suspend inline fun <reified T> postJson(
        url: String,
        json: String,
    ): HttpResult<T> {
        return runCatching<T> {
            doPostJson(url, json).convertToT()
        }
    }


    suspend inline fun <reified T> postBody(
        url: String,
        body: RequestBody,
    ): HttpResult<T> {
        return runCatching<T> {
            doPostBody(url, body).convertToT()
        }
    }

    @JvmOverloads
    suspend fun doPost(
        url: String,
        maps: Map<String, String>? = HashMap(),
    ): String {
        return retryRequest {
            apiService.post(url, maps).string()
        }
    }
    @JvmOverloads
    suspend fun doGet(
        url: String,
        maps: Map<String, String>? = HashMap(),
    ): String {
        return retryRequest {
            apiService.get(url, maps).string()
        }
    }


    suspend fun doPostJson(
        url: String,
        json: String,
    ): String {
        return retryRequest {
            apiService.postJson(url, json.createJson()).string()
        }
    }


    suspend fun doPostBody(
        url: String,
        body: RequestBody,
    ): String {
        return retryRequest {
            apiService.postBody(url, body).string()
        }
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
                    if (progress - emitProgress >= 0.01) {
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
                this.setDisplayMessage("coroutines Timeout Cancel, Timeout $timeout, maybe has more time ")
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

}
