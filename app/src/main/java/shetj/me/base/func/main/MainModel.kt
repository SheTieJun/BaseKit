package shetj.me.base.func.main

import me.shetj.base.mvp.BaseModel
import me.shetj.base.network.RxHttp
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.base.network_coroutine.KCHttpV2
import me.shetj.base.network_coroutine.onFailure
import shetj.me.base.bean.ResultMusic
import timber.log.Timber

/**
 * **@packageName：** shetj.me.base.fun<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/10/29 0029<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */
class MainModel : BaseModel() {
    private val testUrl =
        "https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json"

    suspend fun getMusicV2(): ResultMusic? = KCHttpV2.get<ResultMusic>(testUrl, cacheOption = {
        this.cacheKey = "testUrl"
        this.cacheTime = 10
    }).onFailure {
        Timber.e(it)
    }.getOrNull()

    fun <T> getMusicByRxHttp(simpleNetCallBack: SimpleNetCallBack<T>) =
        RxHttp.get(testUrl).executeCus(simpleNetCallBack)
}