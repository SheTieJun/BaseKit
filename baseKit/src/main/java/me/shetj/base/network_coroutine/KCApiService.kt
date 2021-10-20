package me.shetj.base.network_coroutine

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface KCApiService {

    @POST
    @FormUrlEncoded
    suspend fun post(@Url url: String?, @FieldMap maps: Map<String, String>?): ResponseBody

    @POST
    suspend fun postBody(@Url url: String?, @Body any: Any?): ResponseBody

    @POST
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun postJson(@Url url: String?, @Body jsonBody: RequestBody?): ResponseBody

    @POST
    suspend fun postBody(@Url url: String?, @Body body: RequestBody?): ResponseBody

    @DELETE
    suspend fun delete(@Url url: String?, @QueryMap maps: Map<String, String>?): ResponseBody

    @GET
    suspend fun get(@Url url: String?, @QueryMap maps: Map<String, String>?): ResponseBody

    @HTTP(method = "DELETE", hasBody = true)
    suspend fun deleteBody(@Url url: String?, @Body `object`: Any?): ResponseBody

    @HTTP(method = "DELETE", hasBody = true)
    suspend fun deleteBody(@Url url: String?, @Body body: RequestBody?): ResponseBody

    //@DELETE()//delete body请求比较特殊 需要自定义
    @Headers("Content-Type: application/json", "Accept: application/json")
    @HTTP(method = "DELETE", hasBody = true)
    suspend fun deleteJson(@Url url: String?, @Body jsonBody: RequestBody): ResponseBody

    @PUT
    suspend fun put(@Url url: String?, @QueryMap maps: Map<String, String>): ResponseBody

    @PUT
    suspend fun putBody(@Url url: String?, @Body `object`: Any?): ResponseBody

    @PUT
    suspend fun putBody(@Url url: String?, @Body body: RequestBody?): ResponseBody

    @PUT
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun putJson(@Url url: String?, @Body jsonBody: RequestBody?): ResponseBody

    @Multipart
    @POST
    suspend fun uploadFile(@Url fileUrl: String?, @Part description:  MultipartBody.Part?, @Part file: MultipartBody.Part?): ResponseBody

    @Multipart
    @POST
    suspend fun uploadFiles(@Url url: String?, @PartMap maps: Map<String,@JvmSuppressWildcards RequestBody>?): ResponseBody

    @Multipart
    @POST
    suspend fun uploadFiles(@Url url: String?, @Part parts: List<MultipartBody.Part>): ResponseBody

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String?): ResponseBody
}