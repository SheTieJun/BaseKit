package me.shetj.base.tip

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import me.shetj.base.R
import me.shetj.base.ktx.setDrawables
import me.shetj.base.weight.AbLoadingDialog

/**
 * android:configChanges="orientation|keyboardHidden|screenSize"
 */
class SimLoadingDialog : AbLoadingDialog() {

    override fun createLoading(
        context: Context,
        cancelable: Boolean,
        msg: CharSequence,
        image: Int?
    ): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
        return AlertDialog.Builder(context, R.style.trans_dialog).apply {
            val tvMsg = view.findViewById<TextView>(R.id.tv_msg)
            tvMsg.text = msg
            image?.let {
                tvMsg.setDrawables(it, Gravity.TOP)
                view.findViewById<ProgressBar>(R.id.progress).isVisible = false
            }
            setView(view)
            setCancelable(cancelable)
        }.create().apply {
            setCanceledOnTouchOutside(false)
        }
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun showTip(
            context: ComponentActivity,
            msg: CharSequence = "加载中...",
            tip: TipType = TipType.INFO,
            @LoadingTipsDuration time: Long = LOADING_SHORT
        ): AbLoadingDialog {
            val image = when (tip) {
                TipType.SUCCESS -> R.drawable.icon_tip_success
                TipType.DEFAULT -> R.drawable.icon_tip_success
                TipType.WARNING -> R.drawable.icon_tip_warn
                TipType.INFO -> R.drawable.icon_tip_success
                TipType.ERROR -> R.drawable.icon_tip_error
            }
            return SimLoadingDialog().showTip(context, false, msg, image, time)
        }

        /**
         * 和协程一起使用
         */
        inline fun showWithAction(
            context: ComponentActivity,
            crossinline action: suspend () -> Unit
        ): AbLoadingDialog {
            return SimLoadingDialog().showWithAction(context, action)
        }

        @JvmStatic
        fun showNoAction(context: AppCompatActivity, cancelable: Boolean = true): Dialog? {
            return SimLoadingDialog().showLoading(context, cancelable)
        }
    }
}
