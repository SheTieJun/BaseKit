/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base

import android.app.Application
import android.provider.Settings
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import me.shetj.base.base.TaskExecutor
import me.shetj.base.di.dbModule
import me.shetj.base.ktx.isTrue
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
 * Base kit
 * 工具初始化类
 */
@Keep
object BaseKit {
    var baseUrl: String? = null
        private set

    @JvmStatic
    val app: Application
        get() = Utils.app

    val isDebug = MutableLiveData(false)

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
    @DelicateCoroutinesApi
    val applicationScope = GlobalScope + SupervisorJob() + handler

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
        TaskExecutor.getInstance().executeOnMainThread {
            Utils.init(application)
            Tim.setLogAuto(isDebug)
            if (isDebug) {
                DebugFunc.getInstance().apply {
                    initContext(application)
                }
            }
            startKoin {
                fragmentFactory()
                if (BaseKit.isDebug.isTrue()) {
                    // No static method toDouble-impl
                    // androidLogger() doesn't work with Kotlin 1.6
                    androidLogger(Level.ERROR)
                }
                androidContext(application)
                androidFileProperties()
                modules(dbModule)
            }
        }
    }

    @JvmStatic
    fun initKoin(modules: List<Module>) {
        loadKoinModules(modules)
    }
}