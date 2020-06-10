package me.shetj.base.kt

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import java.text.FieldPosition


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