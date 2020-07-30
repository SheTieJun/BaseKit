package me.shetj.base.network_coroutine

import me.shetj.base.network.exception.ServerException
import me.shetj.base.network.func.ApiResultFunc
import me.shetj.base.network.kt.createJson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.koin.java.KoinJavaComponent.get


/**
 * 协程 Http请求
 * 感觉可能用的不错，所以就只写这几个方法了
 */
object KCHttp {

    val apiService: KCApiService = get(KCApiService::class.java)


    @JvmStatic
    suspend inline fun <reified T> get(url: String, maps: Map<String, String>? =HashMap()): T?{
        return  apiService.get(url, maps).funToT()
    }


    @JvmStatic
    suspend inline fun <reified T> post(url: String, maps: Map<String, String>? =HashMap()): T?{
        return  apiService.post(url, maps).funToT()
    }



    @JvmStatic
    suspend inline fun <reified T> postJson(url: String, json: String): T?{
        return  apiService.postJson(url, json.createJson()).funToT()
    }


    @JvmStatic
    suspend inline fun <reified T> postBody(url: String, body: Any): T?{
        return  apiService.postBody(url, body).funToT()
    }


    @JvmStatic
    suspend inline fun <reified T> postBody(url: String, body: RequestBody): T?{
        return  apiService.postBody(url, body).funToT()
    }


    inline fun <reified T> ResponseBody.funToT() : T?{
       return ApiResultFunc<T>(T::class.java).apply(this).let {
           if (it.isOk) {
               it.data
           } else {
               throw ServerException(it.code, it.msg)
           }
       }
    }
}