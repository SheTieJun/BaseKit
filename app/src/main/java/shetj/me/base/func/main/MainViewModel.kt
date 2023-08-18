

package shetj.me.base.func.main

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.TimeUnit
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.toJson
import me.shetj.base.mvvm.viewbind.BaseViewModel
import me.shetj.base.network_coroutine.HttpResult
import me.shetj.base.network_coroutine.KCHttpV2
import me.shetj.base.network_coroutine.KCHttpV3
import me.shetj.base.network_coroutine.KCHttpV3.convertToT
import me.shetj.base.network_coroutine.KCHttpV4
import me.shetj.base.network_coroutine.buildRequest
import me.shetj.base.network_coroutine.cache.CacheMode
import me.shetj.base.network_coroutine.getDefReqOption
import me.shetj.base.tip.TipKit
import me.shetj.base.tools.time.CalendarReminderUtils
import shetj.me.base.bean.ResultMusic

/**
 * **@packageName：** shetj.me.base.fun<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/10/29 0029<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */
class MainViewModel : BaseViewModel() {
    var isGrayTheme = false
    var isAddJankStats: Boolean = false
    val liveDate = MutableLiveData<HttpResult<ResultMusic>>()
    fun getNightModel(): Int {
        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()

        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return AppCompatDelegate.MODE_NIGHT_YES
        }
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return AppCompatDelegate.MODE_NIGHT_NO
        }
        return AppCompatDelegate.MODE_NIGHT_YES
    }

    private val testUrl = "https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json"

    suspend fun getMusicV2() {
//        KCHttpV4.get<ResultMusic>(testUrl) //有错误待修复
        KCHttpV4.doGetCache(testUrl, hashMapOf()).convertToT<ResultMusic>().toJson().logI("ResultMusic")
        KCHttpV4.doGetReTry(testUrl, hashMapOf()).convertToT<ResultMusic>().toJson().logI("ResultMusic")
    }


    fun addEvent(context: AppCompatActivity) {
        val id = CalendarReminderUtils.addCalendarEvent(
            context,
            title = "这是一个测试时间",
            des = "这是测试时间描述",
            remindTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(5),
            endTime = null, previousTime = 5
        )
        if (id != -1L) {
            TipKit.success(context, "添加成功")
        }
    }
}