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

import me.shetj.base.mvp.BaseModel
import me.shetj.base.network.RxHttp
import me.shetj.base.network_coroutine.cache.CacheMode
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.base.network_coroutine.KCHttpV3
import shetj.me.base.bean.ResultMusic

/**
 * * @packageName：** shetj.me.base.fun<br></br>
 * * @author：** shetj<br></br>
 * * @createTime：** 2018/10/29 0029<br></br>
 * * @company：**<br></br>
 * * @email：** 375105540@qq.com<br></br>
 * * @describe**<br></br>
 */
class MainModel : BaseModel() {


    private val testUrl =
        "https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json"

    suspend fun getMusicV2() = KCHttpV3.get<ResultMusic>(testUrl,
        option = {
            this.cacheKey = "testUrl"
            this.cacheTime = 10
            this.cacheMode = CacheMode.ONLY_NET
            this.repeatNum = 10
            this.timeout = 5000L
        })


    fun <T> getMusicByRxHttp(simpleNetCallBack: SimpleNetCallBack<T>) =
        RxHttp.get(testUrl).execute(simpleNetCallBack)
}