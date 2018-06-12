/*
 * Copyright (C) 2017 Clever Rock Inc. All rights reserved.
 */

package me.shetj.base.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by stj on 17/04/15.
 */
@SuppressWarnings("DefaultFileTemplate")
public class SwipeableLayout extends FrameLayout {

    private int diffY;

    public interface OnLayoutCloseListener {
        void OnLayoutClosed();
    }

    public interface OnLayoutChangeListener {
        void OnLayoutChange(float size);
    }

    enum Direction {
        UP_DOWN,
        LEFT_RIGHT,
        NONE
    }

    private Direction direction = Direction.NONE;
    private int previousFingerPositionY;
    private int previousFingerPositionX;
    private int baseLayoutPosition;
    private boolean isScrollingUp;
    private boolean isLocked = false;
    private OnLayoutCloseListener listener;
    private OnLayoutChangeListener changeListener;
    public SwipeableLayout(Context context) {
        super(context);
    }

    public SwipeableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //防止多点触碰失效
        if (ev.getPointerCount() > 1){
            close();
            isLocked = true;
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            unLock();
        }
        if (isLocked) {
            return false;
        } else {
            final int y = (int) ev.getRawY();
            final int x = (int) ev.getRawX();

            if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
                previousFingerPositionX = x;
                previousFingerPositionY = y;

            } else if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
                int diffY = y - previousFingerPositionY;
                int diffX = x - previousFingerPositionX;

                if (Math.abs(diffX) + 50 < Math.abs(diffY)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        //防止多点触碰失效
        if (ev.getPointerCount() > 1){
            close();
            lock();
            return false;
        }
        if (!isLocked) {

            final int y = (int) ev.getRawY();
            final int x = (int) ev.getRawX();

            if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {

                previousFingerPositionX = x;
                previousFingerPositionY = y;
                baseLayoutPosition = (int) this.getY();

            } else if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {


                diffY = y - previousFingerPositionY;
                int diffX = x - previousFingerPositionX;

                if (direction == Direction.NONE) {
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        direction = Direction.LEFT_RIGHT;
                    } else if (Math.abs(diffX) < Math.abs(diffY)) {
                        direction = Direction.UP_DOWN;
                    } else {
                        direction = Direction.NONE;
                    }
                }

                if (direction == Direction.UP_DOWN) {
                    isScrollingUp = diffY <= 0;
                    this.setY(baseLayoutPosition + diffY);
                    if (diffY > 120 && diffY <= 480) {

                        if (changeListener != null ) {
                            changeListener.OnLayoutChange(1f - (diffY - 120f) / 600f);
                        }
                        postInvalidate();
                    }
                    return true;
                }

            } else if (ev.getActionMasked() == MotionEvent.ACTION_UP) {

                if (direction == Direction.UP_DOWN) {

                    if (isScrollingUp) {

                        close();
                    } else {
                        int height = this.getHeight();

                        if (Math.abs(diffY) > (height / 4)) {

                            if (listener != null && !isLocked) {
                                listener.OnLayoutClosed();
                            }
                        } else {
                            close();
                        }
                    }

                    return true;
                }

                direction = Direction.NONE;
            }

            return true;

        }

        return false;

    }



    public void close(){
        isLocked = false;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(this, "y", this.getY(), 0);
        positionAnimator.setDuration(200);
        positionAnimator.start();
        if (changeListener != null ) {
            changeListener.OnLayoutChange(1f);
        }
        direction = Direction.NONE;
        diffY = 0;
    }

    public void setOnLayoutCloseListener(OnLayoutCloseListener closeListener) {
        this.listener = closeListener;
    }
    public void setOnLayoutChangeListener(OnLayoutChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public void lock() {
        isLocked = true;
    }

    public void unLock() {
        isLocked = false;
    }

}