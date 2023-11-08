package me.shetj.base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint.Style.FILL_AND_STROKE
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes
import me.shetj.base.R.styleable

class MediumBoldTextView : AppCompatTextView {
    private var mStrokeWidth = 0.9f

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.withStyledAttributes(attrs, styleable.MediumBoldTextView, defStyleAttr, 0) {
            mStrokeWidth = getFloat(styleable.MediumBoldTextView_strokeWidth, mStrokeWidth)
        }
    }

    override fun onDraw(canvas: Canvas) {
        // 获取当前控件的画笔
        val paint = paint
        // 设置画笔的描边宽度值
        paint.strokeWidth = mStrokeWidth
        paint.style = FILL_AND_STROKE
        super.onDraw(canvas)
    }
}
