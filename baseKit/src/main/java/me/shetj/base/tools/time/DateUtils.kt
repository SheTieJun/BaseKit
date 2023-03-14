package me.shetj.base.tools.time

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * 日期工具类
 */
@SuppressLint("SimpleDateFormat")
object DateUtils {

    /**
     * 英文简写如：2010
     */
    var FORMAT_Y = "yyyy"

    /**
     * 英文简写如：12:01
     */
    var FORMAT_HM = "HH:mm"

    /**
     * 英文简写如：1-12 12:01
     */
    var FORMAT_MDHM = "MM-dd HH:mm"

    /**
     * 英文简写（默认）如：2010-12-01
     */
    var FORMAT_YMD = "yyyy-MM-dd"

    /**
     * 英文全称  如：2010-12-01 23:15
     */
    var FORMAT_YMDHM = "yyyy-MM-dd HH:mm"

    /**
     * 英文全称  如：2010-12-01 23:15:06
     */
    /**
     * 获得默认的 date pattern
     * @return 默认的格式
     */
    var datePattern = "yyyy-MM-dd HH:mm:ss"

    /**
     * 精确到毫秒的完整时间    如：yyyy-MM-dd HH:mm:ss.S
     */
    var FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S"

    /**
     * 精确到毫秒的完整时间    如：yyyy-MM-dd HH:mm:ss.S
     */
    var FORMAT_FULL_SN = "yyyyMMddHHmmssS"

    /**
     * 中文简写  如：2010年12月01日
     */
    var FORMAT_YMD_CN = "yyyy年MM月dd日"

    /**
     * 中文简写  如：2010年12月01日  12时
     */
    var FORMAT_YMDH_CN = "yyyy年MM月dd日 HH时"

    /**
     * 中文简写  如：2010年12月01日  12时12分
     */
    var FORMAT_YMDHM_CN = "yyyy年MM月dd日 HH时mm分"

    /**
     * 中文全称  如：2010年12月01日  23时15分06秒
     */
    var FORMAT_YMDHMS_CN = "yyyy年MM月dd日  HH时mm分ss秒"

    /**
     * 精确到毫秒的完整中文时间
     */
    var FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒"

    var ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    private var calendar: Calendar? = null
    private const val FORMAT = "yyyy-MM-dd HH:mm:ss"

