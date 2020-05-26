package me.shetj.base

import android.app.Application
import androidx.annotation.Keep
import me.shetj.base.network.RxHttp.Companion.getInstance
import me.shetj.base.tools.app.TimberUtil
import me.shetj.base.tools.app.Utils
import me.shetj.base.tools.debug.DebugFunc

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

    /**
     * 初始化
     * @param application 初始
     * @param isDebug 是否是Debug
     * @param baseUrl if not null will init http
     */
    @JvmOverloads
    @JvmStatic
    fun init(application: Application, isDebug: Boolean, baseUrl: String?) {
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
    }
}
