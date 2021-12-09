package me.shetj.base.tools.app

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

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
            if (null != v && isShouldHideInput(v, ev)) {
                hideSoftKeyboard(activity)
            }
            if (null == v){
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
            v.getHitRect(rect)
            return !rect.contains(event.x.toInt(), event.y.toInt())
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
            activity.window.hideSoftKeyboard()
        }

        @JvmStatic
        fun showSoftKeyboard(activity: Activity) {
            activity.window.showSoftKeyboard()
        }

        /**
         * ViewCompat.setOnApplyWindowInsetsListener(bottomButton) { view, insets ->
         *      val sysWindow = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
         *      view.translationY = -sysWindow.bottom.toFloat()
         *      insets
         * }
         */
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


        @JvmStatic
        fun Window.showSoftKeyboard() {
            ViewCompat.getWindowInsetsController(decorView)
                ?.show(WindowInsetsCompat.Type.ime())
        }

        @JvmStatic
        fun Window.hideSoftKeyboard() {
            ViewCompat.getWindowInsetsController(decorView)
                ?.hide(WindowInsetsCompat.Type.ime())
        }

        /**
         * 隐藏和显示切换
         */
        @JvmStatic
        fun isVisibleKeyBoard(window: Window): Boolean? {
            val insets = ViewCompat.getRootWindowInsets(window.decorView)
            return insets?.isVisible(WindowInsetsCompat.Type.ime())
        }
    }
}