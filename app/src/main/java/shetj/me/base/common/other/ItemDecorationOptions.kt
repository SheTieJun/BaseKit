package shetj.me.base.common.other

import me.shetj.base.tools.app.ArmsUtils.Companion.dp2px
import androidx.annotation.ColorRes
import shetj.me.base.R
import androidx.recyclerview.widget.OrientationHelper
import me.shetj.base.tools.app.ArmsUtils
import shetj.me.base.R.color

class ItemDecorationOptions {
    /**
     * 分割线主色，默认是weike_main_divider；
     */
    @ColorRes
    var mainColorId = color.colorAccent

    /**
     * 分割线次色，默认是白色
     *
     * 有设置dividerMargin才会生效
     */
    @ColorRes
    var secondColorId = color.white

    /**
     * 方向，默认是竖直方向
     *
     * 竖直方向：OrientationHelper.VERTICAL
     *
     * 水平方向：OrientationHelper.HORIZONTAL
     */
    var orientation = OrientationHelper.VERTICAL

    /**
     * 分割线高度，默认是1px
     */
    var dividerHeight = 1 // 默认1px

    /**
     * 分割线宽度，默认是20px
     */
    var dividerWidth = 20 // 默认20px

    /**
     * 分割线左间距，默认是10dp
     *
     * 竖直方向才生效
     */
    var dividerMarginLeft = dp2px(10f)

    /**
     * 分割线右间距，默认是0
     *
     * 竖直方向才生效
     */
    var dividerMarginRight = 0

    /**
     * 分割线顶部间距，默认是0
     *
     * 水平方向才生效
     */
    var dividerMarginTop = 0

    /**
     * 分割线底部间距，默认是0
     *
     * 水平方向才生效
     */
    var dividerMarginBottom = 0

    /**
     * 是否处理空状态情况
     *
     * true处理，false不处理
     */
    var isProcessEmptyStatus = false

    /**
     * 是否需要给第一个 item 添加顶间距
     */
    var isNeedTop = true

    /**
     * 是否需要给最后一个 item 添加底间距
     */
    var isNeedBottom = true
}