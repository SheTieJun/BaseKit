package me.shetj.base.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Keep;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

@Keep
public class ScrollableViewPager extends ViewPager {

	private boolean scrollable = true;
	/** 触摸时按下的点 **/
	PointF downP = new PointF();
	/** 触摸时当前的点 **/
	PointF curP = new PointF();
	public ScrollableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollableViewPager(Context context) {
		super(context);
	}

	public boolean isScrollable() {
		return scrollable;
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	/**
	 *
	 * @Title: onInterceptTouchEvent
	 * @Description:  是否禁止滑动不加这个画面会有微小的变化
	 * @param: @param      arg0 a
	 * @param: @return    设定文件
	 * @return: boolean    返回类型
	 * @throws
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (!scrollable) {
			return false;
		}else {
			return super.onInterceptTouchEvent(arg0);
		}

	}
	//	OnSingleTouchListener onSingleTouchListener;

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (scrollable) {
			if(getChildCount()<=1)
			{
				return super.onTouchEvent(arg0);
			}
			//每次进行onTouch事件都记录当前的按下的坐标
			curP.x = arg0.getX();
			curP.y = arg0.getY();
			if(arg0.getAction() == MotionEvent.ACTION_DOWN)
			{
				//记录按下时候的坐标
				//切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变
				downP.x = arg0.getX();
				downP.y = arg0.getY();
				//此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			if(arg0.getAction() == MotionEvent.ACTION_MOVE){
				//此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
				getParent().requestDisallowInterceptTouchEvent(true);
			}

			if(arg0.getAction() == MotionEvent.ACTION_UP || arg0.getAction() == MotionEvent.ACTION_CANCEL){
				//在up时判断是否按下和松手的坐标为一个点
				//如果是一个点，将执行点击事件，这是我自己写的点击事件，而不是onclick
				getParent().requestDisallowInterceptTouchEvent(false);
				if(downP.x==curP.x && downP.y==curP.y){
					return true;
				}
			}
			super.onTouchEvent(arg0);
			//注意这句不能 return super.onTouchEvent(arg0); 否则触发parent滑动
			return true;
		}else {
			return false;
		}

	}
}