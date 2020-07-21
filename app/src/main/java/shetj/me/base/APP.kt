package shetj.me.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import me.shetj.base.s.init
import me.shetj.base.s.initKoin
import shetj.me.base.kointest.allModules

/**
 * **@packageName：** com.ebu.master<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/2/26<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */
class APP : Application() {
    override fun onCreate() {
        super.onCreate()
        init(this, BuildConfig.LOG_DEBUG, "https://xxxx.com")
        initKoin(this, allModules)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}