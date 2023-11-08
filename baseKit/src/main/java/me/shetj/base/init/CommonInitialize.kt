package me.shetj.base.init

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import me.shetj.base.BaseKit
import me.shetj.base.BuildConfig
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.network_coroutine.HttpKit

/**
 * 初始化PhotoLife
 */

@Keep
class CommonInitialize : Initializer<Unit> {

    override fun create(context: Context) {
        BaseKit.init(context.applicationContext as Application, BuildConfig.DEBUG)
        NetWorkLiveDate.getInstance().start(context)
        HttpKit.loadCookie()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}
