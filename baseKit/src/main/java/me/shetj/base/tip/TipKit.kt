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

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.Disposable
import me.shetj.base.weight.AbLoadingDialog

/**
 * 新的Toast 实现方式
 */
object TipKit {
    /**
     * 标准类型的taost
     *
     * 默认不显示icon，显示时间为Toast.LENGTH_SHORT
     */
    @JvmOverloads
    @JvmStatic
    fun normal(
        context: AppCompatActivity,
        message: CharSequence,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        TipDialog.showTip(
            context, message,
            when (duration) {
                Toast.LENGTH_SHORT -> AbLoadingDialog.LOADING_SHORT
                else -> AbLoadingDialog.LOADING_LONG
            }
        )
    }

    /**
     * 信息类型的taost
     *
     * 默认显示icon，显示时间为Toast.LENGTH_SHORT
     */
    @JvmOverloads
    @JvmStatic
    fun info(
        context: AppCompatActivity,
        message: CharSequence,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        TipDialog.showTip(
            context, message,
            when (duration) {
                Toast.LENGTH_SHORT -> AbLoadingDialog.LOADING_SHORT
                else -> AbLoadingDialog.LOADING_LONG
            }
        )
    }

    /**
     * 成功类型的taost
     *
     * 默认显示icon，显示时间为Toast.LENGTH_SHORT
     */
    @JvmOverloads
    @JvmStatic
    fun success(
        context: AppCompatActivity,
        message: CharSequence,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        SimLoadingDialog.showTip(
            context, message, Tip.SUCCESS,
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
        context: AppCompatActivity,
        message: CharSequence,
        duration: Int = Toast.LENGTH_LONG
    ) {
        SimLoadingDialog.showTip(
            context, message, Tip.ERROR,
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
    fun warn(context: AppCompatActivity, message: CharSequence, duration: Int = Toast.LENGTH_LONG) {
        SimLoadingDialog.showTip(
            context, message, Tip.WARNING,
            when (duration) {
                Toast.LENGTH_SHORT -> AbLoadingDialog.LOADING_SHORT
                else -> AbLoadingDialog.LOADING_LONG
            }
        )
    }

    @JvmStatic
    fun loading(context: AppCompatActivity, action: suspend () -> Unit): AbLoadingDialog {
        return SimLoadingDialog.showWithAction(context, action)
    }

    @JvmStatic
    fun loadingRx(context: AppCompatActivity, action: () -> Disposable): AbLoadingDialog {
        return SimLoadingDialog.showWithRxAction(context, action)
    }

    /**
     * 超时结束的
     */
    @JvmStatic
    fun loading(context: AppCompatActivity, time: Long, action: () -> Unit) {
        SimLoadingDialog().showWithTimeOutAction(context, time, action)
    }
}
