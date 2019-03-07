package me.shetj.base.qmui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout

import java.util.ArrayList

import me.shetj.base.R

/**
 * Created by cgspine on 2018/1/7.
 *
 *
 * modified from https://github.com/ikew0ng/SwipeBackLayout
 */


class SwipeBackLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = R.attr.SwipeBackLayoutStyle) : QMUIWindowInsetLayout(context, attrs) {

    private var mEdgeFlag: Int = 0

    /**
     * Threshold of scroll, we will close the activity, when scrollPercent over
     * this value;
     */
    private var mScrollThreshold = DEFAULT_SCROLL_THRESHOLD

    /**
     * Set up contentView which will be moved by user gesture
     *
     * @param view
     */
    var contentView: View? = null
        private set

    private val mDragHelper: ViewDragHelper

    private var mScrollPercent: Float = 0.toFloat()

    private var mContentLeft: Int = 0

    private var mContentTop: Int = 0

    /**
     * The set of listeners to be sent events through.
     */
    private var mListeners: MutableList<SwipeListener>? = null

    private var mShadowLeft: Drawable? = null

    private var mShadowRight: Drawable? = null

    private var mShadowBottom: Drawable? = null

    private var mScrimOpacity: Float = 0.toFloat()

    private var mScrimColor = DEFAULT_SCRIM_COLOR

    private var mInLayout: Boolean = false

    private val mTmpRect = Rect()

    /**
     * Edge being dragged
     */
    private var mTrackingEdge: Int = 0

    private var mCallback: Callback? = null

    private var mPreventSwipeBackWhenDown = false

    init {
        mDragHelper = ViewDragHelper.create(this, ViewDragCallback())

        val a = context.obtainStyledAttributes(attrs, R.styleable.SwipeBackLayout, defStyle,
                R.style.SwipeBackLayout)

        val mode = EDGE_FLAGS[a.getInt(R.styleable.SwipeBackLayout_edge_flag, 0)]
        setEdgeTrackingEnabled(mode)

        val shadowLeft = a.getResourceId(R.styleable.SwipeBackLayout_shadow_left,
                R.drawable.shadow_left)
        val shadowRight = a.getResourceId(R.styleable.SwipeBackLayout_shadow_right,
                R.drawable.shadow_right)
        val shadowBottom = a.getResourceId(R.styleable.SwipeBackLayout_shadow_bottom,
                R.drawable.shadow_bottom)
        setShadow(shadowLeft, EDGE_LEFT)
        setShadow(shadowRight, EDGE_RIGHT)
        setShadow(shadowBottom, EDGE_BOTTOM)
        a.recycle()
        val density = resources.displayMetrics.density
        val minVel = MIN_FLING_VELOCITY * density
        mDragHelper.minVelocity = minVel
    }

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    private fun canSwipeBack(): Boolean {
        return mCallback == null || mCallback!!.canSwipeBack()
    }

    /**
     * Enable edge tracking for the selected edges of the parent view. The
     * callback's
     * [ViewDragHelper.Callback.onEdgeTouched]
     * and
     * [ViewDragHelper.Callback.onEdgeDragStarted]
     * methods will only be invoked for edges for which edge tracking has been
     * enabled.
     *
     * @param edgeFlags Combination of edge flags describing the edges to watch
     * @see .EDGE_LEFT
     *
     * @see .EDGE_RIGHT
     *
     * @see .EDGE_BOTTOM
     */
    fun setEdgeTrackingEnabled(edgeFlags: Int) {
        mEdgeFlag = edgeFlags
        mDragHelper.setEdgeTrackingEnabled(mEdgeFlag)
    }

    /**
     * Set a color to use for the scrim that obscures primary content while a
     * drawer is open.
     *
     * @param color Color to use in 0xAARRGGBB format.
     */
    fun setScrimColor(color: Int) {
        mScrimColor = color
        invalidate()
    }

    /**
     * Register a callback to be invoked when a swipe event is sent to this
     * view.
     *
     * @param listener the swipe listener to attach to this view
     */
    @Deprecated("use {@link #addSwipeListener} instead")
    fun setSwipeListener(listener: SwipeListener) {
        addSwipeListener(listener)
    }

    /**
     * Add a callback to be invoked when a swipe event is sent to this view.
     *
     * @param listener the swipe listener to attach to this view
     */
    fun addSwipeListener(listener: SwipeListener) {
        if (mListeners == null) {
            mListeners = ArrayList()
        }
        mListeners!!.add(listener)
    }

