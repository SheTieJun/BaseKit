package me.shetj.base

import android.app.Application
import androidx.annotation.Keep

import me.shetj.base.tools.app.TimberUtil
import me.shetj.base.tools.app.Utils

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

    var isDebug  = true
    /**
     * 初始化
     * @param application 初始
     * @param isDebug 是否是Debug
     */
    @JvmStatic
    fun init(application: Application, isDebug: Boolean) {
        Utils.init(application)
        TimberUtil.setLogAuto(isDebug)
        this.isDebug = isDebug
    }
}
