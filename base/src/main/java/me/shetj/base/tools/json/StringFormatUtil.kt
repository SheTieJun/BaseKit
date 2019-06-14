package me.shetj.base.tools.json

import android.content.Context
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan

@Keep
class StringFormatUtil
/**
 *
 * @param mContext 上下文
 * @param wholeStr 全部文字
 * @param highlightStr 改变颜色的文字
 * @param color 颜色
 */
(private val mContext: Context, private val wholeStr: String, private val highlightStr: String, private var color: Int) {
    private var spBuilder: SpannableStringBuilder? = null
    private var start = 0
    private var end = 0

    fun fillColor(): SpannableStringBuilder? {
        if (!TextUtils.isEmpty(wholeStr) && !TextUtils.isEmpty(highlightStr)) {
            if (wholeStr.contains(highlightStr)) {
                start = wholeStr.indexOf(highlightStr)
                end = start + highlightStr.length
            }
        }
        spBuilder = SpannableStringBuilder(wholeStr)
        color = ContextCompat.getColor(mContext, color)
        val charaStyle = ForegroundColorSpan(color)
        spBuilder!!.setSpan(charaStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spBuilder
    }
}  