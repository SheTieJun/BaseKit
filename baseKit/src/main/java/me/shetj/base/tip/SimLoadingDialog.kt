/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.tip

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
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
            context: AppCompatActivity,
            msg: CharSequence = "加载中...",
            tip: Tip = Tip.INFO,
            @LoadingTipsDuration time: Long = LOADING_SHORT
        ): AbLoadingDialog {
            val image = when (tip) {
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
        inline fun showWithAction(
            context: AppCompatActivity,
            crossinline action: suspend () -> Unit
        ): AbLoadingDialog {
            return SimLoadingDialog().showWithAction(context, action)
        }

        @JvmStatic
        fun showNoAction(context: AppCompatActivity, cancelable: Boolean = true): Dialog {
            return SimLoadingDialog().showLoading(context, cancelable)
        }
    }
}
