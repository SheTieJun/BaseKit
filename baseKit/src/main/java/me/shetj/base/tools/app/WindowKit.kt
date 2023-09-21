package me.shetj.base.tools.app

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.window.layout.WindowMetricsCalculator
import me.shetj.base.ktx.getWindowContent

object WindowKit {


    enum class WindowSizeClass() { COMPACT, MEDIUM, EXPANDED }


    fun addView(activity: Activity, view: View, layoutParams: FrameLayout.LayoutParams) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        val windowContent = activity.getWindowContent()
        windowContent?.addView(view, layoutParams)
    }


    fun windowSizeStream(activity: Activity): Pair<WindowSizeClass, WindowSizeClass> {
        val metrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(activity)
        val widthDp = metrics.bounds.width() /
                activity.resources.displayMetrics.density
        val widthWindowSizeClass = when {
            widthDp < 600f -> WindowSizeClass.COMPACT
            widthDp < 840f -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }
        val heightDp = metrics.bounds.height() /
                activity.resources.displayMetrics.density
        val heightWindowSizeClass = when {
            heightDp < 480f -> WindowSizeClass.COMPACT
            heightDp < 900f -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }
        return widthWindowSizeClass to heightWindowSizeClass
    }
}
