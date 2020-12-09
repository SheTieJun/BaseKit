package me.shetj.base.network_coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import me.shetj.base.network.exception.ApiException
import me.shetj.base.network.exception.ServerException
import me.shetj.base.network.func.ApiResultFunc
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.koin.java.KoinJavaComponent.get
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream


//region 下载状态相关
typealias HTTP_ERROR = (ApiException) -> Unit
typealias DOWNLOAD_ERROR = suspend (ApiException) -> Unit
typealias DOWNLOAD_PROCESS = suspend (downloadedSize: Long, length: Long, process: Float) -> Unit
typealias DOWNLOAD_SUCCESS = suspend (uri: File) -> Unit

sealed class DownloadStatus {
    class DownloadProcess(val currentLength: Long, val length: Long, val process: Float) : DownloadStatus()
    class DownloadError(val t: ApiException) : DownloadStatus()
    class DownloadSuccess(val file: File) : DownloadStatus()
}

//endregion

/**
 * 协程 Http请求
 * 感觉可能用的不错，所以就只写这几个方法了
 */
object KCHttp {

    val apiService: KCApiService = get(KCApiService::class.java)

    suspend inline fun <reified T> get(url: String, maps: Map<String, String>? = HashMap(), error: HTTP_ERROR = {}): T? {
        return try {
            apiService.get(url, maps).funToT()
        } catch (e: Exception) {
            e.printStackTrace()
            error(ApiException.handleException(e))
            null
        }
    }


    suspend inline fun <reified T> post(url: String, maps: Map<String, String>? = HashMap(), error: HTTP_ERROR = {}): T? {
        return try {
            apiService.post(url, maps).funToT()
        } catch (e: Exception) {
            e.printStackTrace()
            error(ApiException.handleException(e))
            null
        }
    }


    suspend inline fun <reified T> postJson(url: String, json: String, error: HTTP_ERROR = {}): T? {
        return try {
            apiService.postJson(url, json.createJson()).funToT()
        } catch (e: Exception) {
            e.printStackTrace()
            error(ApiException.handleException(e))
            null
        }
    }


    suspend inline fun <reified T> postBody(url: String, body: Any, error: HTTP_ERROR = {}): T? {
        return try {
            apiService.postBody(url, body).funToT()
        } catch (e: Exception) {
            e.printStackTrace()
            error(ApiException.handleException(e))
            null
        }
    }


    suspend inline fun <reified T> postBody(url: String, body: RequestBody, error: HTTP_ERROR = {}): T? {
        return try {
            apiService.postBody(url, body).funToT()
        } catch (e: Exception) {
            e.printStackTrace()
            error(ApiException.handleException(e))
            null
        }
    }

    inline fun <reified T> ResponseBody.funToT(): T? {
        return ApiResultFunc<T>(T::class.java).apply(this).let {
            if (it.isOk) {
                it.data
            } else {
                throw ServerException(it.code, it.msg)
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @JvmOverloads
    suspend fun download(url: String, outputFile: String, error: DOWNLOAD_ERROR = {},
                         process: DOWNLOAD_PROCESS = { _, _, _ -> },
                         success: DOWNLOAD_SUCCESS = { }) {

        val body = apiService.downloadFile(url)

        flow {
            try {
                val contentLength = body.contentLength()
//                val contentType = body.contentType()?.toString()
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
                    emit(DownloadStatus.DownloadProcess(currentLength.toLong(), contentLength, currentLength.toFloat() / contentLength.toFloat()))
                }
                bufferedInputStream.close()
                ops.close()
                ios.close()
                emit(DownloadStatus.DownloadSuccess(file))
            } catch (e: Exception) {
                emit(DownloadStatus.DownloadError(ApiException.handleException(e)))
            }
        }.flowOn(Dispatchers.IO)
                .collect {
                    when (it) {
                        is DownloadStatus.DownloadError -> error(it.t)
                        is DownloadStatus.DownloadProcess -> process(it.currentLength, it.length, it.process)
                        is DownloadStatus.DownloadSuccess -> success(it.file)
                    }
                }
    }
}
