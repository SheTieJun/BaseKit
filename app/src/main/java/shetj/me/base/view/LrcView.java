package shetj.me.base.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.shetj.base.ktx.AnimatorExtKt;
import shetj.me.base.R;


/**
 * 歌词
 * Created by wcy on 2015/11/9.
 */
@SuppressLint("StaticFieldLeak")
public class LrcView extends View {
    private static final String TAG = "LrcView";
    private static final long ADJUST_DURATION = 100;
    private static final long TIMELINE_KEEP_TIME = 2 * DateUtils.SECOND_IN_MILLIS;

    protected List<LrcEntry> mLrcEntryList = new ArrayList<>();
    private TextPaint mLrcPaint = new TextPaint();
    private TextPaint mTimePaint = new TextPaint();
    private Paint.FontMetrics mTimeFontMetrics;
    private Drawable mPlayDrawable;
    private float mDividerHeight;
    private long mAnimationDuration;
    private int mNormalTextColor;
    private float mNormalTextSize;
    private int mCurrentTextColor;
    private float mCurrentTextSize;
    private int mTimelineTextColor;
    private int mTimelineColor;
    private int mTimeTextColor;
    private int mDrawableWidth;
    private int mTimeTextWidth;
    private String mDefaultLabel;
    private float mLrcPadding;
    private OnPlayClickListener mOnPlayClickListener;
    private OnTapListener mOnTapListener;
    private ValueAnimator mAnimator;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    protected float mOffset;
    protected int mCurrentLine = -1;
    private Object mFlag;
    private boolean isShowTimeline;
    private boolean isTouching;
    private boolean isFling;
    protected int minY = 100;
    private Paint gradientPaint;
    private LinearGradient linearGradient;
    private int fadeLength = 300;
    private StaticLayout mStaticLayout;
    /**
     * 歌词显示位置，靠左/居中/靠右
     */
    private int mTextGravity;

    /**
     * 播放按钮点击监听器，点击后应该跳转到指定播放位置
     */
    public interface OnPlayClickListener {
        /**
         * 播放按钮被点击，应该跳转到指定播放位置
         *
         * @param view 歌词控件
         * @param time 选中播放进度
         * @return 是否成功消费该事件，如果成功消费，则会更新UI
         */
        boolean onPlayClick(LrcView view, long time);
    }

