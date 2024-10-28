package shetj.me.base.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import shetj.me.base.R;


public class ArcProgressView extends View {

    private static final String TAG = ArcProgressView.class.getSimpleName();
    private Context mContext;

    //默认大小
    private int mDefaultSize;
    //是否开启抗锯齿
    private boolean antiAlias;
    //绘制提示
    private TextPaint mHintPaint;
    private CharSequence mHint;
    private int mHintColor;
    private float mHintSize;
    private float mHintOffset;

    //绘制单位
    private TextPaint mUnitPaint;
    private CharSequence mUnit;
    private int mUnitColor;
    private float mUnitSize;
    private float mUnitOffset;

    //绘制数值
    private TextPaint mValuePaint;
    private float mValue;
    private float mMaxValue;
    private float mValueOffset;
    private int mPrecision;
    private String mPrecisionFormat;
    private int mValueColor;
    private float mValueSize;

    //绘制圆弧
    private Paint mArcPaint;
    private float mArcWidth,mBgArcWidth;
    private float mStartAngle, mSweepAngle;
    private RectF mRectF;
    //渐变的颜色是360度，如果只显示270，那么则会缺失部分颜色
    private SweepGradient mSweepGradient;
    private int[] mGradientColors = {Color.GREEN, Color.YELLOW, Color.RED};
    //当前进度，[0.0f,1.0f]
    private float mPercent=0.0f;
    //动画时间
    private long mAnimTime;
    //属性动画
    private ValueAnimator mAnimator;

    //绘制背景圆弧
    private Paint mBgArcPaint;
    private int mBgArcColor;
    private float mStrokeWith;
    //圆心坐标，半径
    private Point mCenterPoint;
    private float mRadius;
    private float mTextOffsetPercentInRadius;
    //初始化默认值
    private static final boolean ANTI_ALIAS = true;

    private static final int DEFAULT_SIZE = 150;
    private static final int DEFAULT_START_ANGLE = 270;
    private static final int DEFAULT_SWEEP_ANGLE = 360;

    private static final int DEFAULT_ANIM_TIME = 1000;

    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_VALUE = 50;

    private static final int DEFAULT_HINT_SIZE = 15;
    private static final int DEFAULT_UNIT_SIZE = 30;
    private static final int DEFAULT_VALUE_SIZE = 15;

