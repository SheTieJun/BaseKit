package me.shetj.base.anim.motion

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent2

class TouchFrameLayout : FrameLayout, NestedScrollingParent2 {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
        }

    val motionLayout: NestedScrollingParent2
        get() = parent as NestedScrollingParent2

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return motionLayout.onStartNestedScroll(child, target, axes, type)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        motionLayout.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        motionLayout.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        motionLayout.onNestedScroll(
            target, dxConsumed, dyConsumed, dxUnconsumed,
            dyUnconsumed, type
        )
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        motionLayout.onNestedPreScroll(target, dx, dy, consumed, type)
    }
}
