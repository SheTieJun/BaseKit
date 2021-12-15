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
