package me.shetj.base.tools.file

import java.util.regex.Pattern
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/8/16
 * desc  : 字符串相关工具类
</pre> *
 */
class StringUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 判断字符串是否为null或长度为0
         *
         * @param s 待校验字符串
         * @return `true`: 空<br></br> `false`: 不为空
         */
        @JvmStatic
        fun isEmpty(s: CharSequence?): Boolean {
            return s == null || s.isEmpty()
        }

        /**
         * 判断字符串是否为null或全为空格
         *
         * @param s 待校验字符串
         * @return `true`: null或全空格<br></br> `false`: 不为null且不全空格
         */
        @JvmStatic
        fun isSpace(s: String?): Boolean {
            return s == null || s.trim { it <= ' ' }.isEmpty()
        }

        /**
         * 判断两字符串是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return `true`: 相等<br></br>`false`: 不相等
         */
        @JvmStatic
        fun equals(a: CharSequence, b: CharSequence): Boolean {
            if (a === b) return true
            val length = a.length
            if (length == b.length) {
                if (a is String && b is String) {
                    return a == b
                } else {
                    for (i in 0 until length) {
                        if (a[i] != b[i]) return false
                    }
                    return true
                }
            }
            return false
        }

        /**
         * 判断两字符串忽略大小写是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return `true`: 相等<br></br>`false`: 不相等
         */
        @JvmStatic
        fun equalsIgnoreCase(a: String, b: String?): Boolean {
            return a == b || b != null && a.length == b.length && a.regionMatches(
                0,
                b,
                0,
                b.length,
                ignoreCase = true
            )
        }

        /**
         * null转为长度为0的字符串
         *
         * @param s 待转字符串
         * @return s为null转为长度为0字符串，否则不改变
         */
        @JvmStatic
        fun null2Length0(s: String?): String {
            return s ?: ""
        }

        /**
         * 返回字符串长度
         *
         * @param s 字符串
         * @return null返回0，其他返回自身长度
         */
        @JvmStatic
        fun length(s: CharSequence?): Int {
            return s?.length ?: 0
        }

        /**
         * 首字母大写
         *
         * @param s 待转字符串
         * @return 首字母大写字符串
         */
        @JvmStatic
        fun upperFirstLetter(s: String): String {
            return if (isEmpty(s) || !Character.isLowerCase(s[0])) s else (s[0].code - 32).toChar()
                .toString() + s.substring(1)
        }

        /**
         * 首字母小写
         *
         * @param s 待转字符串
         * @return 首字母小写字符串
         */
        @JvmStatic
        fun lowerFirstLetter(s: String): String {
            return if (isEmpty(s) || !Character.isUpperCase(s[0])) {
                s
            } else (s[0].code + 32).toChar().toString() + s.substring(1)
        }

        /**
         * 反转字符串
         *
         * @param s 待反转字符串
         * @return 反转字符串
         */
        @JvmStatic
        fun reverse(s: String): String {
            val len = length(s)
            if (len <= 1) return s
            val mid = len shr 1
            val chars = s.toCharArray()
            var c: Char
            for (i in 0 until mid) {
                c = chars[i]
                chars[i] = chars[len - i - 1]
                chars[len - i - 1] = c
            }
            return String(chars)
        }

        /**
         * 转化为半角字符
         *
         * @param s 待转字符串
         * @return 半角字符串
         */
        @JvmStatic
        fun toDBC(s: String): String {
            if (isEmpty(s)) {
                return s
            }
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                when (chars[i].code) {
                    12288 -> {
                        chars[i] = ' '
                    }
                    in 65281..65374 -> {
                        chars[i] = (chars[i].code - 65248).toChar()
                    }
                    else -> {
                        chars[i] = chars[i]
                    }
                }
                i++
            }
            return String(chars)
        }

        /**
         * 转化为全角字符
         *
         * @param s 待转字符串
         * @return 全角字符串
         */
        @JvmStatic
        fun toSBC(s: String): String {
            if (isEmpty(s)) {
                return s
            }
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                when {
                    chars[i] == ' ' -> {
                        chars[i] = 12288.toChar()
                    }
                    chars[i].code in 33..126 -> {
                        chars[i] = (chars[i].code + 65248).toChar()
                    }
                    else -> {
                        chars[i] = chars[i]
                    }
                }
                i++
            }
            return String(chars)
        }

        /**
         * 字符串转换成十六进制字符串
         *
         * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
         */
        @JvmStatic
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
        @JvmStatic
        fun jsonFormat(bodyString: String): String {
            return try {
                when {
                    bodyString.startsWith("{") -> {
                        val jsonObject = JSONObject(bodyString)
                        jsonObject.toString(4)
                    }
                    bodyString.startsWith("[") -> {
                        val jsonArray = JSONArray(bodyString)
                        jsonArray.toString(4)
                    }
                    else -> {
                        bodyString
                    }
                }
            } catch (e: JSONException) {
                bodyString
            }
        }

        @JvmStatic
        fun isMobileNO(mobiles: String): Boolean {
            val p = Pattern
                .compile("^[1][0-9][0-9]{9}$")
            val m = p.matcher(mobiles)
            return m.matches()
        }

        @JvmStatic
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
        @JvmStatic
        fun isIdCard(text: String): Boolean {
            val regx = "[0-9]{17}x"
            val reg1 = "[0-9]{15}"
            val regex = "[0-9]{18}"
            return text.matches(regx.toRegex()) ||
                    text.matches(reg1.toRegex()) || text.matches(regex.toRegex())
        }

        /** * 检测是否有emoji表情 * @param source * @return  */
        @JvmStatic
        fun containsEmoji(source: String): Boolean { // 两种方法限制emoji
            val len = source.length
            for (i in 0 until len) {
                val codePoint = source[i]
                if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
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
            return (
                    codePoint.code == 0x0 || codePoint.code == 0x9 ||
                            codePoint.code == 0xA || codePoint.code == 0xD ||
                            codePoint.code in 0x20..0xD7FF ||
                            codePoint.code in 0xE000..0xFFFD ||
                            codePoint.code in 0x10000..0x10FFFF
                    )
        }

        @JvmStatic
        fun isChinese(a: Char): Boolean {
            val v = a.code
            return v in 19968..171941
        }

        /**
         * 是不是汉字
         * @param s
         * @return
         */
        @JvmStatic
        fun containsChinese(s: String?): Boolean {
            if (null == s || "" == s.trim { it <= ' ' }) return false
            for (element in s) {
                if (isChinese(element)) return true
            }
            return false
        }

        @JvmStatic
        fun numberToCH(intInput: Int): String {
            val si = intInput.toString()
            var sd = ""
            when (si.length) {
                1 -> { // 个
                    sd += getCH(intInput)
                    return sd
                }
                2 -> {// 十
                    sd += if (si.substring(0, 1) == "1") "十" else getCH(intInput / 10) + "十"
                    sd += numberToCH(intInput % 10)
                }
                3 -> {// 百
                    sd += getCH(intInput / 100) + "百"
                    if ((intInput % 100).toString().length < 2) sd += "零"
                    sd += numberToCH(intInput % 100)
                }
                4 -> {// 千
                    sd += getCH(intInput / 1000) + "千"
                    if ((intInput % 1000).toString().length < 3) sd += "零"
                    sd += numberToCH(intInput % 1000)
                }
                5 -> {// 万
                    sd += getCH(intInput / 10000) + "万"
                    if ((intInput % 10000).toString().length < 4) sd += "零"
                    sd += numberToCH(intInput % 10000)
                }
            }
            return sd
        }

        private fun getCH(input: Int): String {
            var sd = ""
            when (input) {
                1 -> sd = "一"
                2 -> sd = "二"
                3 -> sd = "三"
                4 -> sd = "四"
                5 -> sd = "五"
                6 -> sd = "六"
                7 -> sd = "七"
                8 -> sd = "八"
                9 -> sd = "九"
                else -> {}
            }
            return sd
        }
    }
}
