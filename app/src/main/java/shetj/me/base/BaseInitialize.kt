package shetj.me.base

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import me.shetj.base.S
import shetj.me.base.di_kointest.allModules


class BaseInitialize:Initializer<Unit> {

    override fun create(context: Context) {
        S.init(context.applicationContext as Application, BuildConfig.DEBUG, "https://xxxx.com")
        S.initKoin(allModules)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return  mutableListOf()
    }
}