    /**
     * Removes a listener from the set of listeners
     *
     * @param listener
     */
    fun removeSwipeListener(listener: SwipeListener) {
        if (mListeners == null) {
            return
        }
        mListeners!!.remove(listener)
    }

    interface SwipeListener {
        /**
         * Invoke when state change
         *
         * @param state         flag to describe scroll state
         * @param scrollPercent scroll percent of this view
         * @see .STATE_IDLE
         *
         * @see .STATE_DRAGGING
         *
         * @see .STATE_SETTLING
         */
        fun onScrollStateChange(state: Int, scrollPercent: Float)

        /**
         * Invoke when scrolling
         *
         * @param edgeFlag flag to describe edge
         * @param scrollPercent scroll percent of this view
         */
        fun onScroll(edgeFlag: Int, scrollPercent: Float)

        /**
         * Invoke when edge touched
         *
         * @param edgeFlag edge flag describing the edge being touched
         * @see .EDGE_LEFT
         *
         * @see .EDGE_RIGHT
         *
         * @see .EDGE_BOTTOM
         */
        fun onEdgeTouch(edgeFlag: Int)

        /**
         * Invoke when scroll percent over the threshold for the first time
         */
        fun onScrollOverThreshold()
    }

    /**
     * Set scroll threshold, we will close the activity, when scrollPercent over
     * this value
     *
     * @param threshold
     */
    fun setScrollThresHold(threshold: Float) {
        if (threshold >= 1.0f || threshold <= 0) {
            throw IllegalArgumentException("Threshold value should be between 0 and 1.0")
        }
        mScrollThreshold = threshold
    }

    /**
     * Set a drawable used for edge shadow.
     *
     * @param shadow   Drawable to use
     * @param edgeFlag Combination of edge flags describing the edge to set
     * @see .EDGE_LEFT
     *
     * @see .EDGE_RIGHT
     *
     * @see .EDGE_BOTTOM
     */
    fun setShadow(shadow: Drawable, edgeFlag: Int) {
        if (edgeFlag and EDGE_LEFT != 0) {
            mShadowLeft = shadow
        } else if (edgeFlag and EDGE_RIGHT != 0) {
            mShadowRight = shadow
        } else if (edgeFlag and EDGE_BOTTOM != 0) {
            mShadowBottom = shadow
        }
        invalidate()
    }

    /**
     * Set a drawable used for edge shadow.
     *
     * @param resId    Resource of drawable to use
     * @param edgeFlag Combination of edge flags describing the edge to set
     * @see .EDGE_LEFT
     *
     * @see .EDGE_RIGHT
     *
     * @see .EDGE_BOTTOM
     */
    fun setShadow(resId: Int, edgeFlag: Int) {
        setShadow(resources.getDrawable(resId), edgeFlag)
    }

    /**
     * Scroll out contentView and finish the activity
     */
    fun scrollToFinishActivity() {
        val childWidth = contentView!!.width
        val childHeight = contentView!!.height

        var left = 0
        var top = 0
        if (mEdgeFlag and EDGE_LEFT != 0) {
            left = childWidth + mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE
            mTrackingEdge = EDGE_LEFT
        } else if (mEdgeFlag and EDGE_RIGHT != 0) {
            left = -childWidth - mShadowRight!!.intrinsicWidth - OVERSCROLL_DISTANCE
            mTrackingEdge = EDGE_RIGHT
        } else if (mEdgeFlag and EDGE_BOTTOM != 0) {
            top = -childHeight - mShadowBottom!!.intrinsicHeight - OVERSCROLL_DISTANCE
            mTrackingEdge = EDGE_BOTTOM
        }

        mDragHelper.smoothSlideViewTo(contentView!!, left, top)
        invalidate()
    }

    private fun preventSwipeBack(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            mPreventSwipeBackWhenDown = !canSwipeBack()
            return mPreventSwipeBackWhenDown
        } else {
            return !canSwipeBack() || mPreventSwipeBackWhenDown
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (preventSwipeBack(event)) {
            return false
        }
        try {
            return mDragHelper.shouldInterceptTouchEvent(event)
        } catch (e: ArrayIndexOutOfBoundsException) {
            return false
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (preventSwipeBack(event)) {
            return false
        }
        mDragHelper.processTouchEvent(event)
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mInLayout = true
        if (contentView != null) {
            contentView!!.layout(mContentLeft, mContentTop,
                    mContentLeft + contentView!!.measuredWidth,
                    mContentTop + contentView!!.measuredHeight)
        }
        mInLayout = false
    }

    override fun requestLayout() {
        if (!mInLayout) {
            super.requestLayout()
        }
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val drawContent = child === contentView

        val ret = super.drawChild(canvas, child, drawingTime)
        if (mScrimOpacity > 0 && drawContent
                && mDragHelper.viewDragState != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child)
            drawScrim(canvas, child)
        }
        return ret
    }

