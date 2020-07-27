package me.shetj.base

import android.app.Application
import androidx.annotation.Keep
import me.shetj.base.base.TaskExecutor
import me.shetj.base.di.dbModule
import me.shetj.base.network.RxHttp.Companion.getInstance
import me.shetj.base.tools.app.TimberUtil
import me.shetj.base.tools.app.Utils
import me.shetj.base.tools.debug.DebugFunc
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * **@packageName：** me.shetj.base<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/2/24<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */

@Keep
object s {
    @JvmStatic
    val app: Application
        get() = Utils.app

    var isDebug = true
        private set

    /**
     * 初始化
     * @param application 初始
     * @param isDebug 是否是Debug
     * @param baseUrl if not null will init http
     */
    @JvmOverloads
    @JvmStatic
    fun init(application: Application, isDebug: Boolean, baseUrl: String? = null) {
        TaskExecutor.getInstance().executeOnMainThread(Runnable {
            Utils.init(application)
            TimberUtil.setLogAuto(isDebug)
            if (isDebug) {
                DebugFunc.getInstance().apply {
                    initContext(application)
                    setRxJavaErrorHandler()
                }
            }
            this.isDebug = isDebug
            baseUrl?.let {
                getInstance().debug(isDebug)
                        .setBaseUrl(baseUrl)
            }
        })

    }

    @JvmStatic
    fun initKoin(application: Application,modules: List<Module>){
        startKoin {
            if (isDebug) {
                androidLogger()
            }
            androidContext(application)
            androidFileProperties()
            fragmentFactory()
            modules(  modules+dbModule)
        }
    }
}
