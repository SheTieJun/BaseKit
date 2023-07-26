package me.shetj.base.init

import android.content.Context
import me.shetj.base.ktx.logD
import me.shetj.base.tools.app.MD3ThemeKit

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2023/7/26<br>
 */
class MD3ThemeInitialize:ABBaseInitialize() {
    override fun initContent(context: Context) {
        "MD3ThemeInitialize".logD()
//        MD3ThemeKit.addTheme(R.style.BaseTheme_MD3, "默认主题-跟随系统")
        MD3ThemeKit.startInit(context)
    }
}