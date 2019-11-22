package me.shetj.base.tools.app

import android.app.Activity
import android.graphics.Rect
import android.view.View

/**
 * 键盘监听类
 */
class SoftKeyBoardListener(activity: Activity) {

    private var onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener? = null

    init {
          activity.window.decorView.findViewById<View>(android.R.id.content).apply {
              viewTreeObserver.addOnGlobalLayoutListener {
                  val mKeyboardUp = isKeyboardShown(this)
                  if (mKeyboardUp) {
                      onSoftKeyBoardChangeListener?.keyBoardShow()
                  } else {
                      onSoftKeyBoardChangeListener?.keyBoardHide()
                  }
              }
          }
    }

    fun setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener
    }

    private fun isKeyboardShown(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        val heightDiff = rootView.bottom - r.bottom
        return heightDiff > softKeyboardHeight * dm.density
    }

    interface OnSoftKeyBoardChangeListener {
        fun keyBoardShow()
        fun keyBoardHide()
    }
}