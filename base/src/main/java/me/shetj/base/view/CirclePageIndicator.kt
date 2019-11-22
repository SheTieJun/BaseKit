package me.shetj.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import me.shetj.base.R
import me.shetj.base.tools.app.ArmsUtils


/**
 * 指示器
 * @author shetj
 */
class CirclePageIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    private var mActivePosition = 0
    private var mIndicatorSpacing: Int = 0
    private var mIndicatorTypeChanged = false
    private var size = ArmsUtils.dip2px(5f)

    private var mIndicatorType = IndicatorType.of(INDICATOR_TYPE_CIRCLE)
    private var mViewPager: ViewPager? = null

    private var mUserDefinedPageChangeListener: ViewPager.OnPageChangeListener? = null

    enum class IndicatorType(public val type: Int) {
        CIRCLE(INDICATOR_TYPE_CIRCLE),
        FRACTION(INDICATOR_TYPE_FRACTION),
        UNKNOWN(-1);


        companion object {

            fun of(value: Int): IndicatorType {
                when (value) {
                    INDICATOR_TYPE_CIRCLE -> return CIRCLE
                    INDICATOR_TYPE_FRACTION -> return FRACTION
                    else -> return UNKNOWN
                }
            }
        }
    }

    init {

        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.CirclePageIndicator, 0, 0)
        try {
            mIndicatorSpacing = a.getDimensionPixelSize(
                    R.styleable.CirclePageIndicator_indicator_spacing,
                    DEFAULT_INDICATOR_SPACING)
            val indicatorTypeValue = a.getInt(
                    R.styleable.CirclePageIndicator_indicator_type,
                    mIndicatorType.type)
            mIndicatorType = IndicatorType.of(indicatorTypeValue)
        } finally {
            a.recycle()
        }

        init()
    }

    private fun init() {
        orientation = HORIZONTAL
        if (layoutParams !is FrameLayout.LayoutParams) {
            val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            params.gravity = Gravity.BOTTOM or Gravity.START
            layoutParams = params
        }
    }

    fun setViewPager(pager: ViewPager) {
        mViewPager = pager
        mUserDefinedPageChangeListener = getOnPageChangeListener(pager)
        pager.addOnPageChangeListener(this)
        setIndicatorType(mIndicatorType)
    }

    fun setSize(size: Int) {
        this.size = size
        if (mViewPager != null) {
            addIndicator(mViewPager!!.adapter!!.count)
        }
    }

    fun setIndicatorType(indicatorType: IndicatorType) {
        mIndicatorType = indicatorType
        mIndicatorTypeChanged = true
        if (mViewPager != null) {
            addIndicator(mViewPager!!.adapter!!.count)
        }
    }

    private fun removeIndicator() {
        removeAllViews()
    }

    private fun addIndicator(count: Int) {
        removeIndicator()
        if (count <= 0) {
            return
        }
        if (mIndicatorType == IndicatorType.CIRCLE) {
            for (i in 0 until count) {
                val img = ImageView(context)
                val params = LayoutParams(size, size)
                params.leftMargin = mIndicatorSpacing
                params.rightMargin = mIndicatorSpacing
                img.setImageResource(R.drawable.circle_indicator_stroke)
                addView(img, params)
            }
        } else if (mIndicatorType == IndicatorType.FRACTION) {
            val textView = TextView(context)
            textView.tag = count
            val params = LayoutParams(size, size)
            addView(textView, params)
        }
        updateIndicator(mViewPager!!.currentItem)
    }

    private fun updateIndicator(position: Int) {
        if (mIndicatorTypeChanged || mActivePosition != position) {
            mIndicatorTypeChanged = false
            if (mIndicatorType == IndicatorType.CIRCLE) {
                (getChildAt(mActivePosition) as ImageView)
                        .setImageResource(R.drawable.circle_indicator_stroke)
                (getChildAt(position) as ImageView)
                        .setImageResource(R.drawable.circle_indicator_solid)
            } else if (mIndicatorType == IndicatorType.FRACTION) {
                val textView = getChildAt(0) as TextView

                textView.text = String.format("%d/%d", position + 1, textView.tag as Int)
            }
            mActivePosition = position
        }
    }

    private fun getOnPageChangeListener(pager: ViewPager): ViewPager.OnPageChangeListener? {
        try {
            val f = pager.javaClass.getDeclaredField("mOnPageChangeListener")
            f.isAccessible = true
            return f.get(pager) as ViewPager.OnPageChangeListener
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mUserDefinedPageChangeListener != null) {
            mUserDefinedPageChangeListener!!.onPageScrolled(position, positionOffset,
                    positionOffsetPixels)
        }
    }

    override fun onPageSelected(position: Int) {
        updateIndicator(position)
        if (mUserDefinedPageChangeListener != null) {
            mUserDefinedPageChangeListener!!.onPageSelected(position)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (mUserDefinedPageChangeListener != null) {
            mUserDefinedPageChangeListener!!.onPageScrollStateChanged(state)
        }
    }

    companion object {
        val INDICATOR_TYPE_CIRCLE = 0
        val INDICATOR_TYPE_FRACTION = 1

        val DEFAULT_INDICATOR_SPACING = 5
    }
}  