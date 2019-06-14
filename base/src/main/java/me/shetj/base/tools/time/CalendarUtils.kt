package me.shetj.base.tools.time

import androidx.annotation.Keep

import java.util.Calendar

@Keep
object CalendarUtils {
    /**
     * 返回当前月份的天数
     * @param month 月
     * @param year 年
     * @return
     */
    fun getDaysInMonth(month: Int, year: Int): Int {
        var month = month
        month -= 1
        return when (month) {
            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
            Calendar.FEBRUARY -> if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
            else -> throw IllegalArgumentException("Invalid Month")
        }
    }
}