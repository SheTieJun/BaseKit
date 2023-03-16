

package me.shetj.base.init

import android.content.Context
import androidx.startup.Initializer

/**
 * 抽象初始化工具
 */
abstract class ABBaseInitialize : Initializer<Unit> {

    override fun create(context: Context) {
        initContent(context)
    }

    abstract fun initContent(context: Context)

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(CommonInitialize::class.java)
    }
}