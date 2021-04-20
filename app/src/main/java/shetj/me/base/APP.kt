package shetj.me.base

import android.app.Application
import android.util.Log
import android.util.Log.INFO
import dagger.hilt.android.HiltAndroidApp
import me.shetj.base.tools.debug.BaseUncaughtExceptionHandler

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
        Thread.setDefaultUncaughtExceptionHandler(BaseUncaughtExceptionHandler())
        Log.isLoggable("all", INFO)
    }

}