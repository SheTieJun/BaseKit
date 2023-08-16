package me.shetj.base

import android.app.Application
import android.provider.Settings
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import me.shetj.base.base.TaskExecutor
import me.shetj.base.di.getDBModule
import me.shetj.base.di.getHttpModule
import me.shetj.base.ktx.isTrue
import me.shetj.base.tools.app.AppUtils
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

    private val isDebug = MutableLiveData(false)

    private var dnsLocalMap = HashMap<String, String>()

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
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + Dispatchers.Main.immediate) + handler

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
        TaskExecutor.executeOnMain {
            this.TAG = AppUtils.appName ?: "BaseKit"
            Tim.setLogAuto(isDebug)
            if (isDebug) {
                DebugFunc.getInstance().apply {
                    initContext(application)
                }
            }
            startKoin {
                fragmentFactory()
                if (BaseKit.isDebug.isTrue()) {
                    androidLogger(Level.ERROR)
                }
                androidContext(application)
                androidFileProperties("base.properties")
                modules(getDBModule())
                modules(getHttpModule())
            }
        }
    }

    val versionName by lazy { "Version：" + KoinPlatformTools.defaultContext().get().getProperty("version") }

    @JvmStatic
    fun initKoin(modules: List<Module>) {
        loadKoinModules(modules)
    }

    fun isDebug() = isDebug.isTrue()

}
