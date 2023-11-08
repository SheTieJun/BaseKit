package me.shetj.base.tip

import android.widget.Toast
import androidx.activity.ComponentActivity
import me.shetj.base.weight.AbLoadingDialog

/**
 * 新的Toast 实现方式
 */
object TipKit {
    /**
     * 标准类型的Toast
     *
     * 默认不显示icon，显示时间为Toast.LENGTH_SHORT
     */
    @JvmOverloads
    @JvmStatic
    fun normal(
        context: ComponentActivity,
        message: CharSequence,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        TipDialog.showTip(
            context,
            message,
            when (duration) {
                Toast.LENGTH_SHORT -> AbLoadingDialog.LOADING_SHORT
                else -> AbLoadingDialog.LOADING_LONG
            }
        )
    }

    /**
     * 信息类型的Toast
     *
     * 默认显示icon，显示时间为Toast.LENGTH_SHORT
     */
    @JvmOverloads
    @JvmStatic
    fun info(
        context: ComponentActivity,
        message: CharSequence,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        TipDialog.showTip(
            context,
            message,
            when (duration) {
                Toast.LENGTH_SHORT -> AbLoadingDialog.LOADING_SHORT
                else -> AbLoadingDialog.LOADING_LONG
            }
        )
    }

    /**
     * 成功类型的Toast
     *
     * 默认显示icon，显示时间为Toast.LENGTH_SHORT
     */
    @JvmOverloads
    @JvmStatic
    fun success(
        context: ComponentActivity,
        message: CharSequence,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        SimLoadingDialog.showTip(
            context,
            message,
            TipType.SUCCESS,
            when (duration) {
                Toast.LENGTH_SHORT -> AbLoadingDialog.LOADING_SHORT
                else -> AbLoadingDialog.LOADING_LONG
            }
        )
    }

    /**
     * 错误类型的toast
     *
     * 默认显示icon，显示时间为Toast.LENGTH_LONG
     */
    @JvmOverloads
    @JvmStatic
    fun error(
        context: ComponentActivity,
        message: CharSequence,
        duration: Int = Toast.LENGTH_LONG
    ) {
        SimLoadingDialog.showTip(
            context,
            message,
            TipType.ERROR,
            when (duration) {
                Toast.LENGTH_SHORT -> AbLoadingDialog.LOADING_SHORT
                else -> AbLoadingDialog.LOADING_LONG
            }
        )
    }

    /**
     * 警告类型的toast
     *
     * 默认显示icon，显示时间为Toast.LENGTH_LONG
     */
    @JvmOverloads
    @JvmStatic
    fun warn(context: ComponentActivity, message: CharSequence, duration: Int = Toast.LENGTH_LONG) {
        SimLoadingDialog.showTip(
            context,
            message,
            TipType.WARNING,
            when (duration) {
                Toast.LENGTH_SHORT -> AbLoadingDialog.LOADING_SHORT
                else -> AbLoadingDialog.LOADING_LONG
            }
        )
    }

    @JvmStatic
    fun loading(context: ComponentActivity, action: suspend () -> Unit): AbLoadingDialog {
        return SimLoadingDialog.showWithAction(context, action)
    }

    /**
     * 超时结束的
     */
    @JvmStatic
    fun loading(context: ComponentActivity, time: Long, action: () -> Unit) {
        SimLoadingDialog().showWithTimeOutAction(context, time, action)
    }
}
