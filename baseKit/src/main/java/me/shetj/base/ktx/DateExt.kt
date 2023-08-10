package me.shetj.base.ktx

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.collections.ArrayList

fun Date?.getWeekOfDate(
    aLocale: Locale = Locale.getDefault(),
    weekDaysName: Array<String> = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
): String? {
    this?.apply {
//        val weekDaysName = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        // String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        val calendar = Calendar.getInstance(aLocale)
        calendar.time = this
        val intWeek = calendar[Calendar.DAY_OF_WEEK] - 1
        return weekDaysName[intWeek]
    }
    return null
}

/**
 *  字符串日期格式（比如：2018-4-6)转为毫秒
 *  @param format 时间的格式，默认是按照yyyy-MM-dd HH:mm:ss来转换，如果您的格式不一样，则需要传入对应的格式
 */
fun String.toDateMills(format: String = "yyyy-MM-dd HH:mm:ss"): Long? =
    SimpleDateFormat(format, Locale.getDefault()).parse(this)?.time

/**
 * Long类型时间戳转为字符串的日期格式
 * @param format 时间的格式，默认是按照yyyy-MM-dd HH:mm:ss来转换，如果您的格式不一样，则需要传入对应的格式
 */
fun Long.toDateString(format: String = "yyyy-MM-dd HH:mm:ss"): String =
    SimpleDateFormat(format, Locale.getDefault()).format(Date(this))

fun Int.toDateString(format: String = "yyyy-MM-dd HH:mm:ss"): String =
    SimpleDateFormat(format, Locale.getDefault()).format(Date(this.toLong()))

/**
 * 获取当前日期几月几号
 */
fun getDateString(): String {
    val c = Calendar.getInstance(Locale.getDefault())
    return (c[Calendar.MONTH] + 1).toString() + "月" + c[Calendar.DAY_OF_MONTH].toString() + "日"
}

/**
 * 获取当前年月日
 */
fun getStringData(): String {
    val c = Calendar.getInstance(Locale.getDefault())
    return c[Calendar.YEAR].toString() + "-" + (c[Calendar.MONTH] + 1).toString() +
            "-" + c[Calendar.DAY_OF_MONTH].toString()
}

/**
 * 获取当前年
 */
fun getStringYear(): String {
    val c = Calendar.getInstance(Locale.getDefault())
    return c[Calendar.YEAR].toString()
}

/**
 * 获取一个月前的日期
 */
fun getMonthAfter(i: Int): String {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    calendar.add(Calendar.MONTH, i)
    return (calendar[Calendar.MONTH] + 1).toString() // 获取当前月份
}

/**
 * 获取一个月前的日期
 *
 * @return
 */
fun getYearAfter(i: Int): String {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    calendar.add(Calendar.MONTH, i)
    return calendar[Calendar.YEAR].toString() // 获取当前年份
}

/**
 * 获取当前是周几
 */
fun getWeekString(): String {
    val c = Calendar.getInstance()
    return when (c[Calendar.DAY_OF_WEEK].toString()) {
        "1" -> "周天"
        "2" -> "周一"
        "3" -> "周二"
        "4" -> "周三"
        "5" -> "周四"
        "6" -> "周五"
        "7" -> "周六"
        else -> "周六"
    }
}

/**
 * 根据当前日期获得是星期几
 */
fun getWeek(time: String): String {
    var week = ""
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val c = Calendar.getInstance()
    try {
        c.time = format.parse(time)!!
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
        week += "周日"
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.MONDAY) {
        week += "周一"
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.TUESDAY) {
        week += "周二"
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.WEDNESDAY) {
        week += "周三"
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.THURSDAY) {
        week += "周四"
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.FRIDAY) {
        week += "周五"
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY) {
        week += "周六"
    }
    return week
}

/**
 * 根据当前日期获得是星期几
 */
fun getDayWeek(time: String): Int {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val c = Calendar.getInstance()
    try {
        c.time = format.parse(time)!!
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
        return 0
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.MONDAY) {
        return 1
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.TUESDAY) {
        return 2
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.WEDNESDAY) {
        return 3
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.THURSDAY) {
        return 4
    }
    if (c[Calendar.DAY_OF_WEEK] == Calendar.FRIDAY) {
        return 5
    }
    return if (c[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY) {
        6
    } else 0
}

/**
 * 获取今天往后一周的日期（年-月-日）
 */
fun get7date(): List<String> {
    val dates: MutableList<String> = ArrayList()
    val c = Calendar.getInstance()
    c.timeZone = TimeZone.getTimeZone("GMT+8:00")
    val sim = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var date = sim.format(c.time)
    dates.add(date)
    for (i in 0..5) {
        c.add(Calendar.DAY_OF_MONTH, 1)
        date = sim.format(c.time)
        dates.add(date)
    }
    return dates
}

/**
 * 获取今天往后一周的日期（几月几号）
 */
fun getSevenDate(): List<String> {
    val dates: MutableList<String> = ArrayList()
    val c = Calendar.getInstance()
    c.timeZone = TimeZone.getTimeZone("GMT+8:00")
    for (i in 0..6) {
        val date: String =
            (c[Calendar.MONTH] + 1).toString() + "月" +
                    (c[Calendar.DAY_OF_MONTH] + i).toString() + "日"
        dates.add(date)
    }
    return dates
}

fun get7dateT(): List<String> {
    val dates: MutableList<String> = ArrayList()
    val c = Calendar.getInstance()
    c.timeZone = TimeZone.getTimeZone("GMT+8:00")
    for (i in 0..6) {
        // 获取当前月份
        c.add(Calendar.DAY_OF_MONTH, 1)
        // 获取当前日份的日期号码
        val date: String = c[Calendar.DAY_OF_MONTH].toString()
        dates.add(date)
    }
    return dates
}

/**
 * 获取今天往后一周的集合
 */
fun get7week(): List<String> {
    var week: String
    val weeksList: MutableList<String> = ArrayList()
    val dateList = get7date()
    for (s in dateList) {
        week = if (s == getStringData()) {
            "今天"
        } else {
            getWeek(s)
        }
        weeksList.add(week)
    }
    return weeksList
}

fun getDate(): Int {
    val c = Calendar.getInstance()
    c.timeZone = TimeZone.getTimeZone("GMT+8:00")
    return c[Calendar.DAY_OF_MONTH]
}

fun get7dateAndToday(): List<String> {
    val dates: MutableList<String> = ArrayList()
    val c = Calendar.getInstance()
    c.timeZone = TimeZone.getTimeZone("GMT+8:00")
    for (i in 0..6) {
        if (i != 0) {
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
        val date = c[Calendar.DAY_OF_MONTH].toString()
        dates.add(date)
    }
    return dates
}

fun Long.convertToMillisTime(): String {
    System.currentTimeMillis()
    val millis = this % 1000
    val seconds = this / 1000
    return ((seconds.toInt() / 60).convertTwoDecimals() + ":"
            + (seconds.toInt() % 60).convertTwoDecimals()) + "." + (millis.toInt() / 10).convertTwoDecimals()
}

fun Int.convertTwoDecimals(): String {
    return if (this in 0..9) {
        "0$this"
    } else {
        this.toString() + ""
    }
}