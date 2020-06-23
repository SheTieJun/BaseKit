package me.shetj.base.ktx

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter


/**
 * startSmoothScroll(smoothScroller)
 * mLinearLayoutManager.startSmoothScroll(mSmoothScroller);
 * 滚动
 */
fun Context.getSmoothScroller(): LinearSmoothScroller {
    return object : LinearSmoothScroller(this) {
        // 返回：滑过1px时经历的时间(ms)。
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            return displayMetrics?.let {
                150f / displayMetrics.densityDpi
            } ?: super.calculateSpeedPerPixel(displayMetrics)
        }
    }
}


fun  RecyclerView?.smoothToPosition(position: Int,scroller: LinearSmoothScroller?= this?.context?.getSmoothScroller()){
    this?.let {
        scroller?.let {
            it.targetPosition = position
            layoutManager?.startSmoothScroll(scroller)
        }?: this.smoothScrollToPosition(position)

    }
}

/**
 * 目标 item 是否完全可见
 * 仅适用 [LinearLayoutManager]
 */
fun RecyclerView?.isCompleteVisibleScreen(position: Int): Boolean {
    if (this == null || this.adapter == null || this.layoutManager == null || this.layoutManager !is LinearLayoutManager) {
        return false
    }
    var finalPos = position
    if (this.adapter is BaseQuickAdapter<*, *>) {
        finalPos += (adapter as BaseQuickAdapter<*, *>).headerLayoutCount
    }
    val firstCompleteVisibleItemPosition: Int = (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
    val lastCompleteVisibleItemPosition: Int = (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
    return finalPos in firstCompleteVisibleItemPosition..lastCompleteVisibleItemPosition
}