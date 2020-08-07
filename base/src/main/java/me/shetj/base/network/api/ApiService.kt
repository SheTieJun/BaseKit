package me.shetj.base.network.api

import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    @POST
    @FormUrlEncoded
    fun post(@Url url: String?, @FieldMap maps: Map<String, String>?): Observable<ResponseBody>

    @POST
    fun postBody(@Url url: String?, @Body any: Any?): Observable<ResponseBody>

    @POST
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postJson(@Url url: String?, @Body jsonBody: RequestBody?): Observable<ResponseBody>

    @POST
    fun postBody(@Url url: String?, @Body body: RequestBody?): Observable<ResponseBody>

    @DELETE
    fun delete(@Url url: String?, @QueryMap maps: Map<String, String>?): Observable<ResponseBody>

    @GET
    fun get(@Url url: String?, @QueryMap maps: Map<String, String>?): Observable<ResponseBody>

    //@DELETE()//delete body请求比较特殊 需要自定义
    @HTTP(method = "DELETE", hasBody = true)
    fun deleteBody(@Url url: String?, @Body `object`: Any?): Observable<ResponseBody>

    //@DELETE()//delete body请求比较特殊 需要自定义
    @HTTP(method = "DELETE", hasBody = true)
    fun deleteBody(@Url url: String?, @Body body: RequestBody?): Observable<ResponseBody>

    //@DELETE()//delete body请求比较特殊 需要自定义
    @Headers("Content-Type: application/json", "Accept: application/json")
    @HTTP(method = "DELETE", hasBody = true)
    fun deleteJson(@Url url: String?, @Body jsonBody: RequestBody?): Observable<ResponseBody>

    @PUT
    fun put(@Url url: String?, @QueryMap maps: Map<String, String>?): Observable<ResponseBody>

    @PUT
    fun putBody(@Url url: String?, @Body `object`: Any?): Observable<ResponseBody>

    @PUT
    fun putBody(@Url url: String?, @Body body: RequestBody?): Observable<ResponseBody>

    @PUT
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun putJson(@Url url: String?, @Body jsonBody: RequestBody?): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFile(@Url url: String?, @Part("description") description: RequestBody?, @Part("files") file: MultipartBody.Part?): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFiles(@Url url: String?, @PartMap maps: Map<String, RequestBody>?): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFiles(@Url url: String?, @Part parts: List<MultipartBody.Part>?): Observable<ResponseBody>

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String?): Observable<ResponseBody>
}