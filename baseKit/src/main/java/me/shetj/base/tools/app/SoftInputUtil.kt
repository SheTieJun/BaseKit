package me.shetj.base.tools.app

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * val softInputUtil =  SoftInputUtil()
 * softInputUtil.attachSoftInput(editText2, new SoftInputUtil.ISoftInputChanged() {
 *        @Override
 *       public void onChanged(boolean isSoftInputShow, int softInputHeight, int viewOffset) {
 *           if (isSoftInputShow) {
 *           editText2.setTranslationY(et2.getTranslationY() - viewOffset)
 *            } else {
 *            editText2.setTranslationY(0);
 *            }
 *        }
 *    });
 */

class SoftInputUtil {
    private var softInputHeight = 0
    private var softInputHeightChanged = false
    private var isNavigationBarShow = false
    private var navigationHeight = 0
    private var anyView: View? = null
    private var listener: ISoftInputChanged? = null
    private var isSoftInputShowing = false

    interface ISoftInputChanged {
        fun onChanged(isSoftInputShow: Boolean, softInputHeight: Int, viewOffset: Int)
    }

    fun attachSoftInput(anyView: View?, listener: ISoftInputChanged) {
        if (anyView == null) return

        //根View
        val rootView: View = anyView.rootView ?: return
        navigationHeight = getNavigationBarHeight(anyView.context)

        //anyView为需要调整高度的View，理论上来说可以是任意的View
        this.anyView = anyView
        this.listener = listener
        rootView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            //对于Activity来说，该高度即为屏幕高度
            val rootHeight: Int = rootView.height
            val rect = Rect()
            //获取当前可见部分，默认可见部分是除了状态栏和导航栏剩下的部分
            rootView.getWindowVisibleDisplayFrame(rect)
            if (rootHeight - rect.bottom === navigationHeight) {
                //如果可见部分底部与屏幕底部刚好相差导航栏的高度，则认为有导航栏
                isNavigationBarShow = true
            } else if (rootHeight - rect.bottom === 0) {
                //如果可见部分底部与屏幕底部平齐，说明没有导航栏
                isNavigationBarShow = false
            }

            //cal softInput height
            var isSoftInputShow = false
            var softInputHeight = 0
            //如果有导航栏，则要去除导航栏的高度
            val mutableHeight = if (isNavigationBarShow) navigationHeight else 0
            if (rootHeight - mutableHeight > rect.bottom) {
                //除去导航栏高度后，可见区域仍然小于屏幕高度，则说明键盘弹起了
                isSoftInputShow = true
                //键盘高度
                softInputHeight = rootHeight - mutableHeight - rect.bottom
                if (this@SoftInputUtil.softInputHeight !== softInputHeight) {
                    softInputHeightChanged = true
                    this@SoftInputUtil.softInputHeight = softInputHeight
                } else {
                    softInputHeightChanged = false
                }
            }

            //获取目标View的位置坐标
            val location = IntArray(2)
            anyView.getLocationOnScreen(location)

            //条件1减少不必要的回调，只关心前后发生变化的
            //条件2针对软键盘切换手写、拼音键等键盘高度发生变化
            if (isSoftInputShowing != isSoftInputShow || isSoftInputShow && softInputHeightChanged) {
                listener.onChanged(isSoftInputShow, softInputHeight, location[1] + anyView.height - rect.bottom)
                isSoftInputShowing = isSoftInputShow
            }
        }
    }

    /**
     * 给fragment用的，有时候fragment被关闭了,键盘也关闭了，但是没有被重新创建
     * 需要重新重置键盘
     */
    fun reset() {
        isSoftInputShowing = false
    }

    companion object {
        //***************STATIC METHOD******************
        fun getNavigationBarHeight(context: Context?): Int {
            if (context == null) return 0
            val resources: Resources = context.resources
            val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

        fun showSoftInput(view: View?) {
            if (view == null) return
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(view, 0)
        }

        fun hideSoftInput(view: View?) {
            if (view == null) return
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}