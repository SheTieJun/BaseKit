package me.shetj.base.tools.app

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.embedding.SplitController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import me.shetj.base.ktx.getWindowContent
import me.shetj.base.ktx.launch

object WindowKit {


    enum class WindowSizeClass() { COMPACT, MEDIUM, EXPANDED }


    fun addView(activity: Activity, view: View, layoutParams: FrameLayout.LayoutParams) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        val windowContent = activity.getWindowContent()
        windowContent?.addView(view, layoutParams)
    }


    fun windowSize(activity: Activity): Pair<WindowSizeClass, WindowSizeClass> {
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

    @OptIn(ExperimentalContracts::class)
    fun isTableTopPosture(foldingFeature: FoldingFeature?): Boolean {
        contract { returns(true) implies (foldingFeature != null) }
        return foldingFeature?.state == FoldingFeature.State.HALF_OPENED && foldingFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
    }


    /**
     * Add split lisen
     *
     * @param activity
     * @param listener
     * @receiver
     */
    fun addSplitListener(activity: FragmentActivity, listener: (isSplit: Boolean) -> Unit) {
        activity.launch {
            activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                SplitController.getInstance(activity).splitInfoList(activity) // The activity instance.
                    .collect { list ->
                        listener.invoke(list.isEmpty())
                    }
            }
        }
    }

    /**
     * 在运行时检查分屏支持
     * @param context
     * @return boolean 是否可用,如果可用，可以使用[SplitController]来管理分屏，同时需要主要是否在manifest中配置了分屏的配置
     */
    fun isSplitAvailable(context: Context): Boolean {
        return SplitController.getInstance(context).splitSupportStatus == SplitController.SplitSupportStatus.SPLIT_AVAILABLE
    }
}
