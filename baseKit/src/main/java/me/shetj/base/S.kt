package me.shetj.base

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import androidx.annotation.Keep
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import me.shetj.base.base.TaskExecutor
import me.shetj.base.di.dbModule
import me.shetj.base.network.RxHttp.Companion.getInstance
import me.shetj.base.tools.app.Tim
import me.shetj.base.tools.app.Utils
import me.shetj.base.tools.debug.DebugFunc
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import timber.log.Timber

/**
 * **@packageName：** me.shetj.base<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/2/24<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br>super</br>
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
     * 专门用来做不被取消的操作
     */
    val applicationScope = GlobalScope

    /**
     * 处理为捕捉的异常
     */
    val handler = CoroutineExceptionHandler { _, throwable ->
        Timber.tag("CoroutineException").e(throwable)
    }

    /**
     * ANDROID_ID的生成规则为：签名+设备信息+设备用户
     * ANDROID_ID重置规则：设备恢复出厂设置时，ANDROID_ID将被重置
     */
    val androidID: String
        @SuppressLint("HardwareIds")
        get() {
            return Settings.Secure.getString(app.applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        }


    /**
     * 初始化
     * @param application 初始
     * @param isDebug 是否是Debug
     * @param baseUrl if not null will init http
     */
    @JvmOverloads
    @JvmStatic
    fun init(application: Application, isDebug: Boolean, baseUrl: String? = null) {
        this.isDebug = isDebug
        this.baseUrl = baseUrl
        TaskExecutor.getInstance().executeOnMainThread {
            Utils.init(application)
            Tim.setLogAuto(isDebug)
            if (isDebug) {
                DebugFunc.getInstance().apply {
                    initContext(application)
                    setRxJavaErrorHandler()
                }
            }
            startKoin {
                fragmentFactory()
                if (S.isDebug) {
                    androidLogger(Level.ERROR)
                }
                androidContext(application)
                androidFileProperties()
                modules(dbModule)
            }
            baseUrl?.let {
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
