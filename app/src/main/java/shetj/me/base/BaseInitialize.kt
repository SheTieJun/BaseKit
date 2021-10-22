package shetj.me.base

import android.content.Context
import androidx.startup.Initializer
import me.shetj.base.S
import me.shetj.base.init.CommonInitialize
import me.shetj.base.network.RxHttp
import me.shetj.base.tools.app.Tim
import me.shetj.base.tools.debug.BaseUncaughtExceptionHandler
import shetj.me.base.di_kointest.allModules


/**
 * 用start_up 代替application
 */
class BaseInitialize:Initializer<Unit> {

    override fun create(context: Context) {
        Thread.setDefaultUncaughtExceptionHandler(BaseUncaughtExceptionHandler())
        S.initKoin(allModules)
        RxHttp.getInstance().setBaseUrl("https://me.shetj.com").debug(true)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return  mutableListOf(CommonInitialize::class.java)
    }
}