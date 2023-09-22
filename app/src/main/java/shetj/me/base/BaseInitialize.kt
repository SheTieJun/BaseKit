

package shetj.me.base

import android.content.Context
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatDelegate
import com.shetj.messenger.SLogMessenger
import me.shetj.base.BaseKit
import me.shetj.base.init.ABBaseInitialize
import me.shetj.base.ktx.logD
import me.shetj.base.network_coroutine.HttpKit
import me.shetj.base.tools.app.MDThemeKit
import me.shetj.base.tools.app.MDThemeKit.ThemeBean
import me.shetj.base.tools.app.Tim
import me.shetj.base.tools.debug.BaseUncaughtExceptionHandler
import shetj.me.base.utils.SLogMessengerTree
import timber.log.Timber


/**
 * 用start_up 代替application
 */
@Keep
class BaseInitialize : ABBaseInitialize() {

    override fun initContent(context: Context) {
        Thread.setDefaultUncaughtExceptionHandler(BaseUncaughtExceptionHandler())
        HttpKit.debugHttp(true)
        BaseKit.enableLogUILife(isLogUI = true) //关闭界面生命周期的日志
        Tim.setLogAuto(true)
        //这里需要安装另外一个demo(专门用来接收日志的),服务APP,最好开启自启动
//        SLogMessenger.getInstance().bindService(context, "me.shetj.logkit.demo")
        BaseKit.versionName.logD("")
//        SLogMessenger.getInstance().autoHide(context,false)
        SLogMessenger.getInstance().bindService(context,"me.shetj.beloved")
        Timber.plant(SLogMessengerTree())
        MDThemeKit.startInit(context, listOf(
            ThemeBean(R.style.BaseTheme_MD3, "默认主题-跟随系统"),
            ThemeBean(R.style.BaseTheme_MD3, "默认主题-黑夜", AppCompatDelegate.MODE_NIGHT_YES),
            ThemeBean(R.style.BaseTheme_MD3, "默认主题-日间", AppCompatDelegate.MODE_NIGHT_NO),
            ThemeBean(R.style.Base_Theme_LF_Green, "春色宜人(GREEN)", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY),
            ThemeBean(R.style.Base_Theme_LF_RED, "红红火火(RED)", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY),
        ))
    }

}