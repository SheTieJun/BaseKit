package me.shetj.base.init

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import me.shetj.base.BuildConfig
import me.shetj.base.S


/**
 * 初始化PhotoLife
 */
@Keep
class CommonInitialize:Initializer<Unit> {

    override fun create(context: Context) {
        S.init(context.applicationContext as Application, BuildConfig.DEBUG)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return  mutableListOf()
    }
}