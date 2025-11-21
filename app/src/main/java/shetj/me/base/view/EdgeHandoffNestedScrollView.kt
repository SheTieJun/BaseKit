package shetj.me.base.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.ScrollView
import androidx.core.view.ViewCompat
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChildHelper

class EdgeHandoffNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr), NestedScrollingChild2 {

    private val nestedHelper = NestedScrollingChildHelper(this)
    private var lastY = 0f
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private val consumed = IntArray(2)
    private val offset = IntArray(2)
    private val maxHeightPx: Int = (228f * resources.displayMetrics.density).toInt()

    init {
        isNestedScrollingEnabled = true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(ev)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.y
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val y = ev.y
                val dyWanted = (lastY - y).toInt()
                if (kotlin.math.abs(dyWanted) < touchSlop) {
                    return super.onTouchEvent(ev)
                }
                val contentHeight = getChildAt(0)?.measuredHeight ?: 0
                val maxScroll = (contentHeight - height).coerceAtLeast(0)
                val atTop = scrollY <= 0
                val atBottom = scrollY >= maxScroll
                parent.requestDisallowInterceptTouchEvent(!(atTop && dyWanted < 0) && !(atBottom && dyWanted > 0))
                val oldY = scrollY
//                scrollBy(0, dyWanted)//不需要连带滚动，体感很差
                val myConsumed = scrollY - oldY
                val unconsumed = dyWanted - myConsumed
                dispatchNestedScroll(0, myConsumed, 0, unconsumed, offset, ViewCompat.TYPE_TOUCH)
                lastY = y - offset[1]
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                stopNestedScroll(ViewCompat.TYPE_TOUCH)
            }
        }
        return super.onTouchEvent(ev)
    }

    override fun fling(velocityY: Int) {
        super.fling(velocityY)
//        dispatchNestedFling(0f, velocityY.toFloat(), false) //不需要连带滚动，体感很差
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        nestedHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return nestedHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return nestedHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        nestedHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return nestedHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        return nestedHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int): Boolean {
        return nestedHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return nestedHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return nestedHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val h = measuredHeight
        if (h > maxHeightPx) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeightPx, MeasureSpec.EXACTLY))
        }
    }
}