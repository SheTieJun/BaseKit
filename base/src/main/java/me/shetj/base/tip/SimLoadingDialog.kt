package me.shetj.base.tip

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import io.reactivex.rxjava3.disposables.Disposable
import me.shetj.base.R
import me.shetj.base.ktx.setDrawables
import me.shetj.base.weight.AbLoadingDialog


/**
 * android:configChanges="orientation|keyboardHidden|screenSize"
 */
class SimLoadingDialog : AbLoadingDialog() {

    override fun createLoading(context: Context, cancelable: Boolean, msg: CharSequence, image: Int?): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
        return  AlertDialog.Builder(context,R.style.trans_dialog).apply {
            val tvMsg = view.findViewById<TextView>(R.id.tv_msg)
            tvMsg.text = msg
            image?.let {
                tvMsg.setDrawables(it, Gravity.TOP)
                view.findViewById<ProgressBar>(R.id.progress).isVisible = false
            }
            setView(view)
            setCancelable(cancelable)
        }.create()
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun showTip(context: Context, msg: CharSequence = "加载中...", tip: Tip = Tip.INFO, @LoadingTipsDuration time: Long = LOADING_SHORT): AbLoadingDialog {
            val image = when(tip){
                Tip.SUCCESS -> R.drawable.icon_tip_success
                Tip.DEFAULT -> R.drawable.icon_tip_success
                Tip.WARNING -> R.drawable.icon_tip_warn
                Tip.INFO -> R.drawable.icon_tip_success
                Tip.ERROR -> R.drawable.icon_tip_error
            }
            return SimLoadingDialog().showTip(context, false, msg, image, time)
        }
        /**
         * 和协程一起使用
         */
        inline fun showWithAction(context: Context, crossinline action: suspend () -> Unit): AbLoadingDialog {
            return SimLoadingDialog().showWithAction(context, action)
        }

        /**
         * 和RxJava 一起使用
         */
        fun showWithRxAction(context: Context, action: () -> Disposable): AbLoadingDialog {
            return SimLoadingDialog().showWithRxAction(context, action)
        }

        /**
         * 和RxJava 一起使用
         */
        fun showWithDisposable(context: Context, disposable: Disposable): AbLoadingDialog {
            return SimLoadingDialog().showWithDisposable(context, disposable)
        }

        @JvmStatic
        fun showNoAction(context: Context, cancelable: Boolean = true): Dialog {
            return SimLoadingDialog().showLoading(context, cancelable)
        }
    }


}