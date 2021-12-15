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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import me.shetj.base.R
import me.shetj.base.weight.AbLoadingDialog

/**
 * 消息提示框
 */
class TipPopupWindow(private val mContext: AppCompatActivity) :
    PopupWindow(mContext),
    LifecycleEventObserver {
    private var tvTip: TextView? = null
    private val lazyComposite = lazy { CompositeDisposable() }
    private val mCompositeDisposable: CompositeDisposable by lazyComposite
    private var currentDuration = AbLoadingDialog.LOADING_SHORT

    init {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        animationStyle = R.style.tip_pop_anim_style
        isOutsideTouchable = false // 设置点击窗口外边窗口不消失
        isFocusable = false
        mContext.lifecycle.addObserver(this)
        initUI()
    }

    private fun initUI() {
        val rootView = View.inflate(mContext, R.layout.base_dialog_tip, null)
        tvTip = rootView.findViewById(R.id.tv_msg)
        contentView = rootView
        setOnDismissListener {
            if (lazyComposite.isInitialized()) {
                mCompositeDisposable.clear()
            }
        }
    }

    fun tipDismissStop() {
        try {
            dismiss()
        } catch (ignored: Exception) {
            // 暴力解决，可能的崩溃
        }
    }

    fun tipDismiss() {
        try {
            dismiss()
        } catch (_: Exception) {
            // 暴力解决，可能的崩溃
        }
    }

    /**
     * 展示
     * @param tipMsg 消息能让
     */
    fun showTip(tipMsg: CharSequence?, @AbLoadingDialog.LoadingTipsDuration duration: Long) {
        tvTip!!.text = tipMsg
        this.currentDuration = duration
        showAtLocation(mContext.window.decorView, Gravity.CENTER, 0, 0)
        mCompositeDisposable.add(
            AndroidSchedulers.mainThread().scheduleDirect({
                tipDismiss()
            }, duration, TimeUnit.MILLISECONDS)
        )
    }

    companion object {
        @JvmOverloads
        fun showTip(
            context: Context,
            tipMsg: CharSequence?,
            @AbLoadingDialog.LoadingTipsDuration duration: Long = AbLoadingDialog.LOADING_SHORT
        ) {
            TipPopupWindow(context as AppCompatActivity).showTip(tipMsg, duration)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        when (event) {
            Event.ON_STOP -> {
                tipDismissStop()
            }
            Event.ON_DESTROY -> {
                tipDismiss()
            }
        }
    }
}
