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

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
            context: AppCompatActivity,
            msg: CharSequence = "加载中...",
            @LoadingTipsDuration time: Long = LOADING_SHORT
        ): AbLoadingDialog {
            return TipDialog().showTip(context, false, msg = msg, null, time = time)
        }
    }
}
