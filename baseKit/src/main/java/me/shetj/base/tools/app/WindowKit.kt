package me.shetj.base.tools.app

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.embedding.SplitController
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetricsCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import me.shetj.base.ktx.getWindowContent
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.logI
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Window kit
 *FoldingFeature 是一种 DisplayFeature，它提供了有关可折叠设备显示屏的信息，其中包括：
 *
 * - state：设备的折叠状态，即 FLAT 或 HALF_OPENED
 * - orientation：折叠边或合页的方向，即 HORIZONTAL 或 VERTICAL
 * - occlusionType：折叠边或合页是否遮住了显示屏的一部分，即 NONE 或 FULL
 * - isSeparating：折叠边或合页是否创建了两个逻辑显示区域，即 true 或 false
 */
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


    fun addWinLayoutListener(activity: FragmentActivity, collector: FlowCollector<WindowLayoutInfo> = logPostureCollector()){
        activity.lifecycleScope.launch(Dispatchers.Main) {
            activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(activity)
                    .windowLayoutInfo(activity)
                    .collect(collector)
            }
        }
    }

    /**
     * Log posture collector
     * 打印折叠屏的状态
     * @return
     */
    fun logPostureCollector(): FlowCollector<WindowLayoutInfo> {
          return FlowCollector {
              val foldingFeature = it.displayFeatures
                  .filterIsInstance<FoldingFeature>()
                  .firstOrNull()
              when{
                  isTableTopPosture(foldingFeature) -> "isTableTopPosture:桌面模式".logI()
                  isBookPosture(foldingFeature)-> "isBookPosture:图书模式".logI()
                  isSeparating(foldingFeature)->{
                      if (foldingFeature.orientation == FoldingFeature.Orientation.HORIZONTAL) {
                          "isTableTopPosture:桌面模式".logI()
                      } else {
                          "isBookPosture:图书模式".logI()
                      }
                  }
                  else ->{
                      "NormalMode：正常模式".logI()
                  }
              }
          }
    }

    /**
     *桌面模式
     */
    @OptIn(ExperimentalContracts::class)
    fun isTableTopPosture(foldingFeature: FoldingFeature?): Boolean {
        contract { returns(true) implies (foldingFeature != null) }
        return foldingFeature?.state == FoldingFeature.State.HALF_OPENED && foldingFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
    }

    /**
     * 图书模式
     */
    @OptIn(ExperimentalContracts::class)
    fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
        contract { returns(true) implies (foldFeature != null) }
        return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
    }

    /**
     * Is separating 在双屏设备上始终为 true
     * ```
     *
     *                             if (foldingFeature.orientation == HORIZONTAL) {
     *                                 enterTabletopMode(foldingFeature)
     *                             } else {
     *                                 enterBookMode(foldingFeature)
     *                             }
     * ```
     */
    @OptIn(ExperimentalContracts::class)
    fun isSeparating(foldFeature: FoldingFeature?): Boolean {
        contract { returns(true) implies (foldFeature != null) }
        return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
    }

    /**
     * Add split listener
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
