/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package shetj.me.base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.time.DateUtils
import timber.log.Timber


open class BaseCustomView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        View(context, attrs, defStyle) {
    protected val defaultSize = ArmsUtils.dp2px(88f)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Timber.i("onLayout($changed,$left,$top,$right,$bottom)")
        super.onLayout(changed, left, top, right, bottom)
    }

    //测量
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Timber.i("onMeasure($widthMeasureSpec,$heightMeasureSpec)")
        val width = measureWidth(widthMeasureSpec, defaultSize)
        val height = measureHeight(heightMeasureSpec, defaultSize)
        setMeasuredDimension(width, height)
    }

    override fun onDetachedFromWindow() {
        Timber.i("onDetachedFromWindow ${DateUtils.timeString}")
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        Timber.i("onAttachedToWindow ${DateUtils.timeString}")
        super.onAttachedToWindow()
    }

    override fun onAnimationStart() {
        Timber.i("onAnimationStart")
        super.onAnimationStart()
    }

    override fun onAnimationEnd() {
        Timber.i("onAnimationEnd")
        super.onAnimationEnd()
    }

    override fun setZ(z: Float) {
        super.setZ(z)
    }

    fun measureHeight(measureSpec: Int, defaultSize: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = defaultSize + paddingTop + paddingBottom
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        result = result.coerceAtLeast(suggestedMinimumHeight)
        return result
    }

    fun measureWidth(measureSpec: Int, defaultSize: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = defaultSize + paddingLeft + paddingRight
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        result = result.coerceAtLeast(suggestedMinimumWidth)
        return result
    }

    /**
     * 绘制文字在中心
     */
    fun drawnTextCenter(canvas: Canvas, text: String, paint: Paint) {
        val measureText = paint.measureText(text)
        val x = width / 2 - measureText / 2
        val y = height / 2 - getPaintTextYCenter(paint)
        canvas.drawText(text, x, y, paint)
    }

    open fun getPaintTextYCenter(paint: Paint): Float {
        return (paint.ascent() + paint.descent()) / 2
    }

    //开始绘制
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

    //绘制前景
    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
    }

    //绘制内容
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }


    //强行绘制得到宽高
    fun forceSpec() {
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        measure(widthMeasureSpec, heightMeasureSpec)
        Timber.i("widthMeasureSpec = $widthMeasureSpec \n heightMeasureSpec = $heightMeasureSpec")
    }
}

/**
 * onMeasure()会在初始化之后调用一到多次来测量控件或其中的子控件的宽高；
 *   onLayout()会在onMeasure()方法之后被调用一次，将控件或其子控件进行布局；
 *  onDraw()会在onLayout()方法之后调用一次，也会在用户手指触摸屏幕时被调用多次，来绘制控件。
 */