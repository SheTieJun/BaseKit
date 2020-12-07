package me.shetj.base.tip

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import me.shetj.base.R
import me.shetj.base.weight.AbLoadingDialog


/**
 * 如果可以：android:configChanges="orientation|keyboardHidden|screenSize"
 */
class TipDialog : AbLoadingDialog() {


    override fun createLoading(context: Context, cancelable: Boolean, msg: CharSequence, image: Int?): Dialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.base_dialog_tip, null)
        return Dialog(context, R.style.trans_dialog).apply {
            val tvMsg = view.findViewById<TextView>(R.id.tv_msg)
            tvMsg.text = msg
            setContentView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
            setCancelable(cancelable)
        }
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun showTip(context: Context, msg: CharSequence = "加载中...",  @LoadingTipsDuration time: Long = LOADING_SHORT): AbLoadingDialog {
            return TipDialog().showTip(context, false, msg = msg,null,time = time)
        }
    }

}