    private fun drawScrim(canvas: Canvas, child: View) {
        val baseAlpha = (mScrimColor and -0x1000000).ushr(24)
        val alpha = (baseAlpha * mScrimOpacity).toInt()
        val color = alpha shl 24 or (mScrimColor and 0xffffff)

        if (mTrackingEdge and EDGE_LEFT != 0) {
            canvas.clipRect(0, 0, child.left, height)
        } else if (mTrackingEdge and EDGE_RIGHT != 0) {
            canvas.clipRect(child.right, 0, right, height)
        } else if (mTrackingEdge and EDGE_BOTTOM != 0) {
            canvas.clipRect(child.left, child.bottom, right, height)
        }
        canvas.drawColor(color)
    }

    private fun drawShadow(canvas: Canvas, child: View) {
        val childRect = mTmpRect
        child.getHitRect(childRect)

        if (mEdgeFlag and EDGE_LEFT != 0) {
            mShadowLeft!!.setBounds(childRect.left - mShadowLeft!!.intrinsicWidth, childRect.top,
                    childRect.left, childRect.bottom)
            mShadowLeft!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowLeft!!.draw(canvas)
        }

        if (mEdgeFlag and EDGE_RIGHT != 0) {
            mShadowRight!!.setBounds(childRect.right, childRect.top,
                    childRect.right + mShadowRight!!.intrinsicWidth, childRect.bottom)
            mShadowRight!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowRight!!.draw(canvas)
        }

        if (mEdgeFlag and EDGE_BOTTOM != 0) {
            mShadowBottom!!.setBounds(childRect.left, childRect.bottom, childRect.right,
                    childRect.bottom + mShadowBottom!!.intrinsicHeight)
            mShadowBottom!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowBottom!!.draw(canvas)
        }
    }

