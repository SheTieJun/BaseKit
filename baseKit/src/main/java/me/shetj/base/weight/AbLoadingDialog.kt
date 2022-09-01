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
package me.shetj.base.weight

import android.app.Dialog
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.LongDef
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import me.shetj.base.base.ABKtScopeComponent

/**
 *   must  【 android:configChanges="orientation|keyboardHidden|screenSize"】
 */
abstract class AbLoadingDialog : LifecycleEventObserver, ABKtScopeComponent() {

    companion object {
        const val LOADING_LONG = 1800L

        const val LOADING_SHORT = 800L
    }

    @LongDef(LOADING_LONG, LOADING_SHORT)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class LoadingTipsDuration

    private var weakReference: WeakReference<AppCompatActivity>? = null
    private var mLoadingDialog: AlertDialog? = null

    abstract fun createLoading(
        context: Context,
        cancelable: Boolean = false,
        msg: CharSequence = "加载中...",
        @DrawableRes image: Int? = null
    ): AlertDialog?

    fun showLoading(
        context: AppCompatActivity,
        cancelable: Boolean = true,
        msg: CharSequence = "加载中",
        @DrawableRes image: Int? = null
    ): AlertDialog {
        if (context.isFinishing) {
            return mLoadingDialog!!
        }
        initDialog(context, cancelable, msg, image)
        mLoadingDialog?.let {
            if (!mLoadingDialog!!.isShowing) {
                mLoadingDialog!!.show()
            }
        }
        return mLoadingDialog!!
    }

    open fun hideLoading() {
        ktScope.cancel()
        if (null != mLoadingDialog && mLoadingDialog!!.isShowing) {
            mLoadingDialog!!.dismiss()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        when (event) {
            Event.ON_DESTROY -> {
                hideLoading()
            }
            else -> {}
        }
    }

    private fun initDialog(
        context: AppCompatActivity,
        cancelable: Boolean = true,
        msg: CharSequence = "加载中",
        @DrawableRes image: Int? = null
    ) {
        if (mLoadingDialog == null || context != weakReference?.get()) {
            weakReference = WeakReference(context)
            mLoadingDialog = createLoading(context, cancelable, msg, image)?.apply {
                initSetting()
            }
            context.lifecycle.addObserver(this)
        }
    }

    private fun Dialog.initSetting() {
        setOnDismissListener {
            clean()
        }
        setOnCancelListener {
            clean()
        }
    }

    private fun clean() {
        weakReference?.get()?.lifecycle?.removeObserver(this@AbLoadingDialog)
    }

    /**
     * 协程一起使用
     * 任务结束后自定退出
     */
    inline fun showWithAction(
        context: AppCompatActivity,
        crossinline action: suspend () -> Unit
    ): AbLoadingDialog {
        showLoading(context)
        ktScope.launch {
            action()
            hideLoading()
        }
        return this
    }

    inline fun showWithTimeOutAction(
        context: AppCompatActivity,
        time: Long = LOADING_SHORT,
        crossinline action: suspend () -> Unit
    ): AbLoadingDialog {
        ktScope.launch {
            try {
                withTimeout(time) {
                    showLoading(context)
                    action.invoke()
                }
            } catch (e: TimeoutCancellationException) {
                hideLoading()
            }
        }
        return this
    }

    fun showTip(
        context: AppCompatActivity,
        cancelable: Boolean,
        msg: CharSequence = "加载中",
        @DrawableRes image: Int?,
        @LoadingTipsDuration time: Long = LOADING_SHORT
    ): AbLoadingDialog {
        showLoading(context, cancelable, msg, image)
        ktScope.launch {
            delay(time)
            hideLoading()
        }
        return this
    }
}
