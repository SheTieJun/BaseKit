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


package shetj.me.base

import android.content.Context
import androidx.annotation.Keep
import com.shetj.messenger.SLogMessenger
import me.shetj.base.BuildConfig
import me.shetj.base.BaseKit
import me.shetj.base.init.ABBaseInitialize
import me.shetj.base.network_coroutine.HttpKit
import me.shetj.base.tools.debug.BaseUncaughtExceptionHandler
import shetj.me.base.di_kointest.allModules
import shetj.me.base.utils.SLogMessengerTree
import timber.log.Timber


/**
 * 用start_up 代替application
 */
@Keep
class BaseInitialize : ABBaseInitialize() {

    override fun initContent(context: Context) {
        Thread.setDefaultUncaughtExceptionHandler(BaseUncaughtExceptionHandler())
        BaseKit.initKoin(allModules)
        HttpKit.debugHttp(BuildConfig.DEBUG)
        //这里需要安装另外一个demo(专门用来接收日志的),服务APP,最好开启自启动
        SLogMessenger.getInstance().bindService(context, "me.shetj.logkit.demo")
//        SLogMessenger.getInstance().autoHide(context,false)
//        SLogMessenger.getInstance().bindService(context,"me.shetj.beloved")
        Timber.plant(SLogMessengerTree())
    }

}