package shetj.me.base.api

import io.reactivex.rxjava3.core.Observable
import me.shetj.annotation.apt.ApiProcessor
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

@ApiProcessor
interface BApi {
    @GET("/tic/live/check")
    fun getCanLive(@Query("token") jwt: String?, @QueryMap options: Map<String, String>?): Observable<String>

    @GET("/v1/lecture/{lecture_id}/homeworks/{homework_id}/share_homework")
    fun getShareHomeworkCard(@Path("lecture_id") lecture_id: Int, @Path("homework_id") homework_id: Int, @Query("token") jwt: String?): Observable<String>//endregion
}