    /**
     * 歌词控件点击监听器
     */
    public interface OnTapListener {
        /**
         * 歌词控件被点击
         *
         * @param view 歌词控件
         * @param x    点击坐标x，相对于控件
         * @param y    点击坐标y，相对于控件
         */
        void onTap(LrcView view, float x, float y);
    }

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LrcView);
        mCurrentTextSize = ta.getDimension(R.styleable.LrcView_lrcTextSize, getResources().getDimension(R.dimen.lrc_text_size));
        mNormalTextSize = ta.getDimension(R.styleable.LrcView_lrcNormalTextSize, getResources().getDimension(R.dimen.lrc_text_size));
        if (mNormalTextSize == 0) {
            mNormalTextSize = mCurrentTextSize;
        }

        mDividerHeight = ta.getDimension(R.styleable.LrcView_lrcDividerHeight, getResources().getDimension(R.dimen.lrc_divider_height));
        int defDuration = getResources().getInteger(R.integer.lrc_animation_duration);
        mAnimationDuration = ta.getInt(R.styleable.LrcView_lrcAnimationDuration, defDuration);
        mAnimationDuration = (mAnimationDuration < 0) ? defDuration : mAnimationDuration;
        mNormalTextColor = ta.getColor(R.styleable.LrcView_lrcNormalTextColor, getResources().getColor(R.color.lrc_normal_text_color));
        mCurrentTextColor = ta.getColor(R.styleable.LrcView_lrcCurrentTextColor, getResources().getColor(R.color.lrc_current_text_color));
        mTimelineTextColor = ta.getColor(R.styleable.LrcView_lrcTimelineTextColor, getResources().getColor(R.color.lrc_timeline_text_color));
        mDefaultLabel = ta.getString(R.styleable.LrcView_lrcLabel);
        mDefaultLabel = TextUtils.isEmpty(mDefaultLabel) ? getContext().getString(R.string.lrc_label) : mDefaultLabel;
        mLrcPadding = ta.getDimension(R.styleable.LrcView_lrcPadding, 0);
        mTimelineColor = ta.getColor(R.styleable.LrcView_lrcTimelineColor, getResources().getColor(R.color.lrc_timeline_color));
        float timelineHeight = ta.getDimension(R.styleable.LrcView_lrcTimelineHeight, getResources().getDimension(R.dimen.lrc_timeline_height));
        mPlayDrawable = ta.getDrawable(R.styleable.LrcView_lrcPlayDrawable);
        mPlayDrawable = (mPlayDrawable == null) ? ContextCompat.getDrawable(getContext(), R.drawable.lrc_play) : mPlayDrawable;
        mTimeTextColor = ta.getColor(R.styleable.LrcView_lrcTimeTextColor, getResources().getColor(R.color.lrc_time_text_color));
        float timeTextSize = ta.getDimension(R.styleable.LrcView_lrcTimeTextSize, getResources().getDimension(R.dimen.lrc_time_text_size));
        mTextGravity = ta.getInteger(R.styleable.LrcView_lrcTextGravity, LrcEntry.GRAVITY_CENTER);

        ta.recycle();

        mDrawableWidth = (int) getResources().getDimension(R.dimen.lrc_drawable_width);
        mTimeTextWidth = (int) getResources().getDimension(R.dimen.lrc_time_width);

        mLrcPaint.setAntiAlias(true);
        mLrcPaint.setTextSize(mCurrentTextSize);
        mLrcPaint.setTextAlign(Paint.Align.LEFT);
        mTimePaint.setAntiAlias(true);
        mTimePaint.setTextSize(timeTextSize);
        mTimePaint.setTextAlign(Paint.Align.CENTER);
        //noinspection SuspiciousNameCombination
        mTimePaint.setStrokeWidth(timelineHeight);
        mTimePaint.setStrokeCap(Paint.Cap.ROUND);
        mTimeFontMetrics = mTimePaint.getFontMetrics();

        mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
        mScroller = new Scroller(getContext());


        gradientPaint = new Paint();
        gradientPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN)); // 设置叠加模式
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    /**
     * 设置非当前行歌词字体颜色
     */
    public void setNormalColor(int normalColor) {
        mNormalTextColor = normalColor;
        postInvalidate();
    }

    /**
     * 普通歌词文本字体大小
     */
    public void setNormalTextSize(float size) {
        mNormalTextSize = size;
    }

    /**
     * 当前歌词文本字体大小
     */
    public void setCurrentTextSize(float size) {
        mCurrentTextSize = size;
    }

    /**
     * 设置当前行歌词的字体颜色
     */
    public void setCurrentColor(int currentColor) {
        mCurrentTextColor = currentColor;
        postInvalidate();
    }

    /**
     * 设置拖动歌词时选中歌词的字体颜色
     */
    public void setTimelineTextColor(int timelineTextColor) {
        mTimelineTextColor = timelineTextColor;
        postInvalidate();
    }

    /**
     * 设置拖动歌词时时间线的颜色
     */
    public void setTimelineColor(int timelineColor) {
        mTimelineColor = timelineColor;
        postInvalidate();
    }

    /**
     * 设置拖动歌词时右侧时间字体颜色
     */
    public void setTimeTextColor(int timeTextColor) {
        mTimeTextColor = timeTextColor;
        postInvalidate();
    }

    /**
     * 设置歌词是否允许拖动
     *
     * @param draggable           是否允许拖动
     * @param onPlayClickListener 设置歌词拖动后播放按钮点击监听器，如果允许拖动，则不能为 null
     */
    public void setDraggable(boolean draggable, OnPlayClickListener onPlayClickListener) {
        if (draggable) {
            if (onPlayClickListener == null) {
                throw new IllegalArgumentException("if draggable == true, onPlayClickListener must not be null");
            }
            mOnPlayClickListener = onPlayClickListener;
        } else {
            mOnPlayClickListener = null;
        }
    }

    /**
     * 设置播放按钮点击监听器
     *
     * @param onPlayClickListener 如果为非 null ，则激活歌词拖动功能，否则将将禁用歌词拖动功能
     * @deprecated use {@link #setDraggable(boolean, OnPlayClickListener)} instead
     */
    @Deprecated
    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        mOnPlayClickListener = onPlayClickListener;
    }

    /**
     * 设置歌词控件点击监听器
     *
     * @param onTapListener 歌词控件点击监听器
     */
    public void setOnTapListener(OnTapListener onTapListener) {
        mOnTapListener = onTapListener;
    }


    /**
     * 设置歌词为空时屏幕中央显示的文字，如“暂无歌词”
     */
    public void setLabel(String label) {
        runOnUi(() -> {
            mDefaultLabel = label;
            invalidate();
        });
    }


    /**
     * 歌词是否有效
     *
     * @return true，如果歌词有效，否则false
     */
    public boolean hasLrc() {
        return !mLrcEntryList.isEmpty();
    }

    /**
     * 刷新歌词
     *
     * @param time 当前播放时间
     */
    public void updateTime(long time) {
        runOnUi(() -> {
            if (!hasLrc()) {
                return;
            }

            int line = findShowLine(time);
            if (line != mCurrentLine) {
                mCurrentLine = line;
                if (isShowTimeline && line > 0 && Math.abs(getOffsetY(mCurrentLine)) > getHeight() / 2) {
                    isShowTimeline = false;
                }
                if (!isShowTimeline && line >= 0) {
                    smoothScrollTo(line);
                } else {
                    invalidate();
                }
            }
        });
    }


    /**
     * 强制到开头
     */
    public void updateTimeForceStart(int line) {
        runOnUi(() -> {
            if (!hasLrc()) {
                return;
            }
            isShowTimeline = true;
            mCurrentLine = line;
            mOffset = minY;
            invalidate();
        });
    }


    /**
     * 将歌词滚动到指定时间
     *
     * @param time 指定的时间
     * @deprecated 请使用 {@link #updateTime(long)} 代替
     */
    @Deprecated
    public void onDrag(long time) {
        updateTime(time);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            initPlayDrawable();
            initEntryList();
            if (hasLrc()) {
                smoothScrollTo(mCurrentLine, 0L);
            }
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerY = getHeight() / 2;

        canvas.save();

        // 无歌词文件
        if (!hasLrc()) {
            mLrcPaint.setColor(mCurrentTextColor);
            @SuppressLint("DrawAllocation")
            StaticLayout staticLayout = new StaticLayout(mDefaultLabel, mLrcPaint,
                    (int) getLrcWidth(), Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
            drawText(canvas, staticLayout, centerY);
            return;
        }

        int centerLine = getCenterLine();

//        if (isShowTimeline) {
//            mPlayDrawable.draw(canvas);
//
//            mTimePaint.setColor(mTimelineColor);
//            canvas.drawLine(mTimeTextWidth, centerY, getWidth() - mTimeTextWidth, centerY, mTimePaint);
//
//            mTimePaint.setColor(mTimeTextColor);
//            String timeText = LrcUtils.formatTime(mLrcEntryList.get(centerLine).getTime());
//            float timeX = getWidth() - mTimeTextWidth / 2;
//            float timeY = centerY - (mTimeFontMetrics.descent + mTimeFontMetrics.ascent) / 2;
//            canvas.drawText(timeText, timeX, timeY, mTimePaint);
//        }

        canvas.translate(0, mOffset);

        float y = 0;
        for (int i = 0; i < mLrcEntryList.size(); i++) {
            if (i == mCurrentLine) {
                mLrcPaint.setTextSize(mCurrentTextSize);
                mLrcPaint.setColor(mCurrentTextColor);
                mStaticLayout = new StaticLayout(mLrcEntryList.get(i).getText(), mLrcPaint, (int) getLrcWidth() , Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
                if (i > 0) {
                    y += ((mLrcEntryList.get(i - 1).getHeight() + mStaticLayout.getHeight()) >> 1) + mDividerHeight;
                }
                drawText(canvas, mStaticLayout, y);
            } else {
                if (i > 0) {
                    y += ((mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight()) >> 1) + mDividerHeight;
                }
                if (isShowTimeline && i == centerLine) {
                    mLrcPaint.setTextSize(mNormalTextSize);
                    mLrcPaint.setColor(mNormalTextColor);
                    drawText(canvas, mLrcEntryList.get(i).getStaticLayout(), y);
                } else {
                    mLrcPaint.setTextSize(mNormalTextSize);
                    mLrcPaint.setColor(mNormalTextColor);
                    drawText(canvas, mLrcEntryList.get(i).getStaticLayout(), y);
                }
            }
        }

        canvas.restore();

//        if (mOffset != 80) {
//            // 创建渐变
//            linearGradient = new LinearGradient(
//                    0, 0, 0, fadeLength,
//                    Color.TRANSPARENT, Color.BLACK,
//                    Shader.TileMode.CLAMP
//            );
//            gradientPaint.setShader(linearGradient);
//            // 绘制透明渐变
//            canvas.drawRect(0, 0, getWidth(), fadeLength, gradientPaint);
//        }
        if (linearGradient == null) {
            linearGradient = new LinearGradient(
                    0, getHeight() - fadeLength, 0, getHeight(),
                    Color.BLACK, Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
            );
            gradientPaint.setShader(linearGradient);
        }
        canvas.drawRect(0, getHeight() - fadeLength, getWidth(), getHeight(), gradientPaint);
    }

    /**
     * 画一行歌词
     *
     * @param y 歌词中心 Y 坐标
     */
    private void drawText(Canvas canvas, StaticLayout staticLayout, float y) {
        canvas.save();
        canvas.translate(mLrcPadding, y - (staticLayout.getHeight() >> 1));
        staticLayout.draw(canvas);
        canvas.restore();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            isTouching = false;
            // 手指离开屏幕，启动延时任务，恢复歌词位置
            if (hasLrc() && isShowTimeline && mCurrentLine >= 0) {
//                adjustCenter();
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
            }
        }
        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * 手势监听器
     */
    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        // 本次点击仅仅为了停止歌词滚动，则不响应点击事件
        private boolean isTouchForStopFling = false;

        @Override
        public boolean onDown(MotionEvent e) {
            if (!hasLrc()) {
                return mOnTapListener != null;
            }
            isTouching = true;
            removeCallbacks(hideTimelineRunnable);
            if (isFling) {
                isTouchForStopFling = true;
                mScroller.forceFinished(true);
            } else {
                isTouchForStopFling = false;
            }
            return mOnPlayClickListener != null || mOnTapListener != null;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!hasLrc() || mOnPlayClickListener == null) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
            if (!isShowTimeline) {
                isShowTimeline = true;
            } else {
                mOffset += -distanceY;
                mOffset = Math.min(mOffset, minY);
                mOffset = Math.max(mOffset, getOffset(mLrcEntryList.size() - 1));
            }
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!hasLrc() || mOnPlayClickListener == null) {
                return super.onFling(e1, e2, velocityX, velocityY);
            }
            if (isShowTimeline) {
                isFling = true;
                removeCallbacks(hideTimelineRunnable);
                if ((int) mOffset == minY) {
                    mScroller.forceFinished(true);
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
                mScroller.fling(0, (int) mOffset, 0, (int) velocityY, 0, 0, (int) getOffset(mLrcEntryList.size() - 1), minY);
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (hasLrc() && mOnPlayClickListener != null && isShowTimeline && mPlayDrawable.getBounds().contains((int) e.getX(), (int) e.getY())) {
                int centerLine = getCenterLine();
                long centerLineTime = mLrcEntryList.get(centerLine).getTime();
                // onPlayClick 消费了才更新 UI
                if (mOnPlayClickListener != null && mOnPlayClickListener.onPlayClick(LrcView.this, centerLineTime)) {
                    isShowTimeline = false;
                    removeCallbacks(hideTimelineRunnable);
                    mCurrentLine = centerLine;
                    invalidate();
                    return true;
                }
            } else if (mOnTapListener != null && !isTouchForStopFling) {
                mOnTapListener.onTap(LrcView.this, e.getX(), e.getY());
            }
            return super.onSingleTapConfirmed(e);
        }
    };

    private Runnable hideTimelineRunnable = new Runnable() {
        @Override
        public void run() {
            if (hasLrc() && isShowTimeline) {
                isShowTimeline = false;
                if (Math.abs(getOffsetY(mCurrentLine)) > getHeight() / 2) {
                    smoothScrollTo(mCurrentLine);
                } else {
                    mOffset = minY;
                    invalidate();
                }
            }
        }
    };

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mOffset = mScroller.getCurrY();
            invalidate();
        }
        if (isFling && mScroller.isFinished()) {
            isFling = false;
            if (hasLrc() && !isTouching && mCurrentLine >= 0) {
                adjustCenter();
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(hideTimelineRunnable);
        super.onDetachedFromWindow();
    }

    public void loadLrc(List<LrcEntry> entryList) {
        mLrcEntryList.clear();
        if (entryList != null && !entryList.isEmpty()) {
            mLrcEntryList.addAll(entryList);
        }
        if (mLrcEntryList.isEmpty()) {
            setLabel("暂无文稿内容");
            runOnUi(() -> {
                initEntryList();
                postInvalidate();
            });
            return;
        }
        mLrcPaint.setTextSize(mCurrentTextSize);
        mLrcPaint.setColor(mCurrentTextColor);
        mStaticLayout = new StaticLayout(mLrcEntryList.get(0).getText(), mLrcPaint, (int) getLrcWidth(),
                Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
        Collections.sort(mLrcEntryList);
        minY = mStaticLayout.getHeight() / 2 + 10;
        runOnUi(() -> {
            initEntryList();
            postInvalidate();
        });
    }

    private void initPlayDrawable() {
        int l = (mTimeTextWidth - mDrawableWidth) / 2;
        int t = getHeight() / 2 - mDrawableWidth / 2;
        int r = l + mDrawableWidth;
        int b = t + mDrawableWidth;
        mPlayDrawable.setBounds(l, t, r, b);
    }

    private void initEntryList() {
        if (!hasLrc() || getWidth() == 0) {
            return;
        }
        mLrcPaint.setTextSize(mCurrentTextSize);
        mLrcPaint.setColor(mCurrentTextColor);
        for (LrcEntry lrcEntry : mLrcEntryList) {
            lrcEntry.init(mLrcPaint, (int) getLrcWidth(), mTextGravity);
        }
        isShowTimeline = true;
        mOffset = minY;
    }

    private void reset() {
        endAnimation();
        mScroller.forceFinished(true);
        isShowTimeline = false;
        isTouching = false;
        isFling = false;
        removeCallbacks(hideTimelineRunnable);
        mLrcEntryList.clear();
        mOffset = minY;
        mCurrentLine = -1;
        invalidate();
    }

    /**
     * 将中心行微调至正中心
     */
    private void adjustCenter() {
//        smoothScrollTo(getCenterLine(), ADJUST_DURATION);
    }

    /**
     * 滚动到某一行
     */
    public void smoothScrollTo(int line) {
        if (Math.abs(getOffsetY(mCurrentLine)) > getHeight() / 2) {
            smoothScrollTo(line, mAnimationDuration);
        }
    }

    /**
     * 滚动到某一行
     */
    private void smoothScrollTo(int line, long duration) {
        float offset = getOffset(line);
        endAnimation();

        mAnimator = ValueAnimator.ofFloat(mOffset, offset);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(animation -> {
            mOffset = (float) animation.getAnimatedValue();
            invalidate();
        });
        AnimatorExtKt.resetDurationScale(mAnimator);
        mAnimator.start();
    }

    /**
     * 结束滚动动画
     */
    private void endAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
    }

    /**
     * 二分法查找当前时间应该显示的行数（最后一个 <= time 的行数）
     */
    private int findShowLine(long time) {
        if (time < 0) {
            return -1;
        }

        int left = 0;
        int right = mLrcEntryList.size();
        while (left <= right) {
            int middle = (left + right) / 2;
            long middleTime = mLrcEntryList.get(middle).getTime();

            if (time < middleTime) {
                right = middle - 1;
            } else {
                if (middle + 1 >= mLrcEntryList.size() || time < mLrcEntryList.get(middle + 1).getTime()) {
                    return middle;
                }

                left = middle + 1;
            }
        }

        return 0;
    }

    /**
     * 获取当前在视图中央的行数
     */
    private int getCenterLine() {
        int centerLine = 0;
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < mLrcEntryList.size(); i++) {
            if (Math.abs(mOffset - getOffset(i)) < minDistance) {
                minDistance = Math.abs(mOffset - getOffset(i));
                centerLine = i;
            }
        }
        return centerLine;
    }

    /**
     * 获取歌词距离视图顶部的距离
     * 采用懒加载方式
     */
    protected float getOffset(int line) {
        if (line < 0) {
            return minY;
        }
        if (mLrcEntryList.get(line).getOffset() == Float.MIN_VALUE) {
            float offset = getHeight() / 2;
            for (int i = 1; i <= line; i++) {
                offset -= ((mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight()) >> 1) + mDividerHeight;
            }
            mLrcEntryList.get(line).setOffset(offset);
        }
        return mLrcEntryList.get(line).getOffset();
    }

    protected float getOffsetY(int line) {
        if (line < 0) {
            return minY;
        }
        float offset = minY;
        for (int i = 1; i <= line; i++) {
            offset -= ((mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight()) >> 1) + mDividerHeight;
        }
        return offset;
    }

    /**
     * 获取歌词宽度
     */
    private float getLrcWidth() {
        return getWidth() - mLrcPadding * 2;
    }

    /**
     * 在主线程中运行
     */
    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            post(r);
        }
    }

    private Object getFlag() {
        return mFlag;
    }

    private void setFlag(Object flag) {
        this.mFlag = flag;
    }


}