package shetj.me.base.day

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.text.format.DateUtils
import androidx.annotation.IntDef
import java.util.*
import kotlin.annotation.AnnotationRetention.SOURCE
import me.shetj.base.ktx.logI

open class Month private constructor(rawCalendar: Calendar) : Comparable<Month> {
    /** The acceptable int values for month when using [Month.create]  */
    @Retention(SOURCE)
    @IntDef(
        value = [Calendar.JANUARY,
        Calendar.FEBRUARY,
        Calendar.MARCH,
        Calendar.APRIL, Calendar.MAY,
        Calendar.JUNE, Calendar.JULY,
        Calendar.AUGUST, Calendar.SEPTEMBER,
        Calendar.OCTOBER, Calendar.NOVEMBER,
        Calendar.DECEMBER])
    annotation class Months

    private val firstOfMonth: Calendar


    @Months
    val month: Int
    val year: Int
    val daysInWeek: Int
    val daysInMonth: Int
    val timeInMillis: Long


    var longName: String? = null
        /** Returns a localized String representation of the month name and year.  */
        get() {
            if (field == null) {
                field = getYearMonth(firstOfMonth.timeInMillis)
            }
            return field
        }
        private set

    /**
     * 返回月份中第一个位置的索引。 firstOfMonth是某个月的第一天，并且它的DAY_OF_WEEK值为3，那么表示该月的第一天是星期二。
     * @param firstDayOfWeek
     * @return
     */
    fun daysFromStartOfWeekToFirstOfMonth(firstDayOfWeek: Int): Int {
        var difference = (firstOfMonth[Calendar.DAY_OF_WEEK]
                - if (firstDayOfWeek > 0) firstDayOfWeek else firstOfMonth.firstDayOfWeek)
        if (difference < 0) {
            difference += daysInWeek
        }
        return difference
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is Month) {
            return false
        }
        val that = o
        return month == that.month && year == that.year
    }

    override fun hashCode(): Int {
        val hashedFields = arrayOf<Any>(month, year)
        return hashedFields.contentHashCode()
    }

    override fun compareTo(other: Month): Int {
        return firstOfMonth.compareTo(other.firstOfMonth)
    }

    val stableId: Long
        get() = firstOfMonth.timeInMillis

    /**
     * Gets a long for the specific day within the instance's month and year.
     * 获取实例月份和年份内特定日期的长整型值。
     *
     * This method only guarantees validity with respect to [Calendar.isLenient].
     *
     * @param day The desired day within this month and year;  day 本月和今年内所需的日期
     * @return A long representing a time in milliseconds for the given day within the specified month
     * and year 表示指定月份和年份内给定日期的时间（以毫秒为单位）
     */
    fun getDay(day: Int): Long {
        val dayCalendar = LocalDates.getDayCopy(firstOfMonth)
        dayCalendar[Calendar.DAY_OF_MONTH] = day
        return dayCalendar.timeInMillis
    }

    fun getDayOfMonth(date: Long): Int {
        val dayCalendar = LocalDates.getDayCopy(firstOfMonth)
        dayCalendar.timeInMillis = date
        return dayCalendar[Calendar.DAY_OF_MONTH]
    }

    /**
     * instance. 下一个月
     */
    fun monthsLater(months: Int): Month {
        val laterMonth = LocalDates.getDayCopy(firstOfMonth)
        laterMonth.add(Calendar.MONTH, months)
        return Month(laterMonth)
    }

    init {
        rawCalendar[Calendar.DAY_OF_MONTH] = 1
        firstOfMonth = LocalDates.getDayCopy(rawCalendar)
        firstOfMonth.firstDayOfWeek = 0 //设置成周一
        month = firstOfMonth[Calendar.MONTH]
        year = firstOfMonth[Calendar.YEAR]
        daysInWeek = firstOfMonth.getMaximum(Calendar.DAY_OF_WEEK)
        daysInMonth = firstOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        timeInMillis = firstOfMonth.timeInMillis
    }

    companion object {
        /**
         * Creates an instance of Month that contains the provided `timeInMillis` where `timeInMillis` is in milliseconds since 00:00:00 January 1, 1970, UTC.
         */
        fun create(timeInMillis: Long): Month {
            val calendar = LocalDates.getLocalCalendar()
            calendar.timeInMillis = timeInMillis
            return Month(calendar)
        }

        /**
         * Creates an instance of Month with the given parameters backed by a [Calendar].
         *
         * @param year The year
         * @param month The 0-index based month. Use [Calendar] constants (e.g., [     ][Calendar.JANUARY]
         * @return A Month object backed by a new [Calendar] instance
         */
        fun create(year: Int, @Months month: Int): Month {
            val calendar = LocalDates.getLocalCalendar()
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            return Month(calendar)
        }

        /**
         * Returns the [Month] that contains the first moment in current month in the default
         * timezone (as per [Calendar.getInstance].
         */
        fun current(): Month {
            return Month(LocalDates.getTodayCalendar())
        }

        fun getYearMonth(timeInMillis: Long): String {
            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                return LocalDates.getYearMonthFormat(Locale.getDefault()).format(Date(timeInMillis))
            }
            val flags = DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_NO_MONTH_DAY or DateUtils.FORMAT_UTC
            return DateUtils.formatDateTime(null, timeInMillis, flags)
        }
    }

}