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

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import me.shetj.base.ktx.windowInsetsController

/**
 * @author shetj
 * * update 2021年12月10日:
 *     * 点击空白区间会关闭键盘，同时会去掉获取焦点的editText
 *     * 当没有焦点view的时候，点击空白也会关闭键盘
 */
@Keep
class KeyboardUtil private constructor(activity: Activity, private var content: ViewGroup?) {

    init {
        if (content == null) {
            content = activity.findViewById(android.R.id.content)
        }
        getScrollView(content, activity)
        content!!.setOnTouchListener { _, motionEvent ->
            dispatchTouchEvent(activity, motionEvent)
            false
        }
    }

    private fun getScrollView(viewGroup: ViewGroup?, activity: Activity) {
        if (null == viewGroup) {
            return
        }
        val count = viewGroup.childCount
        for (i in 0 until count) {
            val view = viewGroup.getChildAt(i)
            when (view) {
                is ScrollView -> view.setOnTouchListener { _, motionEvent ->
                    dispatchTouchEvent(activity, motionEvent)
                    false
                }
                is AbsListView -> view.setOnTouchListener { _, motionEvent ->
                    dispatchTouchEvent(activity, motionEvent)
                    false
                }
                is RecyclerView -> view.setOnTouchListener { _, motionEvent ->

                    dispatchTouchEvent(activity, motionEvent)
                    false
                }
                is ViewGroup -> this.getScrollView(view, activity)
            }

            if (view.isClickable && view is TextView && view !is EditText) {
                view.setOnTouchListener { _, motionEvent ->
                    dispatchTouchEvent(activity, motionEvent)
                    false
                }
            }
        }
    }

    private fun dispatchTouchEvent(activity: Activity, ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = activity.currentFocus
            if (null == v || (isShouldHideInput(v, ev))) {
                v?.clearFocus()
                hideSoftKeyboard(activity)
            }
        }
        return false
    }

    /**
     * @param v
     * @param event
     * @return
     */
    private fun isShouldHideInput(v: View, event: MotionEvent): Boolean {
        if (v is EditText) {
            val rect = Rect()
            v.getGlobalVisibleRect(rect)
            return !rect.contains(event.rawX.toInt(), event.rawY.toInt())
        }
        return true
    }

    companion object {

        /**
         * Initialization method
         *
         * @param activity
         */
        @JvmStatic
        fun init(activity: Activity) {
            KeyboardUtil(activity, null)
        }

        /**
         * Can pass the outer layout
         *
         * @param activity
         * @param content
         */
        @JvmStatic
        fun init(activity: Activity, content: ViewGroup) {
            KeyboardUtil(activity, content)
        }

        /**
         * Forced hidden keyboard
         * @param activity
         */
        @JvmStatic
        fun hideSoftKeyboard(activity: Activity) {
            activity.windowInsetsController.hide(WindowInsetsCompat.Type.ime())
        }

        @JvmStatic
        fun showSoftKeyboard(activity: Activity) {
            activity.windowInsetsController.show(WindowInsetsCompat.Type.ime())
        }

        @JvmStatic
        fun focusEditShowKeyBoard(editText: EditText) {
            editText.isEnabled = true
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.requestFocus()
            val inputManager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            editText.setSelection(editText.text.length)
            inputManager.showSoftInput(editText, 0)
        }

        @JvmStatic
        fun isShowSoftKeyBoard(activity: Activity, isShow: Boolean = true) {
            if (isShow) {
                showSoftKeyboard(activity)
            } else {
                hideSoftKeyboard(activity)
            }
        }

        fun addKeyBordHeightChangeCallBack(view: View, onAction: (height: Int) -> Unit) {
            var posBottom: Int
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
                val cb = object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                    override fun onProgress(
                        insets: WindowInsets,
                        animations: MutableList<WindowInsetsAnimation>
                    ): WindowInsets {
                        posBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom +
                            insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                        onAction.invoke(posBottom)
                        return insets
                    }
                }
                view.setWindowInsetsAnimationCallback(cb)
            } else {
                ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
                    posBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom +
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                    onAction.invoke(posBottom)
                    insets
                }
            }
        }
    }
}
