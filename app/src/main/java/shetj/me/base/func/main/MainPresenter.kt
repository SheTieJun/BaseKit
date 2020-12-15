package shetj.me.base.func.main

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import me.shetj.base.ktx.toMessage
import me.shetj.base.mvp.BasePresenter
import me.shetj.base.mvp.IView
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.base.tools.time.CalendarReminderUtils
import me.shetj.base.tools.time.DateUtils
import org.koin.java.KoinJavaComponent.get
import shetj.me.base.bean.ResultMusic

/**
 * **@packageName：** shetj.me.base.fun<br></br>
 * **@author：** shetj<br></br>
 * **@createTime：** 2018/10/29 0029<br></br>
 * **@company：**<br></br>
 * **@email：** 375105540@qq.com<br></br>
 * **@describe**<br></br>
 */
class MainPresenter(view: IView) : BasePresenter<MainModel>(view) {

    val view2: IView = get(IView::class.java)

    fun getNightModel(): Int {
        //android:forceDarkAllowed="true"
        //?android:attr/textColorPrimary 这是一种通用型文本颜色。它在浅色主题背景下接近于黑色，在深色主题背景下接近于白色。该颜色包含一个停用状态。
        //?attr/colorControlNormal 一种通用图标颜色。该颜色包含一个停用状态。
        //主题背景属性 ?attr/colorSurface 和 ?attr/colorOnSurface）轻松获取合适的颜色
        //浅色 - MODE_NIGHT_NO
        //深色 - MODE_NIGHT_YES
        //由省电模式设置 - MODE_NIGHT_AUTO_BATTERY
        //系统默认 - MODE_NIGHT_FOLLOW_SYSTEM
        view.updateView(1.toMessage())
        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()

        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return AppCompatDelegate.MODE_NIGHT_YES
        }
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return AppCompatDelegate.MODE_NIGHT_NO
        }
        return AppCompatDelegate.MODE_NIGHT_YES
    }

    suspend fun getMusic(): ResultMusic? {
        return model.getMusic()
    }

    fun <T> getMusicByRxHttp(simpleNetCallBack: SimpleNetCallBack<T>) {
        model.getMusicByRxHttp(simpleNetCallBack)
    }

    fun addEvent(context: Context) {
        CalendarReminderUtils.addCalendarEvent(context,
                title = "这是一个测试时间",
                des = "这是测试时间描述",
                remindTime = DateUtils.str2Calendar("2020-12-16 00:00:00")!!.timeInMillis,
                endTime = null, previousTime = 5
        )
    }
}