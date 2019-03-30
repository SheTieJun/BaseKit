package me.shetj.base.base

import android.os.Message
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity

import androidx.annotation.Keep


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
    val rxContext: RxAppCompatActivity

    /**
     * 显示加载
     * @param msg 展示的信息
     */
    fun showLoading(msg: String)

    /**
     * 隐藏加载
     */
    fun hideLoading()

    /**
     * 显示信息
     * @param message toast信息
     */
    fun showMessage(message: String)

    /**
     * (唯一更新view的方法)
     * 在每次接到信息时,把信息显示view上,---更新view
     * @param message 接收到的信息
     */
    fun updateView(message: Message)

}
