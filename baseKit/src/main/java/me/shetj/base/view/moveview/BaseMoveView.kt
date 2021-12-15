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


package me.shetj.base.view.moveview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout


abstract class BaseMoveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var isDrag: Boolean = false
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()

    init {
         initView()
    }

    abstract fun initView()

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (intercept()){
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    open fun intercept() = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.rawX?:0f
        val y = event?.rawY?:0f
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
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL-> {
                isDrag = false
            }
            else -> {
            }
        }

        return !isDrag || super.onTouchEvent(event)
    }

}