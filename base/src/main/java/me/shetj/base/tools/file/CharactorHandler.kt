package me.shetj.base.tools.file

import androidx.annotation.Keep
import android.text.InputFilter
import android.text.Spanned

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.experimental.and

/**
 */
@Keep
object CharactorHandler {
    val emojiFilter: InputFilter = object : InputFilter {
        //emoji过滤器
        internal var emoji = Pattern.compile(
                "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE)

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int,
                            dend: Int): CharSequence? {

            val emojiMatcher = emoji.matcher(source)
            return if (emojiMatcher.find()) {
                ""
            } else null
        }
    }


    /**
     * 字符串转换成十六进制字符串
     *
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    fun str2HexStr(str: String): String {

        val chars = "0123456789ABCDEF".toCharArray()
        val sb = StringBuilder("")
        val bs = str.toByteArray()
        var bit: Int

        for (i in bs.indices) {
            bit = bs[i].toInt() and 0x0f0 shr 4
            sb.append(chars[bit])
            bit = bs[i].toInt() and 0x0f
            sb.append(chars[bit])
        }
        return sb.toString().trim { it <= ' ' }
    }


    /**
     * json 格式化
     * @param bodyString
     * @return
     */
    fun jsonFormat(bodyString: String): String {
        var message: String
        try {
            if (bodyString.startsWith("{")) {
                val jsonObject = JSONObject(bodyString)
                message = jsonObject.toString(4)
            } else if (bodyString.startsWith("[")) {
                val jsonArray = JSONArray(bodyString)
                message = jsonArray.toString(4)
            } else {
                message = bodyString
            }
        } catch (e: JSONException) {
            message = bodyString
        }

        return message
    }

    fun isMobileNO(mobiles: String): Boolean {
        val p = Pattern
                .compile("^[1][0-9][0-9]{9}$")
        val m = p.matcher(mobiles)
        return m.matches()
    }


    fun isPhone(mobiles: String): Boolean {
        val p = Pattern.compile("\\d{3}-\\d{8}|\\d{4}-\\d{7}|\\d{11}")
        val m = p.matcher(mobiles)
        return m.matches()
    }


    /**
     * 验证身份证号是否符合规则
     * @param text 身份证号
     * @return
     */
    fun isIdCard(text: String): Boolean {
        val regx = "[0-9]{17}x"
        val reg1 = "[0-9]{15}"
        val regex = "[0-9]{18}"
        return text.matches(regx.toRegex()) || text.matches(reg1.toRegex()) || text.matches(regex.toRegex())
    }

    /** * 检测是否有emoji表情 * @param source * @return  */
    fun containsEmoji(source: String): Boolean {                          //两种方法限制emoji
        val len = source.length
        for (i in 0 until len) {
            val codePoint = source[i]
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true
            }
        }
        return false
    }


    /**
     * 判断是否是Emoji
     * @param codePoint 比较的单个字符
     * @return
     */
    private fun isEmojiCharacter(codePoint: Char): Boolean {
        return (codePoint.toInt() == 0x0 || codePoint.toInt() == 0x9 || codePoint.toInt() == 0xA || codePoint.toInt() == 0xD
                || codePoint.toInt() >= 0x20 && codePoint.toInt() <= 0xD7FF
                || codePoint.toInt() >= 0xE000 && codePoint.toInt() <= 0xFFFD
                || codePoint.toInt() >= 0x10000 && codePoint.toInt() <= 0x10FFFF)
    }

    fun isChinese(a: Char): Boolean {
        val v = a.toInt()
        return v >= 19968 && v <= 171941
    }

    /**
     * 是不是汉字
     * @param s
     * @return
     */
    fun containsChinese(s: String?): Boolean {
        if (null == s || "" == s.trim { it <= ' ' }) return false
        for (i in 0 until s.length) {
            if (isChinese(s[i])) return true
        }
        return false
    }
}
