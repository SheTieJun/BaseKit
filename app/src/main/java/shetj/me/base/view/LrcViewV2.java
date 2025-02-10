//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.graphics.Paint;
//import android.graphics.drawable.Drawable;
//import android.text.TextPaint;
//import android.text.format.DateUtils;
//import android.util.AttributeSet;
//import android.view.GestureDetector;
//import android.view.View;
//import android.widget.Scroller;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import shetj.me.base.R;
//import shetj.me.base.view.LrcEntry;
//
///**
// * A custom view for displaying and managing scrolling lyrics (LRC) in a music player.
// * Features:
// * - Smooth scrolling animation for lyrics
// * - Touch interaction for manual scrolling
// * - Timeline display with timestamp
// * - Current line highlighting
// * - Play button integration
// * - Empty state handling
// *
// * Created by wcy on 2015/11/9.
// */
//@SuppressLint("StaticFieldLeak")
//public class LrcViewV2 extends View {
//    private static final String TAG = "LrcView";
//    private static final long ADJUST_DURATION = 100;
//    private static final long TIMELINE_KEEP_TIME = 3 * DateUtils.SECOND_IN_MILLIS;
//
//    private List<LrcEntry> mLrcEntryList = new ArrayList<>();
//    private TextPaint mLrcPaint = new TextPaint();
//    private TextPaint mTimePaint = new TextPaint();
//    private Paint.FontMetrics mTimeFontMetrics;
//    private Drawable mPlayDrawable;
//    private float mDividerHeight;
//    private long mAnimationDuration;
//    private int mNormalTextColor;
//    private float mNormalTextSize;
//    private int mCurrentTextColor;
//    private float mCurrentTextSize;
//    private int mTimelineTextColor;
//    private int mTimelineColor;
//    private int mTimeTextColor;
//    private int mDrawableWidth;
//    private int mTimeTextWidth;
//    private String mDefaultLabel;
//    private float mLrcPadding;
//    private OnPlayClickListener mOnPlayClickListener;
//    private OnTapListener mOnTapListener;
//    private ValueAnimator mAnimator;
//    private GestureDetector mGestureDetector;
//    private Scroller mScroller;
//    private float mOffset;
//    private int mCurrentLine = -1;
//    private Object mFlag;
//    private boolean isShowTimeline;
//    private boolean isTouching;
//    private boolean isFling;
//    /**
//     * 歌词显示位置，靠左/居中/靠右
//     */
//    private int mTextGravity;
//
//    /**
//     * Interface for handling play button click events in the lyrics view.
//     * Implementers can control playback behavior when a specific lyric line is selected.
//     */
//    public interface OnPlayClickListener {
//        /**
//         * Called when the play button is clicked for a specific lyric line.
//         *
//         * @param view The LrcViewV2 instance that triggered the event
//         * @param time The timestamp of the selected lyric line in milliseconds
//         * @return true if the event was handled, false otherwise. UI will only update if true is returned
//         */
//        boolean onPlayClick(LrcViewV2 view, long time);
//    }
//
//    /**
//     * Interface for handling general tap events on the lyrics view.
//     * Useful for implementing custom behaviors when the view is tapped.
//     */
//    public interface OnTapListener {
//        /**
//         * Called when the lyrics view is tapped.
//         *
//         * @param view The LrcViewV2 instance that triggered the event
//         * @param x The x-coordinate of the tap relative to the view
//         * @param y The y-coordinate of the tap relative to the view
//         */
//        void onTap(LrcViewV2 view, float x, float y);
//    }
//
//    public LrcViewV2(Context context) {
//        this(context, null);
//    }
//
//    public LrcViewV2(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public LrcViewV2(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(attrs);
//    }
//
//    private void init(AttributeSet attrs) {
//        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LrcView);
//        mCurrentTextSize = ta.getDimension(R.styleable.LrcView_lrcTextSize, getResources().getDimension(R.dimen.lrc_text_size));
//        mNormalTextSize = ta.getDimension(R.styleable.LrcView_lrcNormalTextSize, getResources().getDimension(R.dimen.lrc_text_size));
//        if (mNormalTextSize == 0) {
//            mNormalTextSize = mCurrentTextSize;
//        }
//
//        mDividerHeight = ta.getDimension(R.styleable.LrcView_lrcDividerHeight, getResources().getDimension(R.dimen.lrc_divider_height));
//        int defDuration = getResources().getInteger(R.integer.lrc_animation_duration);
//        mAnimationDuration = ta.getInt(R.styleable.LrcView_lrcAnimationDuration, defDuration);
//        mAnimationDuration = (mAnimationDuration < 0) ? defDuration : mAnimationDuration;
//        mNormalTextColor = ta.getColor(R.styleable.LrcView_lrcNormalTextColor, getResources().getColor(R.color.lrc_normal_text_color));
//        mCurrentTextColor = ta.getColor(R.styleable.LrcView_lrcCurrentTextColor, getResources().getColor(R.color.lrc_current_text_color));
//        mTimelineTextColor = ta.getColor(R.styleable.LrcView_lrcTimelineTextColor, getResources().getColor(R.color.lrc_timeline_text_color));
//        mDefaultLabel = ta.getString(R.styleable.LrcView_lrcLabel);
//        mDefaultLabel = TextUtils.isEmpty(mDefaultLabel) ? getContext().getString(R.string.lrc_label) : mDefaultLabel;
//        mLrcPadding = ta.getDimension(R.styleable.LrcView_lrcPadding, 0);
//        mTimelineColor = ta.getColor(R.styleable.LrcView_lrcTimelineColor, getResources().getColor(R.color.lrc_timeline_color));
//        float timelineHeight = ta.getDimension(R.styleable.LrcView_lrcTimelineHeight, getResources().getDimension(R.dimen.lrc_timeline_height));
//        mPlayDrawable = ta.getDrawable(R.styleable.LrcView_lrcPlayDrawable);
//        mPlayDrawable = (mPlayDrawable == null) ? ContextCompat.getDrawable(getContext(), R.drawable.lrc_play) : mPlayDrawable;
//        mTimeTextColor = ta.getColor(R.styleable.LrcView_lrcTimeTextColor, getResources().getColor(R.color.lrc_time_text_color));
//        float timeTextSize = ta.getDimension(R.styleable.LrcView_lrcTimeTextSize, getResources().getDimension(R.dimen.lrc_time_text_size));
//        mTextGravity = ta.getInteger(R.styleable.LrcView_lrcTextGravity, LrcEntry.GRAVITY_CENTER);
//
//        ta.recycle();
//
//        mDrawableWidth = (int) getResources().getDimension(R.dimen.lrc_drawable_width);
//        mTimeTextWidth = (int) getResources().getDimension(R.dimen.lrc_time_width);
//
//        mLrcPaint.setAntiAlias(true);
//        mLrcPaint.setTextSize(mCurrentTextSize);
//        mLrcPaint.setTextAlign(Paint.Align.LEFT);
//        mTimePaint.setAntiAlias(true);
//        mTimePaint.setTextSize(timeTextSize);
//        mTimePaint.setTextAlign(Paint.Align.CENTER);
//        //noinspection SuspiciousNameCombination
//        mTimePaint.setStrokeWidth(timelineHeight);
//        mTimePaint.setStrokeCap(Paint.Cap.ROUND);
//        mTimeFontMetrics = mTimePaint.getFontMetrics();
//
//        mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
//        mGestureDetector.setIsLongpressEnabled(false);
//        mScroller = new Scroller(getContext());
//    }
//
//    /**
//     * Sets the non-current line lyrics text color.
//     */
//    public void setNormalColor(int normalColor) {
//        mNormalTextColor = normalColor;
//        postInvalidate();
//    }
//
//    /**
//     * Sets the normal lyrics text size.
//     */
//    public void setNormalTextSize(float size) {
//        mNormalTextSize = size;
//    }
//
//    /**
//     * Sets the current lyrics text size.
//     */
//    public void setCurrentTextSize(float size) {
//        mCurrentTextSize = size;
//    }
//
//    /**
//     * Sets the current line lyrics text color.
//     */
//    public void setCurrentColor(int currentColor) {
//        mCurrentTextColor = currentColor;
//        postInvalidate();
//    }
//
//    /**
//     * Sets the timeline text color during manual scrolling.
//     */
//    public void setTimelineTextColor(int timelineTextColor) {
//        mTimelineTextColor = timelineTextColor;
//        postInvalidate();
//    }
//
//    /**
//     * Sets the timeline color during manual scrolling.
//     */
//    public void setTimelineColor(int timelineColor) {
//        mTimelineColor = timelineColor;
//        postInvalidate();
//    }
//
//    /**
//     * Sets the time text color during manual scrolling.
//     */
//    public void setTimeTextColor(int timeTextColor) {
//        mTimeTextColor = timeTextColor;
//        postInvalidate();
//    }
//
//    /**
//     * Enables or disables manual scrolling for the lyrics view.
//     * If enabled, a play button click listener must be provided.
//     *
//     * @param draggable           Whether to enable manual scrolling
//     * @param onPlayClickListener The play button click listener (required if draggable is true)
//     */
//    public void setDraggable(boolean draggable, OnPlayClickListener onPlayClickListener) {
//        if (draggable) {
//            if (onPlayClickListener == null) {
//                throw new IllegalArgumentException("if draggable == true, onPlayClickListener must not be null");
//            }
//            mOnPlayClickListener = onPlayClickListener;
//        } else {
//            mOnPlayClickListener = null;
//        }
//    }
//
//    /**
//     * Sets the tap listener for the lyrics view.
//     *
//     * @param onTapListener The tap listener
//     */
//    public void setOnTapListener(OnTapListener onTapListener) {
//        mOnTapListener = onTapListener;
//    }
//
//    /**
//     * Sets the default label to display when no lyrics are available.
//     */
//    public void setLabel(String label) {
//        runOnUi(() -> {
//            mDefaultLabel = label;
//            invalidate();
//        });
//    }
//
//    /**
//     * Checks if the lyrics view has any lyrics data.
//     *
//     * @return true if the lyrics view has lyrics data, false otherwise
//     */
//    public boolean hasLrc() {
//        return !mLrcEntryList.isEmpty();
//    }
//
//    /**
//     * Updates the current position in the lyrics based on playback time.
//     * This method handles the smooth scrolling animation to the current line.
//     *
//     * @param time Current playback time in milliseconds
//     */
//    public void updateTime(long time) {
//        runOnUi(() -> {
//            if (!hasLrc()) {
//                return;
//            }
//
//            int line = findShowLine(time);
//            if (line != mCurrentLine) {
//                mCurrentLine = line;
//                if (isShowTimeline && line > 0 && Math.abs(getOffsetY(mCurrentLine)) > getHeight() / 2) {
//                    isShowTimeline = false;
//                }
//                if (!isShowTimeline && line >= 0) {
//                    smoothScrollTo(line);
//                } else {
//                    invalidate();
//                }
//            }
//        });
//    }
//
//    /**
//     * Forces the lyrics view to scroll to the beginning.
//     * Useful for resetting the view state or handling seek to start events.
//     */
//    public void updateTimeForceStart() {
//        runOnUi(() -> {
//            if (!hasLrc()) {
//                return;
//            }
//            isShowTimeline = true;
//            mCurrentLine = 0;
//            mOffset = 30;
//            invalidate();
//        });
//    }
//
//    /**
//     * Loads and initializes lyrics data.
//     * The lyrics will be sorted by time and prepared for display.
//     *
//     * @param entryList List of LrcEntry objects containing lyrics data and timestamps
//     */
//    public void loadLrc(List<LrcEntry> entryList) {
//        if (entryList != null && !entryList.isEmpty()) {
//            mLrcEntryList.addAll(entryList);
//        }
//
//        Collections.sort(mLrcEntryList);
//
//        initEntryList();
//        invalidate();
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        if (changed) {
//            initPlayDrawable();
//            initEntryList();
//            if (hasLrc()) {
//                smoothScrollTo(mCurrentLine, 0L);
//            }
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        int centerY = getHeight() / 2;
//
//        // 无歌词文件
//        if (!hasLrc()) {
//            mLrcPaint.setColor(mCurrentTextColor);
//            @SuppressLint("DrawAllocation")
//            StaticLayout staticLayout = new StaticLayout(mDefaultLabel, mLrcPaint,
//                    (int) getLrcWidth(), Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
//            drawText(canvas, staticLayout, centerY);
//            return;
//        }
//
//        int centerLine = getCenterLine();
//
////        if (isShowTimeline) {
////            mPlayDrawable.draw(canvas);
////
////            mTimePaint.setColor(mTimelineColor);
////            canvas.drawLine(mTimeTextWidth, centerY, getWidth() - mTimeTextWidth, centerY, mTimePaint);
////
////            mTimePaint.setColor(mTimeTextColor);
////            String timeText = LrcUtils.formatTime(mLrcEntryList.get(centerLine).getTime());
////            float timeX = getWidth() - mTimeTextWidth / 2;
////            float timeY = centerY - (mTimeFontMetrics.descent + mTimeFontMetrics.ascent) / 2;
////            canvas.drawText(timeText, timeX, timeY, mTimePaint);
////        }
//
//        canvas.translate(0, mOffset);
//
//        float y = 0;
//        for (int i = 0; i < mLrcEntryList.size(); i++) {
//            if (i > 0) {
//                y += ((mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight()) >> 1) + mDividerHeight;
//            }
//            if (i == mCurrentLine) {
//                mLrcPaint.setTextSize(mCurrentTextSize);
//                mLrcPaint.setColor(mCurrentTextColor);
//            } else if (isShowTimeline && i == centerLine) {
//                mLrcPaint.setTextSize(mNormalTextSize);
//                mLrcPaint.setColor(mNormalTextColor);
//            } else {
//                mLrcPaint.setTextSize(mNormalTextSize);
//                mLrcPaint.setColor(mNormalTextColor);
//            }
//            drawText(canvas, mLrcEntryList.get(i).getStaticLayout(), y);
//        }
//    }
//
//    /**
//     * Draws a single line of lyrics.
//     *
//     * @param y The vertical position of the line
//     */
//    private void drawText(Canvas canvas, StaticLayout staticLayout, float y) {
//        canvas.save();
//        canvas.translate(mLrcPadding, y - (staticLayout.getHeight() >> 1));
//        staticLayout.draw(canvas);
//        canvas.restore();
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
//            isTouching = false;
//            // 手指离开屏幕，启动延时任务，恢复歌词位置
//            if (hasLrc() && isShowTimeline && mCurrentLine >= 0) {
//                adjustCenter();
//                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
//            }
//        }
//        return mGestureDetector.onTouchEvent(event);
//    }
//
//    /**
//     * Gesture listener for handling touch events.
//     */
//    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
//        // 本次点击仅仅为了停止歌词滚动，则不响应点击事件
//        private boolean isTouchForStopFling = false;
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            if (!hasLrc()) {
//                return mOnTapListener != null;
//            }
//            isTouching = true;
//            removeCallbacks(hideTimelineRunnable);
//            if (isFling) {
//                isTouchForStopFling = true;
//                mScroller.forceFinished(true);
//            } else {
//                isTouchForStopFling = false;
//            }
//            return mOnPlayClickListener != null || mOnTapListener != null;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            if (!hasLrc() || mOnPlayClickListener == null) {
//                return super.onScroll(e1, e2, distanceX, distanceY);
//            }
//            if (!isShowTimeline) {
//                isShowTimeline = true;
//            } else {
//                mOffset += -distanceY;
//                mOffset = Math.min(mOffset, getOffset(0));
//                mOffset = Math.max(mOffset, getOffset(mLrcEntryList.size() - 1));
//            }
//            invalidate();
//            return true;
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            if (!hasLrc() || mOnPlayClickListener == null) {
//                return super.onFling(e1, e2, velocityX, velocityY);
//            }
//            if (isShowTimeline) {
//                isFling = true;
//                removeCallbacks(hideTimelineRunnable);
//                mScroller.fling(0, (int) mOffset, 0, (int) velocityY, 0, 0, (int) getOffset(mLrcEntryList.size() - 1), (int) getOffset(0));
//                return true;
//            }
//            return super.onFling(e1, e2, velocityX, velocityY);
//        }
//
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            if (hasLrc() && mOnPlayClickListener != null && isShowTimeline && mPlayDrawable.getBounds().contains((int) e.getX(), (int) e.getY())) {
//                int centerLine = getCenterLine();
//                long centerLineTime = mLrcEntryList.get(centerLine).getTime();
//                // onPlayClick 消费了才更新 UI
//                if (mOnPlayClickListener != null && mOnPlayClickListener.onPlayClick(LrcViewV2.this, centerLineTime)) {
//                    isShowTimeline = false;
//                    removeCallbacks(hideTimelineRunnable);
//                    mCurrentLine = centerLine;
//                    invalidate();
//                    return true;
//                }
//            } else if (mOnTapListener != null && !isTouchForStopFling) {
//                mOnTapListener.onTap(LrcViewV2.this, e.getX(), e.getY());
//            }
//            return super.onSingleTapConfirmed(e);
//        }
//    };
//
//    private Runnable hideTimelineRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Log.d(TAG, "hideTimelineRunnable run");
//            if (hasLrc() && isShowTimeline) {
//                isShowTimeline = false;
//                smoothScrollTo(mCurrentLine);
//            }
//        }
//    };
//
//    @Override
//    public void computeScroll() {
//        if (mScroller.computeScrollOffset()) {
//            mOffset = mScroller.getCurrY();
//            invalidate();
//        }
//
//        if (isFling && mScroller.isFinished()) {
//            Log.d(TAG, "fling finish");
//            isFling = false;
//            if (hasLrc() && !isTouching) {
//                adjustCenter();
//                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
//            }
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        removeCallbacks(hideTimelineRunnable);
//        super.onDetachedFromWindow();
//    }
//
//    /**
//     * Initializes the play button drawable.
//     */
//    private void initPlayDrawable() {
//        int l = (mTimeTextWidth - mDrawableWidth) / 2;
//        int t = getHeight() / 2 - mDrawableWidth / 2;
//        int r = l + mDrawableWidth;
//        int b = t + mDrawableWidth;
//        mPlayDrawable.setBounds(l, t, r, b);
//    }
//
//    /**
//     * Initializes the lyrics entry list.
//     */
//    private void initEntryList() {
//        if (!hasLrc() || getWidth() == 0) {
//            return;
//        }
//
//        for (LrcEntry lrcEntry : mLrcEntryList) {
//            lrcEntry.init(mLrcPaint, (int) getLrcWidth(), mTextGravity);
//        }
//        isShowTimeline = true;
//        mOffset = 30;
//    }
//
//    /**
//     * Resets the lyrics view to its initial state.
//     */
//    private void reset() {
//        endAnimation();
//        mScroller.forceFinished(true);
//        isShowTimeline = false;
//        isTouching = false;
//        isFling = false;
//        removeCallbacks(hideTimelineRunnable);
//        mLrcEntryList.clear();
//        mOffset = 0;
//        mCurrentLine = -1;
//        invalidate();
//    }
//
//    /**
//     * Adjusts the center line to be perfectly centered.
//     */
//    private void adjustCenter() {
//        smoothScrollTo(getCenterLine(), ADJUST_DURATION);
//    }
//
//    /**
//     * Scrolls to a specific line with animation.
//     *
//     * @param line The line number to scroll to
//     */
//    public void smoothScrollTo(int line) {
//        smoothScrollTo(line, mAnimationDuration);
//    }
//
//    /**
//     * Scrolls to a specific line with animation and custom duration.
//     *
//     * @param line    The line number to scroll to
//     * @param duration The animation duration in milliseconds
//     */
//    private void smoothScrollTo(int line, long duration) {
//        float offset = getOffset(line);
//        endAnimation();
//
//        mAnimator = ValueAnimator.ofFloat(mOffset, offset);
//        mAnimator.setDuration(duration);
//        mAnimator.setInterpolator(new LinearInterpolator());
//        mAnimator.addUpdateListener(animation -> {
//            mOffset = (float) animation.getAnimatedValue();
//            invalidate();
//        });
//        LrcUtils.resetDurationScale();
//        mAnimator.start();
//    }
//
//    /**
//     * Ends the current animation.
//     */
//    private void endAnimation() {
//        if (mAnimator != null && mAnimator.isRunning()) {
//            mAnimator.end();
//        }
//    }
//
//    /**
//     * Finds the appropriate line number to display for the given timestamp using binary search.
//     * Returns the index of the last line with a timestamp less than or equal to the given time.
//     *
//     * @param time The timestamp to find the corresponding lyric line for
//     * @return The index of the matching line, or -1 if the time is negative
//     */
//    private int findShowLine(long time) {
//        if (time < 0) {
//            return -1;
//        }
//
//        int left = 0;
//        int right = mLrcEntryList.size();
//        while (left <= right) {
//            int middle = (left + right) / 2;
//            long middleTime = mLrcEntryList.get(middle).getTime();
//
//            if (time < middleTime) {
//                right = middle - 1;
//            } else {
//                if (middle + 1 >= mLrcEntryList.size() || time < mLrcEntryList.get(middle + 1).getTime()) {
//                    return middle;
//                }
//
//                left = middle + 1;
//            }
//        }
//
//        return 0;
//    }
//
//    /**
//     * Calculates the line number that should be centered in the view based on current scroll offset.
//     * This is used for determining which line to highlight during manual scrolling.
//     *
//     * @return The index of the line that should be centered
//     */
//    private int getCenterLine() {
//        int centerLine = 0;
//        float minDistance = Float.MAX_VALUE;
//        for (int i = 0; i < mLrcEntryList.size(); i++) {
//            if (Math.abs(mOffset - getOffset(i)) < minDistance) {
//                minDistance = Math.abs(mOffset - getOffset(i));
//                centerLine = i;
//            }
//        }
//        return centerLine;
//    }
//
//    /**
//     * Calculates the vertical offset for a given line number using lazy loading.
//     * The offset represents the distance from the top of the view to where the line should be rendered.
//     * Results are cached to improve performance on subsequent calls.
//     *
//     * @param line The line number to calculate the offset for
//     * @return The vertical offset in pixels
//     */
//    private float getOffset(int line) {
//        if (line < 0) {
//            return 30;
//        }
//        if (mLrcEntryList.get(line).getOffset() == Float.MIN_VALUE) {
//            float offset = getHeight() / 2;
//            for (int i = 1; i <= line; i++) {
//                offset -= ((mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight()) >> 1) + mDividerHeight;
//            }
//            mLrcEntryList.get(line).setOffset(offset);
//        }
//        return mLrcEntryList.get(line).getOffset();
//    }
//
//    /**
//     * Calculates the vertical offset for a given line number without caching.
//     *
//     * @param line The line number to calculate the offset for
//     * @return The vertical offset in pixels
//     */
//    private float getOffsetY(int line) {
//        if (line < 0) {
//            return 30;
//        }
//        float offset = 30;
//        for (int i = 1; i <= line; i++) {
//            offset -= ((mLrcEntryList.get(i - 1).getHeight() + mLrcEntryList.get(i).getHeight()) >> 1) + mDividerHeight;
//        }
//        return offset;
//    }
//
//    /**
//     * Calculates the width available for rendering lyrics.
//     *
//     * @return The available width in pixels
//     */
//    private float getLrcWidth() {
//        return getWidth() - mLrcPadding * 2;
//    }
//
//    /**
//     * Ensures that a Runnable is executed on the main thread.
//     * If already on the main thread, executes immediately; otherwise posts to the main thread.
//     *
//     * @param r The Runnable to be executed
//     */
//    private void runOnUi(Runnable r) {
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            r.run();
//        } else {
//            post(r);
//        }
//    }
//
//    private Object getFlag() {
//        return mFlag;
//    }
//
//    private void setFlag(Object flag) {
//        this.mFlag = flag;
//    }
//}