/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package shetj.me.base.common.other

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter

/**
 * Project Name:LiZhiWeiKe
 * Package Name:com.lizhiweike.base.decoration
 * Created by tom on 2018/2/5 10:40 .
 *
 *
 * Copyright (c) 2016—2017 https://www.lizhiweike.com all rights reserved.
 */
class SimpleItemDecoration : RecyclerView.ItemDecoration {
    private var needTop = false

    /**
     * 头部 item 样式
     */
    private var viewTypeList: ArrayList<Int>? = null
    private var hasHeaderOptions = false
    private var headerOptions: ItemDecorationOptions? = null

    /**
     * 普通 item 样式
     */
    private var dividerMainPaint: Paint
    private var dividerSecondPaint: Paint
    private var orientation: Int
    private var isProcessEmptyStatus: Boolean

    // 竖直方向
    private var dividerHeight: Int
    private var dividerMarginLeft: Int
    private var dividerMarginRight: Int

    // 水平方向
    private var dividerWidth: Int
    private var dividerMarginTop: Int
    private var dividerMarginBottom: Int

    private constructor(context: Context, options: ItemDecorationOptions) {
        dividerMainPaint = Paint()
        dividerMainPaint.color = ContextCompat.getColor(context, options.mainColorId)
        dividerSecondPaint = Paint()
        dividerSecondPaint.color = ContextCompat.getColor(context, options.secondColorId)
        orientation = options.orientation
        dividerHeight = options.dividerHeight
        dividerMarginLeft = options.dividerMarginLeft
        dividerMarginRight = options.dividerMarginRight
        dividerWidth = options.dividerWidth
        dividerMarginTop = options.dividerMarginTop
        dividerMarginBottom = options.dividerMarginBottom
        isProcessEmptyStatus = options.isProcessEmptyStatus
        needTop = options.isNeedTop
    }

