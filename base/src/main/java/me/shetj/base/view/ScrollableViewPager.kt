package me.shetj.base.view

import android.content.Context
import android.graphics.PointF
import androidx.annotation.Keep
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 多层滚动的viewpager
 */
@Keep
class ScrollableViewPager : ViewPager {

    private var isScrollable = true
    /** 触摸时按下的点  */
    private var downP = PointF()
    /** 触摸时当前的点  */
    private var curP = PointF()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    /**
     *
     * @Title: onInterceptTouchEvent
     * @Description:  是否禁止滑动不加这个画面会有微小的变化
     * @param: @param      arg0 a
     * @param: @return    设定文件
     * @return: boolean    返回类型
     * @throws
     */
    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        return if (!isScrollable) {
            false
        } else {
            super.onInterceptTouchEvent(arg0)
        }

    }
    //	OnSingleTouchListener onSingleTouchListener;

    override fun onTouchEvent(arg0: MotionEvent): Boolean {
        if (isScrollable) {
            if (childCount <= 1) {
                return super.onTouchEvent(arg0)
            }
            //每次进行onTouch事件都记录当前的按下的坐标
            curP.x = arg0.x
            curP.y = arg0.y
            if (arg0.action == MotionEvent.ACTION_DOWN) {
                //记录按下时候的坐标
                //切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
                downP.x = arg0.x
                downP.y = arg0.y
                //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
                parent.requestDisallowInterceptTouchEvent(true)
            }
            if (arg0.action == MotionEvent.ACTION_MOVE) {
                //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
                parent.requestDisallowInterceptTouchEvent(true)
            }

            if (arg0.action == MotionEvent.ACTION_UP || arg0.action == MotionEvent.ACTION_CANCEL) {
                //在up时判断是否按下和松手的坐标为一个点
                //如果是一个点，将执行点击事件，这是我自己写的点击事件，而不是onclick
                parent.requestDisallowInterceptTouchEvent(false)
                if (downP.x == curP.x && downP.y == curP.y) {
                    return true
                }
            }
            super.onTouchEvent(arg0)
            //注意这句不能 return super.onTouchEvent(arg0); 否则触发parent滑动
            return true
        } else {
            return false
        }

    }
}