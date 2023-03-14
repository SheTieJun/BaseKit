package me.shetj.base.tools.json

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import me.shetj.base.R

/**
 * 创建带高亮文字的字符串
 */
@Keep
class HighStringFormatUtil(
    private val wholeStr: String,
    private val highlightStr: String,
    @ColorInt private val color: Int
) {
    private var start = 0
    private var end = 0

    fun fillColor(): SpannableStringBuilder {
        if (!TextUtils.isEmpty(wholeStr) && !TextUtils.isEmpty(highlightStr)) {
            if (wholeStr.contains(highlightStr)) {
                start = wholeStr.indexOf(highlightStr)
                end = start + highlightStr.length
            }
        }
        val spBuilder = SpannableStringBuilder(wholeStr)
        val charaStyle = ForegroundColorSpan(color)
        spBuilder.setSpan(charaStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spBuilder
    }

    companion object {
        /**
         * @param context 上下文
         * @param wholeStr 全部文字
         * @param highlightStr 改变颜色的文字
         * @param color 颜色
         */
        @JvmStatic
        fun buildLightString(
            context: Context,
            wholeStr: String,
            highlightStr: String,
            @ColorRes color: Int = R.color.colorAccent
        ): SpannableStringBuilder {
            return HighStringFormatUtil(
                wholeStr, highlightStr,
                ContextCompat.getColor(context, color)
            ).fillColor()
        }

        @JvmStatic
        fun buildLightString(
            wholeStr: String,
            highlightStr: String,
            @ColorInt color: Int = Color.YELLOW
        ): SpannableStringBuilder {
            return HighStringFormatUtil(wholeStr, highlightStr, color).fillColor()
        }
    }
}
