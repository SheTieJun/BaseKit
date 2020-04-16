package shetj.me.base.common.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * Project Name:LiZhiWeiKe
 * Package Name:com.lizhiweike.base.decoration
 * Created by tom on 2018/2/5 10:40 .
 * <p>
 * Copyright (c) 2016—2017 https://www.lizhiweike.com all rights reserved.
 */
public class SimpleItemDecoration extends RecyclerView.ItemDecoration {
    private Paint dividerMainPaint;
    private Paint dividerSecondPaint;
    private int orientation;
    // 竖直方向
    private int dividerHeight;
    private int dividerMarginLeft;
    private int dividerMarginRight;
    // 水平方向
    private int dividerWidth;
    private int dividerMarginTop;
    private int dividerMarginBottom;

    private SimpleItemDecoration(Context context, @NonNull ItemDecorationOptions options) {
        dividerMainPaint = new Paint();
        dividerMainPaint.setColor(context.getResources().getColor(options.mainColorId));
        dividerSecondPaint = new Paint();
        dividerSecondPaint.setColor(context.getResources().getColor(options.secondColorId));
        orientation = options.orientation;
        dividerHeight = options.dividerHeight;
        dividerMarginLeft = options.dividerMarginLeft;
        dividerMarginRight = options.dividerMarginRight;
        dividerWidth = options.dividerWidth;
        dividerMarginTop = options.dividerMarginTop;
        dividerMarginBottom = options.dividerMarginBottom;
    }

    public static SimpleItemDecoration newInstance(Context context, @NonNull ItemDecorationOptions options) {
        return new SimpleItemDecoration(context, options);
    }


    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        if (orientation == OrientationHelper.VERTICAL) outRect.bottom = dividerHeight;// 竖直方向
        else outRect.right = dividerWidth;// 水平方向
    }

    @Override
    public void onDraw(@NotNull Canvas c, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (orientation == OrientationHelper.VERTICAL) {
            // 竖直方向
            int childCount = parent.getChildCount();
            int childWidth = parent.getWidth();
            int left = dividerMarginLeft;
            int right = childWidth - dividerMarginRight;
            for (int i = 0; i < childCount - 1; i++) {
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + dividerHeight;
                // 主色
                c.drawRect(left, top, right, bottom, dividerMainPaint);
                // 次色
                if (dividerMarginLeft > 0)
                    c.drawRect(0, top, left, bottom, dividerSecondPaint);
                if (dividerMarginRight > 0)
                    c.drawRect(right, top, childWidth, bottom, dividerSecondPaint);
            }
        } else {
            // 水平方向
            int childCount = parent.getChildCount();
            int childHeight = parent.getHeight();
            int top = dividerMarginTop;
            int bottom = childHeight - dividerMarginBottom;
            for (int i = 0; i < childCount - 1; i++) {
                View view = parent.getChildAt(i);
                float left = view.getRight();
                float right = view.getRight() + dividerWidth;
                // 主色
                c.drawRect(left, top, right, bottom, dividerMainPaint);
                // 20180205，次色暂时不考虑
            }
        }
    }
}
