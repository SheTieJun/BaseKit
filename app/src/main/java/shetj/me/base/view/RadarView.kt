package shetj.me.base.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import shetj.me.base.R
import kotlin.math.cos
import kotlin.math.sin

class RadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    data class RadarData(
        val label: String,
        val value: Float
    )

    private val SIDES = 5 // 改为常量


    private val circleColor = ContextCompat.getColor(context, R.color.circle_color)
    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val circlePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = circleColor
    }
    private val path = Path()
    private var dataList = listOf(
        RadarData("气息稳定性气息稳定性", 12f),
        RadarData("发音流畅度发音流畅度", 14f),
        RadarData("声音调值声音调值", 18f),
        RadarData("情感饱满度情感饱满度", 16f),
        RadarData("发音准确度发音准确度", 20f)
    )

    private val currentValues: FloatArray
        get() = dataList.map { it.value }.toFloatArray()
    private val currentLabels: Array<String>
        get() = dataList.map { it.label }.toTypedArray()

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private val circleCount = 5 // 圆圈数量
    private var circleSpacing = 20f // 圆圈间距
    private val circleRadius = 6f // 顶点圆的半径

    private val points = ArrayList<PointF>()
    private val dataPoints = ArrayList<PointF>()
    private val maxValue = 20f
    private var animator: ValueAnimator? = null
    private var animatedValues = FloatArray(5) { 0f }

    init {
        startAnimation()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f + 20
        radius = Math.min(centerX, centerY) - 170
        circleSpacing = (radius - 5) / circleCount
        calculatePoints()
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                val fraction = it.animatedValue as Float
                for (i in currentValues.indices) {
                    animatedValues[i] = (currentValues[i] * fraction)
                }
                calculatePoints()
                invalidate()
            }
            start()
        }
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

        for (i in 0 until circleCount) {
            val circleRadius = radius - i * circleSpacing
            canvas.drawCircle(centerX.toFloat(), centerY, circleRadius.toFloat(), circlePaint)
        }


        // 画从圆心到每个顶点的线条
        paint.color = ContextCompat.getColor(context, R.color.c_2)
        points.forEachIndexed { index, point ->
            canvas.drawLine(centerX.toFloat(), centerY.toFloat(), point.x, point.y, paint)
        }


        paint.color = ContextCompat.getColor(context, R.color.c_3)
        paint.style = Paint.Style.FILL
        // 画多边形
        path.reset()
        dataPoints.forEachIndexed { index, point ->
            if (index == 0) path.moveTo(point.x, point.y)
            else path.lineTo(point.x, point.y)

            canvas.drawCircle(point.x, point.y, circleRadius, paint)
        }
        path.close()
        paint.color = ContextCompat.getColor(context, R.color.c_1)
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)
        paint.color = ContextCompat.getColor(context, R.color.c_3)
        paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE
        canvas.drawPath(path, paint)

        // 画标签和分数




        points.forEachIndexed { index, point ->
            val label = currentLabels[index]
            val value = currentValues[index]
            val labelWidth = paint.measureText(label)
            val scoreWidth = paint.measureText(value.toString() + "分")
            // 计算标签位置 - 距离更远
            val labelDistance = radius + 100 // 增加距离
            val angle = Math.PI * 2 / SIDES * index - Math.PI / 2
            val labelX = centerX + labelDistance * cos(angle).toFloat()
            val labelY = centerY + labelDistance * sin(angle).toFloat()

            //设置画笔的描边宽度值
            paint.strokeWidth = 0.9f
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.textSize = 32f
            paint.color = ContextCompat.getColor(context, R.color.black)

            // 绘制标签
            canvas.drawText(label, labelX - labelWidth / 2, labelY, paint)
            paint.textSize = 35f
            paint.color = ContextCompat.getColor(context, R.color.c_3)
            canvas.drawText("${value.toInt()}分", labelX - scoreWidth / 2f, labelY + 45, paint)
        }

    }

    fun setData(newData: List<RadarData>) {
        require(newData.size == 5) { "RadarData must contain exactly 5 items" }
        dataList = newData
        startAnimation()
    }
}
