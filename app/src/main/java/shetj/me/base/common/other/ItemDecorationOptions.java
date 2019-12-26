package shetj.me.base.common.other;


import androidx.annotation.ColorRes;
import androidx.recyclerview.widget.OrientationHelper;

import me.shetj.base.tools.app.ArmsUtils;
import shetj.me.base.R;

/**
 * Project Name:LiZhiWeiKe
 * Package Name:com.lizhiweike.base.decoration
 * Created by tom on 2018/2/5 11:38 .
 * <p>
 * Copyright (c) 2016—2017 https://www.lizhiweike.com all rights reserved.
 */
public class ItemDecorationOptions {
    /**
     * 分割线主色，默认是weike_main_divider；
     */
    @ColorRes
    public int mainColorId = R.color.colorAccent;
    /**
     * 分割线次色，默认是白色
     * <p>有设置dividerMargin才会生效</p>
     */
    @ColorRes
    public int secondColorId = R.color.white;
    /**
     * 方向，默认是竖直方向
     * <p>竖直方向：OrientationHelper.VERTICAL</p>
     * <p>水平方向：OrientationHelper.HORIZONTAL</p>
     */
    public int orientation = OrientationHelper.VERTICAL;
    /**
     * 分割线高度，默认是1px
     */
    public int dividerHeight = 1;// 默认1px
    /**
     * 分割线宽度，默认是20px
     */
    public int dividerWidth = 20;// 默认20px
    /**
     * 分割线左间距，默认是10dp
     * <p>竖直方向才生效</p>
     */
    public int dividerMarginLeft = ArmsUtils.dip2px(10);
    /**
     * 分割线右间距，默认是0
     * <p>竖直方向才生效</p>
     */
    public int dividerMarginRight = 0;
    /**
     * 分割线顶部间距，默认是0
     * <p>水平方向才生效</p>
     */
    public int dividerMarginTop = 0;
    /**
     * 分割线底部间距，默认是0
     * <p>水平方向才生效</p>
     */
    public int dividerMarginBottom = 0;
    /**
     * 是否处理空状态情况
     * <p>true处理，false不处理</p>
     */
    public boolean isProcessEmptyStatus = false;
    /**
     * 是否需要给第一个 item 添加顶间距
     */
    public boolean isNeedTop = true;
    /**
     * 是否需要给最后一个 item 添加底间距
     */
    public boolean isNeedBottom = true;
}
