package shetj.me.base.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import shetj.me.base.R
import kotlin.math.cos
import kotlin.math.sin


class RadarChartView @JvmOverloads constructor(
    context: Context, 
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class RadarData(
        val label: String,
        val value: Float
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private val points = ArrayList<PointF>()
    private val dataPoints = ArrayList<PointF>()

    private var dataList = listOf(
        RadarData("气息稳定性发音准确度", 12f),
        RadarData("发音流畅度发音准确度", 14f),
        RadarData("声音调值发音准确度", 18f),
        RadarData("情感饱满度发音准确度", 16f),
        RadarData("发音准确度发音准确度", 14f)
    )
    
    private var animatedValues = FloatArray(5) { 0f }
    private val currentValues: FloatArray
        get() = dataList.map { it.value }.toFloatArray()
    private val currentLabels: Array<String>
        get() = dataList.map { it.label }.toTypedArray()
    
    private val maxValue = 20f
    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f
    
    private var backgroundPentagonColor = Color.parseColor("#E6E8F7")
    private var dataAreaColor = Color.parseColor("#8F92F3")
    private var gridLineColor = Color.parseColor("#CCCCCC")
    private var textColor = Color.parseColor("#666666")
    
    private var animator: ValueAnimator? = null

    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 24f
        color = Color.parseColor("#666666")
        textAlign = Paint.Align.CENTER
    }

    private val SIDES = 5 // 改为常量
    private val GRID_LAYERS = 5 // 网格层数
    
    // 添加圆环相关属性
    private var circleSpacing = 30f  // 圆环间距
    private var startColor = Color.parseColor("#E6E8F7") // 起始颜色
    private var endColor = Color.parseColor("#FFFFFF")   // 结束颜色
    
    init {
        textPaint.apply {
            textSize = 32f // 增大文字大小
            color = textColor
            textAlign = Paint.Align.CENTER
        }

        gridPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        
        initAttributes(context, attrs)
        startAnimation()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.RadarChartView).apply {
            backgroundPentagonColor = getColor(R.styleable.RadarChartView_backgroundPentagonColor, backgroundPentagonColor)
            dataAreaColor = getColor(R.styleable.RadarChartView_dataAreaColor, dataAreaColor)
            gridLineColor = getColor(R.styleable.RadarChartView_gridLineColor, gridLineColor)
            textColor = getColor(R.styleable.RadarChartView_textColor, textColor)
            circleSpacing = getDimension(R.styleable.RadarChartView_circleSpacing, circleSpacing)
            startColor = getColor(R.styleable.RadarChartView_startColor, startColor)
            endColor = getColor(R.styleable.RadarChartView_endColor, endColor)
            recycle()
        }
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener { 
                val fraction = it.animatedValue as Float
                for (i in currentValues.indices) {
                    animatedValues[i] = currentValues[i] * fraction
                }
                calculatePoints()
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = Math.min(w, h) / 2f * 0.6f
        
        calculatePoints()
    }

    private fun calculatePoints() {
        points.clear()
        dataPoints.clear()

        for (i in 0 until SIDES) {
            val angle = Math.PI * 2 / SIDES * i - Math.PI / 2
            // 计算外圈点
            val x = centerX + radius * cos(angle).toFloat()
            val y = centerY + radius * sin(angle).toFloat()
            points.add(PointF(x, y))
            
            // 计算数据点
            val value = animatedValues[i] / maxValue
            val dataX = centerX + radius * value * cos(angle).toFloat()
            val dataY = centerY + radius * value * sin(angle).toFloat()
            dataPoints.add(PointF(dataX, dataY))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 绘制同心圆
        paint.style = Paint.Style.FILL
        
        for (i in GRID_LAYERS downTo 1) {
            val ratio = i.toFloat() / GRID_LAYERS
            val currentRadius = radius * ratio
            
            // 计算当前圆的颜色
            val fraction = (GRID_LAYERS - i).toFloat() / (GRID_LAYERS - 1)
            val red = (Color.red(startColor) * (1 - fraction) + Color.red(endColor) * fraction).toInt()
            val green = (Color.green(startColor) * (1 - fraction) + Color.green(endColor) * fraction).toInt()
            val blue = (Color.blue(startColor) * (1 - fraction) + Color.blue(endColor) * fraction).toInt()
            val alpha = (Color.alpha(startColor) * (1 - fraction) + Color.alpha(endColor) * fraction).toInt()
            
            paint.color = Color.argb(alpha, red, green, blue)
            canvas.drawCircle(centerX, centerY, currentRadius - (i - 1) * circleSpacing, paint)
        }
        
        // 绘制背景五边形
        paint.apply {
            style = Paint.Style.FILL
            color = backgroundPentagonColor
        }
        
        path.reset()
        points.forEachIndexed { index, point ->
            if (index == 0) path.moveTo(point.x, point.y)
            else path.lineTo(point.x, point.y)
        }
        path.close()
        canvas.drawPath(path, paint)
        
        // 绘制数据区域
        paint.apply {
            color = dataAreaColor
            alpha = 102
        }
        
        path.reset()
        dataPoints.forEachIndexed { index, point ->
            if (index == 0) path.moveTo(point.x, point.y)
            else path.lineTo(point.x, point.y)
        }
        path.close()
        canvas.drawPath(path, paint)
        
        // 绘制文字和分数
        points.forEachIndexed { index, point ->
            val label = currentLabels[index]
            val value = currentValues[index]
            
            // 计算标签位置 - 距离更远
            val labelDistance = radius * 1.3f // 增加距离
            val angle = Math.PI * 2 / SIDES * index - Math.PI / 2
            val labelX = centerX + labelDistance * cos(angle).toFloat()
            val labelY = centerY + labelDistance * sin(angle).toFloat()
            
            // 绘制标签
            canvas.drawText(label, labelX, labelY, textPaint)
            
            // 绘制分数
            val scoreDistance = radius * 1.1f
            val scoreX = centerX + scoreDistance * cos(angle).toFloat()
            val scoreY = centerY + scoreDistance * sin(angle).toFloat()
            canvas.drawText("${value.toInt()}分", scoreX, scoreY, valuePaint)
        }
    }

    fun setData(newData: List<RadarData>) {
        require(newData.size == 5) { "RadarData must contain exactly 5 items" }
        dataList = newData
        startAnimation()
    }

    fun setDataItem(index: Int, label: String, value: Float) {
        require(index in 0..4) { "Index must be between 0 and 4" }
        require(value in 0f..maxValue) { "Value must be between 0 and $maxValue" }
        
        val newList = dataList.toMutableList()
        newList[index] = RadarData(label, value)
        dataList = newList
        startAnimation()
    }

    fun updateValue(index: Int, value: Float) {
        require(index in 0..4) { "Index must be between 0 and 4" }
        require(value in 0f..maxValue) { "Value must be between 0 and $maxValue" }
        
        val newList = dataList.toMutableList()
        newList[index] = newList[index].copy(value = value)
        dataList = newList
        startAnimation()
    }

    fun updateLabel(index: Int, label: String) {
        require(index in 0..4) { "Index must be between 0 and 4" }
        
        val newList = dataList.toMutableList()
        newList[index] = newList[index].copy(label = label)
        dataList = newList
        invalidate()
    }
} 