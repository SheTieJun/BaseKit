package me.shetj.base

import android.app.Application
import androidx.annotation.Keep

import com.bumptech.glide.request.target.ViewTarget

import me.shetj.base.http.easyhttp.EasyHttpUtils
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

    val app: Application
        get() = Utils.app

    /**
     * 初始化
     * @param application 初始
     * @param isDebug 是否是Debug
     * @param baseUrl http的baseUrl
     */
    fun init(application: Application, isDebug: Boolean, baseUrl: String, version: Int) {
        EasyHttpUtils.init(application, isDebug, baseUrl, version)
        Utils.init(application)
        ViewTarget.setTagId(R.id.base_glide_tag)
        TimberUtil.setLogAuto(isDebug)
    }


}
