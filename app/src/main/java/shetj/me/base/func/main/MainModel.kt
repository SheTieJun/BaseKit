package shetj.me.base.func.main

import me.shetj.base.ktx.doOnIO
import me.shetj.base.mvp.BaseModel
import me.shetj.base.network.RxHttp
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.base.network_coroutine.KCHttp
import shetj.me.base.bean.ResultMusic

/**
 * **@packageName：** shetj.me.base.fun<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/10/29 0029<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */
class MainModel : BaseModel() {
    private val testUrl = "https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json"
    override fun onDestroy() {}

    suspend fun getMusic(): ResultMusic? = doOnIO {
       KCHttp.get<ResultMusic>(testUrl)
    }


    fun <T> getMusicByRxHttp(simpleNetCallBack: SimpleNetCallBack<T>) {
        //            RxHttp.get(testUrl)
        //                    .executeCus(object : SimpleNetCallBack<ResultMusic>(this) {
        //                        override fun onSuccess(data: ResultMusic) {
        //                            super.onSuccess(data)
        //                            Timber.i(data.toJson())
        //                        }
        //
        //                        override fun onError(e: Exception) {
        //                            super.onError(e)
        //                            Timber.e(e)
        //                        }
        //                    })

        //            RxHttp.get(testUrl)
        //                    .executeCus(ResultMusic::class.java)
        //                    .map { it.data }
        //                    .subscribe ({
        //                        Timber.i(it.toJson())
        //                    },{
        //                        Timber.e(it)
        //                    })
//
        RxHttp.get(testUrl)
                .executeCus(simpleNetCallBack)


        //            RxHttp.get(testUrl)
        //                    .execute(object : SimpleNetCallBack<List<MusicBean>>(this) {
        //                        override fun onSuccess(data: List<MusicBean>) {
        //                            super.onSuccess(data)
        //                            Timber.i(data.toJson())
        //                        }
        //
        //                        override fun onError(e: Exception) {
        //                            super.onError(e)
        //                            Timber.e(e)
        //                        }
        //                    })
    }


}