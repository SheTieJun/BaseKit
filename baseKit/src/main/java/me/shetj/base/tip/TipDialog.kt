package me.shetj.base.tip

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import me.shetj.base.R
import me.shetj.base.weight.AbLoadingDialog

/**
 * 如果可以：android:configChanges="orientation|keyboardHidden|screenSize"
 */
class TipDialog : AbLoadingDialog() {

    override fun createLoading(context: Context, cancelable: Boolean, msg: CharSequence, image: Int?): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.base_dialog_tip, null)
        return AlertDialog.Builder(context, R.style.trans_dialog).apply {
            val tvMsg = view.findViewById<TextView>(R.id.tv_msg)
            tvMsg.text = msg
            setView(view)
            setCancelable(cancelable)
        }.create()
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun showTip(
            context: ComponentActivity,
            msg: CharSequence = "加载中...",
            @LoadingTipsDuration time: Long = LOADING_SHORT
        ): AbLoadingDialog {
            return TipDialog().showTip(context, false, msg = msg, null, time = time)
        }
    }
}
