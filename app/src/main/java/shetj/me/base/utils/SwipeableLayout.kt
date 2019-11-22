/*
 * Copyright (C) 2017 Clever Rock Inc. All rights reserved.
 */

package shetj.me.base.utils

import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

import androidx.annotation.Keep

/**
 * Created by stj on 17/04/15.
 */
@Keep
class SwipeableLayout : FrameLayout {

    private var diffY: Int = 0

    private var direction = Direction.NONE
    private var previousFingerPositionY: Int = 0
    private var previousFingerPositionX: Int = 0
    private var baseLayoutPosition: Int = 0
    private var isScrollingUp: Boolean = false
    private var isLocked = false
    private var listener: OnLayoutCloseListener? = null
    private var changeListener: OnLayoutChangeListener? = null

    @Keep
    interface OnLayoutCloseListener {
        fun OnLayoutClosed()
    }

    @Keep
    interface OnLayoutChangeListener {
        fun OnLayoutChange(size: Float)
    }

    internal enum class Direction {
        UP_DOWN,
        LEFT_RIGHT,
        NONE
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        //防止多点触碰失效
        if (ev.pointerCount > 1) {
            close()
            isLocked = true
        }
        if (ev.actionMasked == MotionEvent.ACTION_UP) {
            unLock()
        }
        if (isLocked) {
            return false
        } else {
            val y = ev.rawY.toInt()
            val x = ev.rawX.toInt()

            if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
                previousFingerPositionX = x
                previousFingerPositionY = y

            } else if (ev.actionMasked == MotionEvent.ACTION_MOVE) {
                val diffY = y - previousFingerPositionY
                val diffX = x - previousFingerPositionX

                return Math.abs(diffX) + 50 < Math.abs(diffY)
            }

            return false
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        //防止多点触碰失效
        if (ev.pointerCount > 1) {
            close()
            lock()
            return false
        }
        if (!isLocked) {

            val y = ev.rawY.toInt()
            val x = ev.rawX.toInt()

            if (ev.actionMasked == MotionEvent.ACTION_DOWN) {

                previousFingerPositionX = x
                previousFingerPositionY = y
                baseLayoutPosition = this.y.toInt()

            } else if (ev.actionMasked == MotionEvent.ACTION_MOVE) {


                diffY = y - previousFingerPositionY
                val diffX = x - previousFingerPositionX

                if (direction == Direction.NONE) {
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        direction = Direction.LEFT_RIGHT
                    } else if (Math.abs(diffX) < Math.abs(diffY)) {
                        direction = Direction.UP_DOWN
                    } else {
                        direction = Direction.NONE
                    }
                }

                if (direction == Direction.UP_DOWN) {
                    isScrollingUp = diffY <= 0
                    this.y = (baseLayoutPosition + diffY).toFloat()
                    if (diffY > 120 && diffY <= 480) {

                        if (changeListener != null) {
                            changeListener!!.OnLayoutChange(1f - (diffY - 120f) / 600f)
                        }
                        postInvalidate()
                    }
                    return true
                }

            } else if (ev.actionMasked == MotionEvent.ACTION_UP) {

                if (direction == Direction.UP_DOWN) {

                    if (isScrollingUp) {

                        close()
                    } else {
                        val height = this.height

                        if (Math.abs(diffY) > height / 4) {

                            if (listener != null && !isLocked) {
                                listener!!.OnLayoutClosed()
                            }
                        } else {
                            close()
                        }
                    }

                    return true
                }

                direction = Direction.NONE
            }

            return true

        }

        return false

    }


    fun close() {
        isLocked = false
        val positionAnimator = ObjectAnimator.ofFloat(this, "y", this.y, 0f)
        positionAnimator.duration = 200
        positionAnimator.start()
        if (changeListener != null) {
            changeListener!!.OnLayoutChange(1f)
        }
        direction = Direction.NONE
        diffY = 0
    }

    fun setOnLayoutCloseListener(closeListener: OnLayoutCloseListener) {
        this.listener = closeListener
    }

    fun setOnLayoutChangeListener(changeListener: OnLayoutChangeListener) {
        this.changeListener = changeListener
    }

    fun lock() {
        isLocked = true
    }

    fun unLock() {
        isLocked = false
    }

}