    private static final int DEFAULT_ARC_WIDTH = 15;
    public ArcProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mDefaultSize =dp2px(DEFAULT_SIZE);
        mAnimator = new ValueAnimator();
        mRectF = new RectF();
        mCenterPoint = new Point();
        initAttrs(attrs);
        initPaint();
        //setValue(mValue);
    }
    protected int dp2px(float dp) {
        float scale =getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    protected int sp2px(float spValue) {
        float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ArcProgressView);

        antiAlias = typedArray.getBoolean(R.styleable.ArcProgressView_antiAlias, ANTI_ALIAS);

        mHint = typedArray.getString(R.styleable.ArcProgressView_hint);
        mHintColor = typedArray.getColor(R.styleable.ArcProgressView_hintColor, Color.BLACK);
        mHintSize = typedArray.getDimension(R.styleable.ArcProgressView_hintSize, DEFAULT_HINT_SIZE);

        mValue = typedArray.getFloat(R.styleable.ArcProgressView_value, DEFAULT_VALUE);
        mMaxValue = typedArray.getFloat(R.styleable.ArcProgressView_maxValue, DEFAULT_MAX_VALUE);
        //内容数值精度格式
        mPrecision = typedArray.getInt(R.styleable.ArcProgressView_precision, 0);
        mPrecisionFormat =getPrecisionFormat(mPrecision);
        mValueColor = typedArray.getColor(R.styleable.ArcProgressView_valueColor, Color.BLACK);
        mValueSize = typedArray.getDimension(R.styleable.ArcProgressView_valueSize,DEFAULT_VALUE_SIZE);

        mUnit = typedArray.getString(R.styleable.ArcProgressView_unit);
        mUnitColor = typedArray.getColor(R.styleable.ArcProgressView_unitColor, Color.BLACK);
        mUnitSize = typedArray.getDimension(R.styleable.ArcProgressView_unitSize, DEFAULT_UNIT_SIZE);

        //mArcWidth = typedArray.getDimension(R.styleable.ArcProgressView_arcWidth, DEFAULT_ARC_WIDTH);
        mStartAngle = typedArray.getFloat(R.styleable.ArcProgressView_startAngle, DEFAULT_START_ANGLE);
        mSweepAngle = typedArray.getFloat(R.styleable.ArcProgressView_sweepAngle, DEFAULT_SWEEP_ANGLE);

        mBgArcColor = typedArray.getColor(R.styleable.ArcProgressView_bgArcColor, Color.WHITE);
        //mBgArcWidth = typedArray.getDimension(R.styleable.ArcProgressView_bgArcWidth, DEFAULT_ARC_WIDTH);
        mStrokeWith= typedArray.getDimension(R.styleable.ArcProgressView_mStrokeWith, DEFAULT_ARC_WIDTH);
        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable.ArcProgressView_textOffsetPercentInRadius, 0.33f);

        mAnimTime = typedArray.getInt(R.styleable.ArcProgressView_animTime, DEFAULT_ANIM_TIME);
        int color = Color.parseColor("#9c71fe");
        mGradientColors = new int[2];
        mGradientColors[0] = color;
        mGradientColors[1] = color;
        typedArray.recycle();
    }

    private void initPaint() {
        mHintPaint = new TextPaint();
        // 设置抗锯齿,会消耗较大资源，绘制图形速度会变慢。
        mHintPaint.setAntiAlias(antiAlias);
        // 设置绘制文字大小
        mHintPaint.setTextSize(mHintSize);
        // 设置画笔颜色
        mHintPaint.setColor(mHintColor);
        // 从中间向两边绘制，不需要再次计算文字
        mHintPaint.setTextAlign(Paint.Align.CENTER);

        mValuePaint = new TextPaint();
        mValuePaint.setAntiAlias(antiAlias);
        mValuePaint.setTextSize(mValueSize);
        mValuePaint.setColor(mValueColor);
        // 设置Typeface对象，即字体风格，包括粗体，斜体以及衬线体，非衬线体等
        mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mUnitPaint = new TextPaint();
        mUnitPaint.setAntiAlias(antiAlias);
        mUnitPaint.setTextSize(mUnitSize);
        mUnitPaint.setColor(mUnitColor);
        mUnitPaint.setTextAlign(Paint.Align.CENTER);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(antiAlias);
        // 设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
        mArcPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔粗细
        mArcPaint.setStrokeWidth(mStrokeWith);
        // 设置线帽
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(antiAlias);
        mBgArcPaint.setColor(mBgArcColor);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mStrokeWith);
        mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measures(widthMeasureSpec,mDefaultSize),measures(heightMeasureSpec,mDefaultSize));
    }
    private int measures(int measureSpec,int defaultSize){
        int result=defaultSize;
        int specSize= MeasureSpec.getSize(measureSpec);
        int specMode= MeasureSpec.getMode(measureSpec);
        if(specMode== MeasureSpec.EXACTLY){
            result=specSize;
        }else if(specMode== MeasureSpec.AT_MOST){
            result= Math.min(result,specSize);
        }
        return result;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //求圆弧和背景圆弧的最大宽度
        //float maxArcWidth = Math.max(mArcWidth, mBgArcWidth);
        //float maxArcWidth=mStrokeWith;
        //求最小值作为实际值
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * (int) mStrokeWith,
                h - getPaddingTop() - getPaddingBottom() - 2 * (int) mStrokeWith);
        //减去圆弧的宽度，否则会造成部分圆弧绘制在外围
        mRadius = minSize / 2;
        //获取圆的相关参数
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;
        //绘制圆弧的边界
        mRectF.left = mCenterPoint.x - mRadius - mStrokeWith/ 2;
        mRectF.top = mCenterPoint.y - mRadius - mStrokeWith/ 2;
        mRectF.right = mCenterPoint.x + mRadius + mStrokeWith/ 2;
        mRectF.bottom = mCenterPoint.y + mRadius + mStrokeWith/ 2;
        //计算文字绘制时的 baseline
        //由于文字的baseline、descent、ascent等属性只与textSize和typeface有关，所以此时可以直接计算
        //若value、hint、unit由同一个画笔绘制或者需要动态设置文字的大小，则需要在每次更新后再次计算
        mValueOffset = mCenterPoint.y + getBaselineOffsetFromY(mValuePaint);
        mHintOffset = mCenterPoint.y - mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mHintPaint);
        mUnitOffset = mCenterPoint.y + mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mUnitPaint);
        updateArcPaint();
    }

    private float getBaselineOffsetFromY(Paint paint) {
        return measureTextHeight(paint) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawArc(canvas);
    }

    /**
     * 绘制内容文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        // 计算文字宽度，由于Paint已设置为居中绘制，故此处不需要重新计算
        // float textWidth = mValuePaint.measureText(mValue.toString());
        // float x = mCenterPoint.x - textWidth / 2;
//        canvas.drawText(String.format(mPrecisionFormat, mValue), mCenterPoint.x, mValueOffset, mValuePaint);

        if (mHint != null) {
            canvas.drawText(mHint.toString(), mCenterPoint.x, mHintOffset, mHintPaint);
        }

        if (mUnit != null) {
            canvas.drawText(mUnit.toString(), mCenterPoint.x, mUnitOffset, mUnitPaint);
        }
    }

    private void drawArc(Canvas canvas) {
        // 绘制背景圆弧
        // 从进度圆弧结束的地方开始重新绘制，优化性能
        canvas.save();
        float currentAngle = mSweepAngle * mPercent;
        //以圆心（x,y）为中心，旋转angle度，顺时针方向为正方向
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);
        canvas.drawArc(mRectF, currentAngle, mSweepAngle - currentAngle, false, mBgArcPaint);
        // 第一个参数 oval 为 RectF 类型，即圆弧显示区域
        // startAngle 和 sweepAngle  均为 float 类型，分别表示圆弧起始角度和圆弧度数
        // 3点钟方向为0度(即x轴正方向)，顺时针递增
        // 如果 startAngle < 0 或者 > 360,则相当于 startAngle % 360
        // useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
        canvas.drawArc(mRectF, 0, currentAngle, false, mArcPaint);
        canvas.restore();
    }
    /**
     * 更新圆弧画笔
     */
    private void updateArcPaint() {
        // 设置渐变
        mSweepGradient = new SweepGradient(mCenterPoint.x-mStrokeWith, mCenterPoint.y-mStrokeWith, mGradientColors, null);
        mArcPaint.setShader(mSweepGradient);
    }

    public boolean isAntiAlias() {
        return antiAlias;
    }

    public CharSequence getHint() {
        return mHint;
    }

    public void setHint(CharSequence hint) {
        mHint = hint;
    }

    public CharSequence getUnit() {
        return mUnit;
    }

    public void setUnit(CharSequence unit) {
        mUnit = unit;
    }

    public float getValue() {
        return mValue;
    }

    /**
     * 设置当前值
     *
     * @param value
     */
    public void setValue(float value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mAnimTime);
    }

    private void startAnimator(float start, float end, long animTime) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                mValue = mPercent * mMaxValue;
                invalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * 获取最大值
     *
     * @return
     */
    public float getMaxValue() {
        return mMaxValue;
    }

    /**
     * 设置最大值
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        mMaxValue = maxValue;
    }

    /**
     * 获取精度
     *
     * @return
     */
    public int getPrecision() {
        return mPrecision;
    }

    public void setPrecision(int precision) {
        mPrecision = precision;
        mPrecisionFormat =getPrecisionFormat(precision);
    }

    public int[] getGradientColors() {
        return mGradientColors;
    }

    /**
     * 设置渐变
     *
     * @param gradientColors
     */
    public void setGradientColors(int[] gradientColors) {
        mGradientColors = gradientColors;
        updateArcPaint();
    }

    public long getAnimTime() {
        return mAnimTime;
    }

    public void setAnimTime(long animTime) {
        mAnimTime = animTime;
    }

    /**
     * 重置
     */
    public void reset() {
        startAnimator(mPercent, 0.0f, 1000L);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //释放资源
    }
    /**
     * 获取数值精度格式化字符串
     *
     * @param precision
     * @return
     */
    private String getPrecisionFormat(int precision) {
        return "%." + precision + "f";
    }
    /**
     * 测量文字高度
     * @param paint
     * @return
     */
    private float measureTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (Math.abs(fontMetrics.ascent) - fontMetrics.descent);
    }
}