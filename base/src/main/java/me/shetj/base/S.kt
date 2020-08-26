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
import org.koin.core.context.loadKoinModules
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
object S {
    var baseUrl: String? = null
        private set

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
        TaskExecutor.getInstance().executeOnMainThread {
            this.isDebug = isDebug
            Utils.init(application)
            TimberUtil.setLogAuto(isDebug)
            if (isDebug) {
                DebugFunc.getInstance().apply {
                    initContext(application)
                    setRxJavaErrorHandler()
                }
            }
            startKoin {
                if (S.isDebug) {
                    androidLogger()
                }
                androidContext(application)
                androidFileProperties()
                fragmentFactory()
                //Kotlin 1.4.0 之后需要这样写    modules(modules + dbModule)
                koin.loadModules(arrayOf(dbModule).toList())
                koin.createRootScope()
            }
            baseUrl?.let {
                this.baseUrl = baseUrl
                getInstance().debug(S.isDebug)
                        .setBaseUrl(S.baseUrl)
            }
        }

    }

    @JvmStatic
    fun initKoin(modules: List<Module>) {
        loadKoinModules(modules)
    }
}
