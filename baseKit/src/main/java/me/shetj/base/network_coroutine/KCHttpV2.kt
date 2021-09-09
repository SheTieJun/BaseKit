package me.shetj.base.network_coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.shetj.base.ktx.toBean
import me.shetj.base.network.exception.ApiException
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import org.koin.java.KoinJavaComponent.get
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream


/**
 * 协程 Http请求
 * 感觉可能用的不错，所以就只写这几个方法了
 */
object KCHttpV2 {

    val apiService: KCApiService = get(KCApiService::class.java)

    suspend inline fun <reified T> get(url: String, maps: Map<String, String>? = HashMap()): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.get(url, maps).string()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }


    suspend inline fun <reified T> post(url: String, maps: Map<String, String>? = HashMap()): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.post(url, maps).string()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }


    suspend inline fun <reified T> postJson(url: String, json: String): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.postJson(url, json.createJson()).toString()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }


    suspend inline fun <reified T> postBody(url: String, body: Any): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.postBody(url, body).toString()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }


    suspend inline fun <reified T> postBody(url: String, body: RequestBody): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.postBody(url, body).toString()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @JvmOverloads
    suspend fun download(url: String, outputFile: String,
                         onError: download_error = {},
                         onProcess: download_process = { _, _, _ -> },
                         onSuccess: download_success = { }) {
        val body = apiService.downloadFile(url)
        flow {
            try {
                val contentLength = body.contentLength()
                val ios = body.byteStream()
                val file = File(outputFile)
                val ops = FileOutputStream(file)
                var currentLength = 0
                val bufferSize = 1024 * 8
                val buffer = ByteArray(bufferSize)
                val bufferedInputStream = BufferedInputStream(ios, bufferSize)
                var readLength: Int
                while (bufferedInputStream.read(buffer, 0, bufferSize)
                                .also { readLength = it } != -1
                ) {
                    ops.write(buffer, 0, readLength)
                    currentLength += readLength
                    emit(HttpResult.progress(currentLength.toLong(), contentLength, currentLength.toFloat() / contentLength.toFloat()))
                }
                bufferedInputStream.close()
                ops.close()
                ios.close()
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

}
