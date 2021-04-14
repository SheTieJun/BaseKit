package me.shetj.base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import me.shetj.base.R

class ForegroundImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatImageView(context, attrs) {
    private var foregroundDraw: Drawable? = null

    /**
     * Supply a drawable resource that is to be rendered on top of all of the child
     * views in the frame layout.
     *
     * @param drawableResId The drawable resource to be drawn on top of the children.
     */
    fun setForegroundResource(drawableResId: Int) {
        foreground = ContextCompat.getDrawable(context, drawableResId)
    }

    /**
     * Supply a Drawable that is to be rendered on top of all of the child
     * views in the frame layout.
     *
     * @param drawable The Drawable to be drawn on top of the children.
     */
    override fun setForeground(drawable: Drawable?) {
        if (foregroundDraw === drawable) {
            return
        }
        if (foregroundDraw != null) {
            foregroundDraw!!.callback = null
            unscheduleDrawable(foregroundDraw)
        }
        foregroundDraw = drawable
        if (drawable != null) {
            drawable.callback = this
            if (drawable.isStateful) {
                drawable.state = drawableState
            }
        }
        requestLayout()
        invalidate()
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === foregroundDraw
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (foregroundDraw != null) foregroundDraw!!.jumpToCurrentState()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (foregroundDraw != null && foregroundDraw!!.isStateful) {
            foregroundDraw!!.state = drawableState
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (foregroundDraw != null) {
            foregroundDraw!!.setBounds(0, 0, measuredWidth, measuredHeight)
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (foregroundDraw != null) {
            foregroundDraw!!.setBounds(0, 0, w, h)
            invalidate()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (foregroundDraw != null) {
            foregroundDraw!!.draw(canvas)
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundImageView)
        val foreground = a.getDrawable(R.styleable.ForegroundImageView_android_foreground)
        foreground?.let { setForeground(it) }
        a.recycle()
    }
}