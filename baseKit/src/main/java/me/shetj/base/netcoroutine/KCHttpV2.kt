package me.shetj.base.netcoroutine

import me.shetj.base.ktx.convertToT
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import org.koin.java.KoinJavaComponent.get

object KCHttpV2 {

    val apiService: KCApiService = get(KCApiService::class.java)

    suspend inline fun <reified T> get(url: String, maps: Map<String, String>): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.get(url, maps).string()
            data.convertToT()
        }
    }

    suspend inline fun <reified T> post(url: String, maps: Map<String, String>): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.post(url, maps).string()
            data.convertToT()
        }
    }

    suspend inline fun <reified T> postJson(url: String, json: String): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.postJson(url, json.createJson()).string()
            data.convertToT()
        }
    }

    suspend inline fun <reified T> postBody(url: String, body: Any): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.postBody(url, body).string()
            data.convertToT()
        }
    }

    suspend inline fun <reified T> postBody(url: String, body: RequestBody): HttpResult<T> {
        return runCatching<T> {
            val data = apiService.postBody(url, body).string()
            data.convertToT()
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
        KCHttpV3.download(url, outputFile, onError, onProcess, onSuccess)
    }
}