    override fun computeScroll() {
        mScrimOpacity = 1 - mScrollPercent
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private inner class ViewDragCallback : ViewDragHelper.Callback() {
        private var mIsScrollOverValid: Boolean = false

        override fun tryCaptureView(view: View, i: Int): Boolean {
            val ret = mDragHelper.isEdgeTouched(mEdgeFlag, i)
            if (ret) {
                if (mDragHelper.isEdgeTouched(EDGE_LEFT, i)) {
                    mTrackingEdge = EDGE_LEFT
                } else if (mDragHelper.isEdgeTouched(EDGE_RIGHT, i)) {
                    mTrackingEdge = EDGE_RIGHT
                } else if (mDragHelper.isEdgeTouched(EDGE_BOTTOM, i)) {
                    mTrackingEdge = EDGE_BOTTOM
                }
                if (mListeners != null && !mListeners!!.isEmpty()) {
                    for (listener in mListeners!!) {
                        listener.onEdgeTouch(mTrackingEdge)
                    }
                }
                mIsScrollOverValid = true
            }
            var directionCheck = false
            if (mEdgeFlag == EDGE_LEFT || mEdgeFlag == EDGE_RIGHT) {
                directionCheck = !mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL, i)
            } else if (mEdgeFlag == EDGE_BOTTOM) {
                directionCheck = !mDragHelper
                        .checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL, i)
            } else if (mEdgeFlag == EDGE_ALL) {
                directionCheck = true
            }
            return ret and directionCheck
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return mEdgeFlag and (EDGE_LEFT or EDGE_RIGHT)
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return mEdgeFlag and EDGE_BOTTOM
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            if (mTrackingEdge and EDGE_LEFT != 0) {
                mScrollPercent = Math.abs(left.toFloat() / (contentView!!.width + mShadowLeft!!.intrinsicWidth))
            } else if (mTrackingEdge and EDGE_RIGHT != 0) {
                mScrollPercent = Math.abs(left.toFloat() / (contentView!!.width + mShadowRight!!.intrinsicWidth))
            } else if (mTrackingEdge and EDGE_BOTTOM != 0) {
                mScrollPercent = Math.abs(top.toFloat() / (contentView!!.height + mShadowBottom!!.intrinsicHeight))
            }
            mContentLeft = left
            mContentTop = top
            invalidate()
            if (mScrollPercent < mScrollThreshold && !mIsScrollOverValid) {
                mIsScrollOverValid = true
            }
            if (mListeners != null && !mListeners!!.isEmpty()) {
                if (mDragHelper.viewDragState == STATE_DRAGGING &&
                        mScrollPercent >= mScrollThreshold && mIsScrollOverValid) {
                    mIsScrollOverValid = false
                    for (listener in mListeners!!) {
                        listener.onScrollOverThreshold()
                    }
                }
                for (listener in mListeners!!) {
                    listener.onScroll(mTrackingEdge, mScrollPercent)
                }
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val childWidth = releasedChild.width
            val childHeight = releasedChild.height

            var left = 0
            var top = 0
            if (mTrackingEdge and EDGE_LEFT != 0) {
                left = if (xvel > 0 || xvel == 0f && mScrollPercent > mScrollThreshold)
                    childWidth+ mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE
                else
                    0
            } else if (mTrackingEdge and EDGE_RIGHT != 0) {
                left = if (xvel < 0 || xvel == 0f && mScrollPercent > mScrollThreshold)
                    -(childWidth
                            + mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE)
                else
                    0
            } else if (mTrackingEdge and EDGE_BOTTOM != 0) {
                top = if (yvel < 0 || yvel == 0f && mScrollPercent > mScrollThreshold)
                    -(childHeight
                            + mShadowBottom!!.intrinsicHeight + OVERSCROLL_DISTANCE)
                else
                    0
            }

            mDragHelper.settleCapturedViewAt(left, top)
            invalidate()
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            var ret = 0
            if (mEdgeFlag and EDGE_LEFT != 0) {
                ret = Math.min(child.width, Math.max(left, 0))
            } else if (mEdgeFlag and EDGE_RIGHT != 0) {
                ret = Math.min(0, Math.max(left, -child.width))
            }
            return ret
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            var ret = 0
            if (mEdgeFlag and EDGE_BOTTOM != 0) {
                ret = Math.min(0, Math.max(top, -child.height))
            }
            return ret
        }

        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            if (mListeners != null && !mListeners!!.isEmpty()) {
                for (listener in mListeners!!) {
                    listener.onScrollStateChange(state, mScrollPercent)
                }
            }
        }
    }


    interface Callback {
        /**
         * @return 是否可以滑动
         */
        fun canSwipeBack(): Boolean
    }

    companion object {
        /**
         * Minimum velocity that will be detected as a fling
         */
        private val MIN_FLING_VELOCITY = 400 // dips per second

        private val DEFAULT_SCRIM_COLOR = -0x67000000

        private val FULL_ALPHA = 255

        /**
         * Edge flag indicating that the left edge should be affected.
         */
        val EDGE_LEFT = ViewDragHelper.EDGE_LEFT

        /**
         * Edge flag indicating that the right edge should be affected.
         */
        val EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT

        /**
         * Edge flag indicating that the bottom edge should be affected.
         */
        val EDGE_BOTTOM = ViewDragHelper.EDGE_BOTTOM

        /**
         * Edge flag set indicating all edges should be affected.
         */
        val EDGE_ALL = EDGE_LEFT or EDGE_RIGHT or EDGE_BOTTOM

        /**
         * A view is not currently being dragged or animating as a result of a
         * fling/snap.
         */
        val STATE_IDLE = ViewDragHelper.STATE_IDLE

        /**
         * A view is currently being dragged. The position is currently changing as
         * a result of user input or simulated user input.
         */
        val STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING

        /**
         * A view is currently settling into place as a result of a fling or
         * predefined non-interactive motion.
         */
        val STATE_SETTLING = ViewDragHelper.STATE_SETTLING

        /**
         * Default threshold of scroll
         */
        private val DEFAULT_SCROLL_THRESHOLD = 0.3f

        private val OVERSCROLL_DISTANCE = 10

        private val EDGE_FLAGS = intArrayOf(EDGE_LEFT, EDGE_RIGHT, EDGE_BOTTOM, EDGE_ALL)

        fun wrap(child: View, edgeFlag: Int, callback: Callback): SwipeBackLayout {
            val wrapper = SwipeBackLayout(child.context)
            wrapper.setEdgeTrackingEnabled(edgeFlag)
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            child.layoutParams = lp
            wrapper.addView(child)
            wrapper.contentView = child
            wrapper.setCallback(callback)
            return wrapper
        }
    }
}
