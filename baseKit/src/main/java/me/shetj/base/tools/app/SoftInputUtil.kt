package me.shetj.base.tools.app

import android.graphics.Rect
import android.view.Window

class SoftInputUtil {
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
            var inputShow = false
            rootView.decorView.getWindowVisibleDisplayFrame(rect)
            val visibleHeight: Int = rect.height()
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }

            // 根视图显示高度没有变化，可以看做软键盘显示/隐藏状态没有变化
            if (rootViewVisibleHeight == visibleHeight) {
                return@addOnGlobalLayoutListener
            }
            if (rootViewVisibleHeight - visibleHeight > 200) {
                inputShow = true
            }
            // 根视图显示高度变大超过了200，可以看做软键盘隐藏了
            if (visibleHeight - rootViewVisibleHeight > 200) {
                inputShow = false
            }
            rootViewVisibleHeight = visibleHeight
            if (isSoftInputShowing != inputShow || inputShow) {
                listener.onChanged(inputShow)
                isSoftInputShowing = inputShow
            }
        }
    }

    fun dismiss() {
        isSoftInputShowing = false
    }
}
