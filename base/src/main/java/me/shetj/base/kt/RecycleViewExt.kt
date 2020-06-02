package me.shetj.base.kt

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller


/**
 * startSmoothScroll(smoothScroller)
 * mLinearLayoutManager.startSmoothScroll(mSmoothScroller);
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