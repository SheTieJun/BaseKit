package shetj.me.base

import android.app.Application
import android.util.Log
import android.util.Log.INFO
import dagger.hilt.android.HiltAndroidApp
import me.shetj.base.S
import me.shetj.base.S.initKoin
import shetj.me.base.di_kointest.allModules
import shetj.me.base.test.UncaughtExceptionHandler

/**
 * **@packageName：** com.ebu.master<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/2/26<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */
@HiltAndroidApp
class APP : Application() {
    override fun onCreate() {
        super.onCreate()
        S.init(this, true, "https://xxxx.com")
        initKoin(allModules)
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler())
        Log.isLoggable("all", INFO)
    }

}