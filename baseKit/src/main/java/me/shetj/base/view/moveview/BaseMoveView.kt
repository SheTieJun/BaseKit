package me.shetj.base.view.moveview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

abstract class BaseMoveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var isDrag: Boolean = false
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()

    init {
        initView()
    }

    abstract fun initView()

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (intercept()) {
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    open fun intercept() = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.rawX ?: 0f
        val y = event?.rawY ?: 0f
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isDrag = false
                lastX = x
                lastY = y
            }

            MotionEvent.ACTION_MOVE -> {
                isDrag = true
                val dx = x - lastX
                val dy = y - lastY
                val x1 = getX() + dx
                val y1 = getY() + dy
                setX(x1)
                setY(y1)
                lastX = x
                lastY = y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDrag = false
            }
            else -> {
            }
        }

        return !isDrag || super.onTouchEvent(event)
    }
}
