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


//endregion

/**
 * 协程 Http请求
 * 感觉可能用的不错，所以就只写这几个方法了
 */
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
