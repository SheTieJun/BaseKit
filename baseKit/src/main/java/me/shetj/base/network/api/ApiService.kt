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


package me.shetj.base.network.api

import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

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
    fun deleteBody(@Url url: String?, @Body obj: Any?): Observable<ResponseBody>

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
    fun uploadFile(
        @Url url: String, @Part description: MultipartBody.Part?,
        @Part file: MultipartBody.Part?
    ): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFiles(
        @Url url: String?,
        @PartMap maps: Map<String, @JvmSuppressWildcards RequestBody>?
    ): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFiles(
        @Url url: String?,
        @Part parts: List<MultipartBody.Part>?
    ): Observable<ResponseBody>

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String?): Observable<ResponseBody>
}