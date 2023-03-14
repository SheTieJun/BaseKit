

package shetj.me.base

import android.content.Context
import androidx.annotation.Keep
import com.shetj.messenger.SLogMessenger
import me.shetj.base.BuildConfig
import me.shetj.base.BaseKit
import me.shetj.base.init.ABBaseInitialize
import me.shetj.base.network_coroutine.HttpKit
import me.shetj.base.tools.app.LanguageKit
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
//        Timber.plant(SLogMessengerTree())
    }

}