    /**
     * 允许头部 item 和内部 item 不一致
     */
    private constructor(
        context: Context,
        headerOptions: ItemDecorationOptions,
        itemOptions: ItemDecorationOptions,
        viewTypeList: ArrayList<Int>
    ) {
        dividerMainPaint = Paint()
        dividerMainPaint.color = ContextCompat.getColor(context, itemOptions.mainColorId)
        dividerSecondPaint = Paint()
        dividerSecondPaint.color = ContextCompat.getColor(context, itemOptions.secondColorId)
        orientation = itemOptions.orientation
        dividerHeight = itemOptions.dividerHeight
        dividerMarginLeft = itemOptions.dividerMarginLeft
        dividerMarginRight = itemOptions.dividerMarginRight
        dividerWidth = itemOptions.dividerWidth
        dividerMarginTop = itemOptions.dividerMarginTop
        dividerMarginBottom = itemOptions.dividerMarginBottom
        isProcessEmptyStatus = itemOptions.isProcessEmptyStatus
        hasHeaderOptions = true
        this.viewTypeList = viewTypeList
        this.headerOptions = headerOptions
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (isProcessEmptyStatus) {
            val adapter = parent.adapter
            if (adapter is BaseQuickAdapter<*, *>) {
                if (adapter.data.isNullOrEmpty()) {
                    // 没有数据的时候直接return
                    return
                }
            }
        }
        if (orientation == OrientationHelper.VERTICAL) {
            val pos = parent.getChildAdapterPosition(view)
            if (needTop && pos == 0) {
                outRect.top = dividerHeight
            }
            outRect.bottom = dividerHeight // 竖直方向
        } else {
            outRect.right = dividerWidth // 水平方向
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (isProcessEmptyStatus) {
            val adapter = parent.adapter
            if (adapter is BaseQuickAdapter<*, *>) {
                if (adapter.data.isNullOrEmpty()) {
                    // 没有数据的时候直接return
                    return
                }
            }
        }
        if (needTop) {
            handleNormal(c, parent)
        } else {
            handleNormalWithoutTop(c, parent)
        }
    }

    private fun handleNormalWithoutTop(c: Canvas, parent: RecyclerView) {
        if (orientation == OrientationHelper.VERTICAL) {
            // 竖直方向
            val childCount = parent.childCount
            val childWidth = parent.width
            var left = dividerMarginLeft
            var right = childWidth - dividerMarginRight
            for (i in 0 until childCount - 1) {
                val view = parent.getChildAt(i)
                val pos = parent.getChildAdapterPosition(view)
                if (pos <= 0) {
                    c.drawRect(0f, 0f, 0f, 0f, dividerMainPaint)
                    continue
                }
                val viewType = parent.adapter!!.getItemViewType(pos)
                val top = view.bottom.toFloat()
                var bottom = (view.bottom + dividerHeight).toFloat()
                if (hasHeaderOptions && viewTypeList!!.contains(viewType)) {
                    left = headerOptions!!.dividerMarginLeft
                    right = childWidth - headerOptions!!.dividerMarginRight
                    bottom = (view.bottom + headerOptions!!.dividerHeight).toFloat()
                } else if (right != childWidth - dividerMarginRight) {
                    left = dividerMarginLeft
                    right = childWidth - dividerMarginRight
                    bottom = (view.bottom + dividerHeight).toFloat()
                }
                // 主色
                c.drawRect(left.toFloat(), top, right.toFloat(), bottom, dividerMainPaint)
                // 次色
                if (dividerMarginLeft > 0) c.drawRect(
                    0f,
                    top,
                    left.toFloat(),
                    bottom,
                    dividerSecondPaint
                )
                if (dividerMarginRight > 0) c.drawRect(
                    right.toFloat(),
                    top,
                    childWidth.toFloat(),
                    bottom,
                    dividerSecondPaint
                )
            }
        } else {
            // 水平方向
            val childCount = parent.childCount
            val childHeight = parent.height
            var top = dividerMarginTop
            var bottom = childHeight - dividerMarginBottom
            for (i in 0 until childCount - 1) {
                val view = parent.getChildAt(i)
                val pos = parent.getChildAdapterPosition(view)
                if (pos != 0 && parent.layoutManager != null &&
                    parent.layoutManager!!.itemCount - 1 != pos
                ) {
                    val viewType = parent.adapter!!.getItemViewType(pos)
                    val left = view.right.toFloat()
                    var right = (view.right + dividerWidth).toFloat()
                    if (hasHeaderOptions && viewTypeList!!.contains(viewType)) {
                        top = headerOptions!!.dividerMarginTop
                        right = (view.right + headerOptions!!.dividerWidth).toFloat()
                        bottom = childHeight - headerOptions!!.dividerHeight
                    } else if (top != dividerMarginTop) {
                        top = dividerMarginTop
                        right = (view.right + dividerWidth).toFloat()
                        bottom = childHeight - dividerMarginBottom
                    }

                    // 主色
                    c.drawRect(left, top.toFloat(), right, bottom.toFloat(), dividerMainPaint)
                    // 20180205，次色暂时不考虑
                }
            }
        }
    }

    private fun handleNormal(c: Canvas, parent: RecyclerView) {
        if (orientation == OrientationHelper.VERTICAL) {
            // 竖直方向
            val childCount = parent.childCount
            val childWidth = parent.width
            var left = dividerMarginLeft
            var right = childWidth - dividerMarginRight
            for (i in 0 until childCount - 1) {
                val view = parent.getChildAt(i)
                val pos = parent.getChildAdapterPosition(view)
                val viewType = parent.adapter!!.getItemViewType(pos)
                val top = view.bottom.toFloat()
                var bottom = (view.bottom + dividerHeight).toFloat()
                if (hasHeaderOptions && viewTypeList!!.contains(viewType)) {
                    left = headerOptions!!.dividerMarginLeft
                    right = childWidth - headerOptions!!.dividerMarginRight
                    bottom = (view.bottom + headerOptions!!.dividerHeight).toFloat()
                } else if (right != childWidth - dividerMarginRight) {
                    left = dividerMarginLeft
                    right = childWidth - dividerMarginRight
                    bottom = (view.bottom + dividerHeight).toFloat()
                }
                // 主色
                c.drawRect(left.toFloat(), top, right.toFloat(), bottom, dividerMainPaint)
                // 次色
                if (dividerMarginLeft > 0) c.drawRect(
                    0f,
                    top,
                    left.toFloat(),
                    bottom,
                    dividerSecondPaint
                )
                if (dividerMarginRight > 0) c.drawRect(
                    right.toFloat(),
                    top,
                    childWidth.toFloat(),
                    bottom,
                    dividerSecondPaint
                )
            }
        } else {
            // 水平方向
            val childCount = parent.childCount
            val childHeight = parent.height
            var top = dividerMarginTop
            var bottom = childHeight - dividerMarginBottom
            for (i in 0 until childCount - 1) {
                val view = parent.getChildAt(i)
                val pos = parent.getChildAdapterPosition(view)
                val viewType = parent.adapter!!.getItemViewType(pos)
                val left = view.right.toFloat()
                var right = (view.right + dividerWidth).toFloat()
                if (hasHeaderOptions && viewTypeList!!.contains(viewType)) {
                    top = headerOptions!!.dividerMarginTop
                    right = (view.right + headerOptions!!.dividerWidth).toFloat()
                    bottom = childHeight - headerOptions!!.dividerHeight
                } else if (top != dividerMarginTop) {
                    top = dividerMarginTop
                    right = (view.right + dividerWidth).toFloat()
                    bottom = childHeight - dividerMarginBottom
                }

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

        fun newInstance(
            context: Context,
            headerOptions: ItemDecorationOptions,
            itemOptions: ItemDecorationOptions,
            viewTypeList: ArrayList<Int>
        ): SimpleItemDecoration {
            return SimpleItemDecoration(context, headerOptions, itemOptions, viewTypeList)
        }
    }
}