package me.shetj.base.mvp

import android.os.Message
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity

/**
 *
 * @author shetj
 * @date 16/4/22
 */
@Keep
interface IView {
    /**
     * 返回当前的activity
     * @return RxAppCompatActivity
     */
    val rxContext: AppCompatActivity

    /**
     * (唯一更新view的方法)
     * 在每次接到信息时,把信息显示view上,---更新view
     * @param message 接收到的信息
     */
    fun updateView(message: Message)
}
