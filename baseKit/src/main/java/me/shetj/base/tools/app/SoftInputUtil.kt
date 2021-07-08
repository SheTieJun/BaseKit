package me.shetj.base.tools.app

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager

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

    companion object {

        fun showSoftInput(view: View?) {
            if (view == null) return
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
                view,
                0
            )
        }

        fun hideSoftInput(view: View?) {
            if (view == null) return
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
                view.windowToken,
                0
            )
        }
    }
}