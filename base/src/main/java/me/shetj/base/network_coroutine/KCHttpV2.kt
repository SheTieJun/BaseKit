package me.shetj.base.network_coroutine

import me.shetj.base.ktx.toBean
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import org.koin.java.KoinJavaComponent.get


/**
 * 协程 Http请求
 * 感觉可能用的不错，所以就只写这几个方法了
 */
object KCHttpV2 {

    val apiService: KCApiService = get(KCApiService::class.java)

    suspend inline fun <reified T> get(url: String, maps: Map<String, String>? = HashMap(), error: HTTP_ERROR = {}): Result<T> {
        return runCatching<T> {
            val data = apiService.get(url, maps).string()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }


    suspend inline fun <reified T> post(url: String, maps: Map<String, String>? = HashMap(), error: HTTP_ERROR = {}): Result<T> {
        return runCatching<T> {
            val data = apiService.post(url, maps).string()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }


    suspend inline fun <reified T> postJson(url: String, json: String, error: HTTP_ERROR = {}): Result<T> {
        return runCatching<T> {
            val data = apiService.postJson(url, json.createJson()).toString()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }


    suspend inline fun <reified T> postBody(url: String, body: Any, error: HTTP_ERROR = {}): Result<T> {
        return runCatching<T> {
            val data = apiService.postBody(url, body).toString()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }


    suspend inline fun <reified T> postBody(url: String, body: RequestBody, error: HTTP_ERROR = {}): Result<T> {
        return runCatching<T> {
            val data = apiService.postBody(url, body).toString()
            if (T::class.java != String::class.java) {
                data.toBean()!!
            } else {
                data as T
            }
        }
    }

}
