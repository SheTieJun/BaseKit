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


package shetj.me.base.func.main

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.TimeUnit
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.base.network_coroutine.HttpResult
import me.shetj.base.network_coroutine.KCHttpV3
import me.shetj.base.network_coroutine.KCHttpV4
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


    private suspend fun getMusicV3() = KCHttpV3.get<ResultMusic>(testUrl){
        this.cacheKey = "testUrl"
        this.cacheTime = 10
        this.cacheMode = CacheMode.ONLY_NET
        this.repeatNum = 10
        this.timeout = 5000L
    }

    //    private suspend fun getMusicV4() = KCHttpV4.get<ResultMusic>(testUrl)

//    private suspend fun getMusicV4() = KCHttpV4.get<ResultMusic>(testUrl, requestOption = buildRequest {
//        this.cacheTime = 10
//        this.cacheMode = CacheMode.ONLY_NET
//        this.repeatNum = 10
//        this.timeout = 5000L
//    })

//    private suspend fun getMusicV4() = KCHttpV4.get<ResultMusic>(testUrl, useDefOption = true)

    private suspend fun getMusicV4() = KCHttpV4.get<ResultMusic>(testUrl, requestOption = testUrl.getDefReqOption())

    suspend fun getMusicV2() {
        val httpResult = getMusicV4()
        liveDate.postValue(httpResult)
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