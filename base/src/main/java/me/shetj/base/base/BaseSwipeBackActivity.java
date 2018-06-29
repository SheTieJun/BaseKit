package me.shetj.base.base;

import android.app.Activity;
import android.support.annotation.Keep;
import android.view.MotionEvent;

import com.aitangba.swipeback.SwipeBackHelper;

/**
 * @author shetj
 */
@Keep
public abstract class BaseSwipeBackActivity<T extends BasePresenter> extends BaseActivity<T> implements SwipeBackHelper.SlideBackManager {


    private SwipeBackHelper mSwipeBackHelper;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mSwipeBackHelper == null) {
            mSwipeBackHelper = new SwipeBackHelper(this);
        }
        return mSwipeBackHelper.processTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public Activity getSlideActivity() {
        return this;
    }

    @Override
    public boolean supportSlideBack() {
        return true;
    }

    @Override
    public boolean canBeSlideBack() {
        return true;
    }

    @Override
    public void finish() {
        if(mSwipeBackHelper != null) {
            mSwipeBackHelper.finishSwipeImmediately();
            mSwipeBackHelper = null;
        }
        super.finish();
    }
}
