package me.shetj.base.ktx

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * startSmoothScroll(smoothScroller)
 * mLinearLayoutManager.startSmoothScroll(mSmoothScroller);
 * 滚动
 * speedTime 越小 速度越快
 */
fun Context.getSmoothScroller(speedTime: Float = 150f): LinearSmoothScroller {
    return object : LinearSmoothScroller(this) {
        // 返回：滑过1px时经历的时间(ms)。
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            return displayMetrics?.let {
                speedTime / displayMetrics.densityDpi
            } ?: super.calculateSpeedPerPixel(displayMetrics)
        }
    }
}

fun RecyclerView?.smoothToPosition(
    position: Int,
    scroller: LinearSmoothScroller? = this?.context?.getSmoothScroller()
) {
    if (this == null) return
    scroller?.let {
        scroller.targetPosition = position
        layoutManager?.startSmoothScroll(scroller)
    } ?: this.smoothScrollToPosition(position)
}

inline fun <reified T : BaseViewHolder> RecyclerView.findEachViewHolder(action: T?.() -> Unit) {
    for (i in 0 until childCount) {
        action((getChildViewHolder(getChildAt(i)) as? T))
    }
}

/**
 * 目标 item 是否完全可见
 * 仅适用 [LinearLayoutManager]
 */
fun RecyclerView?.isCompleteVisibleScreen(position: Int): Boolean {
    if (this == null || this.adapter == null || this.layoutManager == null ||
        this.layoutManager !is LinearLayoutManager
    ) {
        return false
    }
    var finalPos = position
    if (this.adapter is BaseQuickAdapter<*, *>) {
        finalPos += (adapter as BaseQuickAdapter<*, *>).headerLayoutCount
    }
    val firstCompleteVisibleItemPosition: Int =
        (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
    val lastCompleteVisibleItemPosition: Int =
        (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
    return finalPos in firstCompleteVisibleItemPosition..lastCompleteVisibleItemPosition
}

fun RecyclerView?.isCompleteVisibleShow(position: Int): Int {
    if (this == null || this.adapter == null || this.layoutManager == null ||
        this.layoutManager !is LinearLayoutManager
    ) {
        return position
    }
    var finalPos = position
    if (this.adapter is BaseQuickAdapter<*, *>) {
        finalPos += (adapter as BaseQuickAdapter<*, *>).headerLayoutCount
    }
    val firstCompleteVisibleItemPosition: Int =
        (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
    val lastCompleteVisibleItemPosition: Int =
        (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
    val mCount = (lastCompleteVisibleItemPosition - firstCompleteVisibleItemPosition) / 2
    if (finalPos < firstCompleteVisibleItemPosition) {
        return finalPos - 1
    }
    if (finalPos > lastCompleteVisibleItemPosition) {
        return finalPos + mCount + 1
    }
    if (finalPos in firstCompleteVisibleItemPosition..lastCompleteVisibleItemPosition) {
        return finalPos + mCount
    }
    return finalPos + 1
}

/**
 * viewPager2是必须通过代码设置
 */
fun ViewPager2.overNever() {
    val child: View = getChildAt(0)
    if (child is RecyclerView) {
        child.setOverScrollMode(View.OVER_SCROLL_NEVER)
    }
}
