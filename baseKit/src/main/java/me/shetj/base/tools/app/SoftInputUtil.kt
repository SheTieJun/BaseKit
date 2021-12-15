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


package me.shetj.base.tools.app

import android.graphics.Rect
import android.view.Window
import androidx.core.view.ViewCompat

/**
 * 只能用来判断关闭键盘
 */
class SoftInputUtil {
    private var softInputHeightChanged = false
    private var listener: ISoftInputChanged? = null
    private var isSoftInputShowing = false
    private var rootViewVisibleHeight = 0
    private val rect = Rect()

    interface ISoftInputChanged {
        fun onChanged(isSoftInputShow: Boolean)
    }

    fun attachSoftInput(rootView: Window?, listener: ISoftInputChanged?) {
        if (listener == null || rootView == null) return
        this.listener = listener

        rootView.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val isSoftInputShow: Boolean
            rootView.decorView.getWindowVisibleDisplayFrame(rect)
            val visibleHeight: Int = rect.height()
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }

            //根视图显示高度没有变化，可以看做软键盘显示/隐藏状态没有变化
            if (rootViewVisibleHeight == visibleHeight) {
                return@addOnGlobalLayoutListener
            }

            // 根视图显示高度变小超过200，可以看做软键盘显示了
            if (rootViewVisibleHeight - visibleHeight > 200) {
                isSoftInputShow = true
                rootViewVisibleHeight = visibleHeight
                if (isSoftInputShowing != isSoftInputShow || isSoftInputShow && softInputHeightChanged) {
                    listener.onChanged(isSoftInputShow)
                    isSoftInputShowing = isSoftInputShow
                }
                return@addOnGlobalLayoutListener
            }

            // 根视图显示高度变大超过了200，可以看做软键盘隐藏了
            if (visibleHeight - rootViewVisibleHeight > 200) {
                isSoftInputShow = false
                rootViewVisibleHeight = visibleHeight
                if (isSoftInputShowing != isSoftInputShow || isSoftInputShow && softInputHeightChanged) {
                    listener.onChanged(isSoftInputShow)
                    isSoftInputShowing = isSoftInputShow
                }
                return@addOnGlobalLayoutListener
            }
        }
    }

    fun dismiss() {
        isSoftInputShowing = false
    }
}