package shetj.me.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.webkit.WebView
import kotlin.math.abs

/**
 * viewpager +webview的问题
 */
class NestedX5WebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs) {

    private var initialX = 0f
    private var initialY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var orientation = 0
    private val verticalPriorityThreshold = 10f


    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                initialY = event.y
                orientation = 0
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - initialX
                val dy = event.y - initialY

                if (orientation == 0) {
                    if (abs(dy) >= verticalPriorityThreshold) {
                        orientation = 1
                    } else if (abs(dx) > touchSlop && abs(dx) > abs(dy)) {
                        orientation = 2
                    } else if (abs(dy) > touchSlop) {
                        orientation = 1
                    }
                }
                if (orientation == 1) {
                    if (parent?.parent?.parent != null){
                        parent.parent.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    return super.onInterceptTouchEvent(event)
                } else if (orientation == 2) {
                    if (parent?.parent?.parent != null){
                        parent.parent.parent.requestDisallowInterceptTouchEvent(false)
                    }
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                orientation = 0
            }
        }
        return super.onInterceptTouchEvent(event)
    }

}