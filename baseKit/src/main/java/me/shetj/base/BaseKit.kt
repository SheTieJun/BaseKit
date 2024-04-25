package me.shetj.base

import android.app.Application
import android.provider.Settings
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import me.shetj.base.coroutine.DispatcherProvider
import me.shetj.base.di.getDBModule
import me.shetj.base.di.getHttpModule
import me.shetj.base.ktx.isTrue
import me.shetj.base.tools.app.AppUtils
import me.shetj.base.tools.app.Tim
import me.shetj.base.tools.app.Utils
import me.shetj.base.tools.app.webview.WebViewManager
import me.shetj.base.tools.debug.DebugFunc
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.mp.KoinPlatformTools
import timber.log.Timber

/**
 * Base kit
 * 工具初始化类
 */
@Keep
object BaseKit {
    var baseUrl: String? = null
        private set

    var TAG = "BaseKit"

    @JvmStatic
    val app: Application
        get() = Utils.app

    val isDebug = MutableLiveData(false)

    private var enableLogUI = false
    private val dnsLocalMap = HashMap<String, String>()

    fun enableLogUILife(isLogUI: Boolean) {
        this.enableLogUI = isLogUI
    }

    fun isLogUILife() = enableLogUI

    /**
     * 处理为捕捉的异常
     */
    val handler = CoroutineExceptionHandler { _, throwable ->
        Timber.tag("CoroutineException").e(throwable)
    }

    fun addDnsMap(hashMap: HashMap<String, String>) {
        dnsLocalMap.putAll(hashMap)
    }

    internal fun getDnsMap() = dnsLocalMap

    /**
     * 专门用来做不被取消的操作
     * 全局的
     */
    val applicationScope = CoroutineScope(SupervisorJob() + DispatcherProvider.main()) + handler

    /**
     * ANDROID_ID的生成规则为：签名+设备信息+设备用户
     * ANDROID_ID重置规则：设备恢复出厂设置时，ANDROID_ID将被重置
     */
    val androidID: String
        get() {
            return Settings.Secure.getString(
                app.applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }

    /**
     * 初始化
     * @param application 初始
     * @param isDebug 是否是Debug
     * @param baseUrl if not null will init http
     */
    @JvmOverloads
    @JvmStatic
    internal fun init(application: Application, isDebug: Boolean, baseUrl: String? = null) {
        this.isDebug.postValue(isDebug)
        this.baseUrl = baseUrl
        Utils.init(application)
        this.TAG = AppUtils.appName ?: "BaseKit"
        this.isDebug.observe(ProcessLifecycleOwner.get()) { t ->
            if (t) {
                DebugFunc.getInstance().initContext(application)
            }
            Tim.setLogAuto(isDebug)
        }
        startKoin {
            fragmentFactory()
            if (BaseKit.isDebug.isTrue()) {
                androidLogger(Level.ERROR)
            }
            androidContext(application)
            androidFileProperties("base.properties")
            modules(getDBModule()) //数据库
            modules(getHttpModule()) //网络
        }
        WebViewManager.startSafeBrowsing()
    }

    val SDKVersionName by lazy {
        "Version：" + KoinPlatformTools.defaultContext().get().getProperty<String>("version").toString()
    }

    @JvmStatic
    fun initKoin(modules: List<Module>) {
        loadKoinModules(modules)
    }

    fun isDebug() = isDebug.isTrue()
}
