package me.shetj.base.tools.app

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView

@Keep
class HideUtil private constructor(activity: Activity, private var content: ViewGroup?) {

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

    private fun dispatchTouchEvent(mActivity: Activity, ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = mActivity.currentFocus
            if (null != v && isShouldHideInput(v, ev)) {
                hideSoftInput(mActivity, v.windowToken)
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

    /**
     * @param mActivity
     * @param token
     */
    private fun hideSoftInput(mActivity: Activity, token: IBinder?) {
        if (token != null) {
            val im = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    companion object {

        /**
         * Initialization method
         *
         * @param activity
         */
        @JvmStatic
        fun init(activity: Activity) {
            HideUtil(activity, null)
        }

        /**
         * Can pass the outer layout
         *
         * @param activity
         * @param content
         */
        @JvmStatic
        fun init(activity: Activity, content: ViewGroup) {
            HideUtil(activity, content)
        }

        /**
         * Forced hidden keyboard
         * @param activity
         */
        @JvmStatic
        fun hideSoftKeyboard(activity: Activity) {
            val view = activity.currentFocus
            if (null != view) {
                val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }
}