package me.shetj.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewParent
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.absoluteValue

/**
 * 解决多级嵌套问题
 */
class SuperNestedScrollableHost @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var initialX = 0f
    private var initialY = 0f
    private var childScrollLocked = false
    private var touchSlop = 0
    private var isNeedLimit: Boolean? = null //是否需要检查父布局

    private var parentViewPager: ViewPager2? = null
        get() {
            if (field != null) return field
            var p: ViewParent? = parent
            while (p != null) {
                if (p is ViewPager2) {
                    if (isNeedLimit == null){
                        isNeedLimit = p.isUserInputEnabled
                    }
                    field = p
                    return p
                }
                p = p.parent
            }
            return null
        }

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 防止内存泄漏，移除引用
        parentViewPager = null
        isNeedLimit = null
    }

    private val childView: View?
        get() = getChildAt(0)

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val child = childView ?: return false
        val rv = if (child is ViewPager2) {
            child.getChildAt(0) as? RecyclerView
        } else child as? RecyclerView ?: return false
        return if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) rv?.canScrollHorizontally(delta.toInt()) == true else rv?.canScrollVertically(delta.toInt()) == true
    }

    private fun canParentScroll(orientation: Int, delta: Float): Boolean {
        val parent = parentViewPager ?: return false
        val rv = parent.getChildAt(0) as? RecyclerView ?: return false
        return if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) rv.canScrollHorizontally(delta.toInt()) else rv.canScrollVertically(delta.toInt())
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val parent = parentViewPager ?: return super.onInterceptTouchEvent(e)
        if (isNeedLimit != true) return super.onInterceptTouchEvent(e)
        val orientation = parent.orientation
        val isHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL

        // 建议使用 actionMasked 以兼容多指触控，防止由于额外手指按下导致的坐标剧烈跳变
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialX = e.x
                initialY = e.y
                parent.requestDisallowInterceptTouchEvent(true)
                childScrollLocked = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = e.x - initialX
                val dy = e.y - initialY
                val scaledDx = dx.absoluteValue
                val scaledDy = dy.absoluteValue
                
                // 将硬编码的 4 替换为系统规范的 touchSlop，提升在不同分辨率设备上的滑动体验一致性
                val sameOrientation = if (isHorizontal) scaledDx > (scaledDy + touchSlop) else scaledDy > (scaledDx + touchSlop)
                if (sameOrientation) {
                    val delta = if (isHorizontal) dx else dy
                    val childCanScroll = canChildScroll(orientation, -delta)
                    val parentCanScroll = canParentScroll(orientation, -delta)
                    if (childCanScroll || !parentCanScroll || childScrollLocked) {
                        childScrollLocked = true
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        childScrollLocked = false
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                } else {
                    childScrollLocked = false
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                childScrollLocked = false
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}