    @JvmStatic
    val curDateStr: String
        get() {
            val c = Calendar.getInstance()
            c.time = Date()
            return c.get(Calendar.YEAR).toString() + "-" + (c.get(Calendar.MONTH) + 1) + "-" +
                c.get(Calendar.DAY_OF_MONTH) + "-" +
                c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) +
                ":" + c.get(Calendar.SECOND)
        }

    /**
     * 获取时间戳
     * @return 获取时间戳
     */
    @JvmStatic
    val timeString: String
        get() {
            val df = SimpleDateFormat(FORMAT_FULL)
            val calendar = Calendar.getInstance()
            return df.format(calendar.time)
        }

    @JvmStatic
    @JvmOverloads
    fun str2Date(str: String?, format: String? = null): Date? {
        var formatClone = format
        if (str == null || str.isEmpty()) {
            return null
        }
        if (formatClone == null || formatClone.isEmpty()) {
            formatClone = FORMAT
        }
        var date: Date? = null
        try {
            val sdf = SimpleDateFormat(formatClone)
            date = sdf.parse(str)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return date
    }

    @JvmStatic
    @JvmOverloads
    fun str2Calendar(str: String, format: String? = null): Calendar? {

        val date = str2Date(str, format) ?: return null
        val c = Calendar.getInstance()
        c.time = date

        return c
    }

    @JvmStatic
    @JvmOverloads
    fun date2Str(c: Calendar?, format: String? = null): String? {
        return if (c == null) {
            null
        } else date2Str(c.time, format)
    }

    @JvmStatic
    @JvmOverloads
    fun date2Str(d: Date?, format: String? = null): String? { // yyyy-MM-dd HH:mm:ss
        var formatClone = format
        if (d == null) {
            return null
        }
        if (formatClone == null || formatClone.isEmpty()) {
            formatClone = FORMAT
        }
        val sdf = SimpleDateFormat(formatClone)
        return sdf.format(d)
    }

    /**
     * 获得当前日期的字符串格式
     * @param format    格式化的类型
     * @return 返回格式化之后的事件
     */
    @JvmStatic
    fun getCurDateStr(format: String): String? {
        val c = Calendar.getInstance()
        return date2Str(c, format)
    }

    /**
     *
     * @param time 当前的时间
     * @return 格式到秒
     */
    @JvmStatic
    fun getMillon(time: Long): String {

        return SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(time)
    }

    /**
     *
     * @param time  当前的时间
     * @return 当前的天
     */
    @JvmStatic
    fun getDay(time: Long): String {

        return SimpleDateFormat("yyyy-MM-dd").format(time)
    }

    /**
     *
     * @param time 时间
     * @return 返回一个毫秒
     */
    @JvmStatic
    fun getSMillon(time: Long): String {
        return SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(time)
    }

    /**
     * 在日期上增加数个整月
     * @param date 日期
     * @param n 要增加的月数
     * @return 增加数个整月
     */
    @JvmStatic
    @JvmOverloads
    fun addMonth(date: Date, n: Int = 1): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.MONTH, n)
        return cal.time
    }

    /**
     * 在日期上增加天数
     * @param date 日期
     * @param n 要增加的天数
     * @return 增加之后的天数
     */
    @JvmStatic
    @JvmOverloads
    fun addDay(date: Date, n: Int = 1): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DATE, n)
        return cal.time
    }

    /**
     * 获取距现在某一小时的时刻
     *
     * @param format 格式化时间的格式
     * @param h 距现在的小时 例如：h=-1为上一个小时，h=1为下一个小时
     * @return 获取距现在某一小时的时刻
     */
    @JvmStatic
    fun getNextHour(format: String, h: Int): String {
        val sdf = SimpleDateFormat(format)
        val date = Date()
        date.time = date.time + h * 60 * 60 * 1000
        return sdf.format(date)
    }

    /**
     * 功能描述：返回月
     *
     * @param date Date 日期
     * @return 返回月份
     */
    @JvmStatic
    fun getMonth(date: Date): Int {
        calendar = Calendar.getInstance()
        calendar!!.time = date
        return calendar!!.get(Calendar.MONTH) + 1
    }

    /**
     * 功能描述：返回日
     *
     * @param date Date 日期
     * @return 返回日份
     */
    @JvmStatic
    fun getDay(date: Date): Int {
        calendar = Calendar.getInstance()
        calendar!!.time = date
        return calendar!!.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 功能描述：返回小
     *
     * @param date 日期
     * @return 返回小时
     */
    @JvmStatic
    fun getHour(date: Date): Int {
        calendar = Calendar.getInstance()
        calendar!!.time = date
        return calendar!!.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * 功能描述：返回分
     *
     * @param date 日期
     * @return 返回分钟
     */
    @JvmStatic
    fun getMinute(date: Date): Int {
        calendar = Calendar.getInstance()
        calendar!!.time = date
        return calendar!!.get(Calendar.MINUTE)
    }

    /**
     * 返回秒钟
     *
     * @param date Date 日期
     * @return 返回秒钟
     */
    @JvmStatic
    fun getSecond(date: Date): Int {
        calendar = Calendar.getInstance()

        calendar!!.time = date
        return calendar!!.get(Calendar.SECOND)
    }

    /**
     * 功能描述：返回毫
     *
     * @param date 日期
     * @return 返回毫
     */
    @JvmStatic
    fun getMillis(date: Date): Long {
        calendar = Calendar.getInstance()
        calendar!!.time = date
        return calendar!!.timeInMillis
    }

    /**
     * 按默认格式的字符串距离今天的天数
     *
     * @param dateString 日期字符串
     * @return 按默认格式的字符串距离今天的天数
     */
    @JvmStatic
    fun countDays(dateString: String): Int {
        val t = Calendar.getInstance().time.time
        val c = Calendar.getInstance()
        c.time = parse(dateString) ?: return -1
        val t1 = c.time.time
        return (t / 1000 - t1 / 1000).toInt() / 3600 / 24
    }

    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return 提取字符串日期
     */
    @JvmOverloads
    fun parse(strDate: String, pattern: String = datePattern): Date? {
        val df = SimpleDateFormat(pattern)
        return try {
            df.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 按用户格式字符串距离今天的天数
     *
     * @param strDate 日期字符串
     * @param format 日期格式
     * @return 按用户格式字符串距离今天的天数 -1等于字符错误
     */
    @JvmStatic
    fun countDays(strDate: String, format: String): Int {
        val t = Calendar.getInstance().time.time
        val c = Calendar.getInstance()
        c.time = parse(strDate, format) ?: return -1
        val t1 = c.time.time
        return (t / 1000 - t1 / 1000).toInt() / 3600 / 24
    }
}
