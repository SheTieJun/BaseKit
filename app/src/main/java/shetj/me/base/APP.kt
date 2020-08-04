package shetj.me.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.didichuxing.doraemonkit.DoraemonKit
import dagger.hilt.android.HiltAndroidApp
import me.shetj.base.S
import me.shetj.base.S.initKoin
import shetj.me.base.di_kointest.allModules

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
        S.initKoin(this, allModules)
        DoraemonKit.disableUpload();
        DoraemonKit.install(this,"a0b7c73af7016fd6f1e94cdaecc5faa5");
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}