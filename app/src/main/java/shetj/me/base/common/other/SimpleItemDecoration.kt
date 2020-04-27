package shetj.me.base.common.other

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

class SimpleItemDecoration private constructor(context: Context, options: ItemDecorationOptions) : RecyclerView.ItemDecoration() {
    private val dividerMainPaint: Paint
    private val dividerSecondPaint: Paint
    private val orientation: Int

    // 竖直方向
    private val dividerHeight: Int
    private val dividerMarginLeft: Int
    private val dividerMarginRight: Int

    // 水平方向
    private val dividerWidth: Int
    private val dividerMarginTop: Int
    private val dividerMarginBottom: Int
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (orientation == OrientationHelper.VERTICAL) outRect.bottom = dividerHeight // 竖直方向
        else outRect.right = dividerWidth // 水平方向
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (orientation == OrientationHelper.VERTICAL) {
            // 竖直方向
            val childCount = parent.childCount
            val childWidth = parent.width
            val left = dividerMarginLeft
            val right = childWidth - dividerMarginRight
            for (i in 0 until childCount - 1) {
                val view = parent.getChildAt(i)
                val top = view.bottom.toFloat()
                val bottom = view.bottom + dividerHeight.toFloat()
                // 主色
                c.drawRect(left.toFloat(), top, right.toFloat(), bottom, dividerMainPaint)
                // 次色
                if (dividerMarginLeft > 0) c.drawRect(0f, top, left.toFloat(), bottom, dividerSecondPaint)
                if (dividerMarginRight > 0) c.drawRect(right.toFloat(), top, childWidth.toFloat(), bottom, dividerSecondPaint)
            }
        } else {
            // 水平方向
            val childCount = parent.childCount
            val childHeight = parent.height
            val top = dividerMarginTop
            val bottom = childHeight - dividerMarginBottom
            for (i in 0 until childCount - 1) {
                val view = parent.getChildAt(i)
                val left = view.right.toFloat()
                val right = view.right + dividerWidth.toFloat()
                // 主色
                c.drawRect(left, top.toFloat(), right, bottom.toFloat(), dividerMainPaint)
                // 20180205，次色暂时不考虑
            }
        }
    }

    companion object {
        fun newInstance(context: Context, options: ItemDecorationOptions): SimpleItemDecoration {
            return SimpleItemDecoration(context, options)
        }
    }

    init {
        dividerMainPaint = Paint()
        dividerMainPaint.color = ContextCompat.getColor(context,options.mainColorId)
        dividerSecondPaint = Paint()
        dividerSecondPaint.color = ContextCompat.getColor(context,options.secondColorId)
        orientation = options.orientation
        dividerHeight = options.dividerHeight
        dividerMarginLeft = options.dividerMarginLeft
        dividerMarginRight = options.dividerMarginRight
        dividerWidth = options.dividerWidth
        dividerMarginTop = options.dividerMarginTop
        dividerMarginBottom = options.dividerMarginBottom
    }
}