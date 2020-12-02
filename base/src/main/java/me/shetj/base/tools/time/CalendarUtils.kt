package me.shetj.base.tools.time

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.provider.CalendarContract.Reminders
import androidx.annotation.Keep
import java.util.*


/**
 *    <uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.WRITE_CALENDAR" />
 */
@Keep
object CalendarUtils {
    /**
     * 返回当前月份的天数
     * @param month 月
     * @param year 年
     * @return
     */
    fun getDaysInMonth(month: Int, year: Int): Int {
        var month1 = month - 1
        month1 -= 1
        return when (month1) {
            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
            Calendar.FEBRUARY -> if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
            else -> throw IllegalArgumentException("Invalid Month")
        }
    }

    /**
     * 组装日历事件
     *
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param eventTitle    事件标题
     * @param eventDes      事件描述
     * @param eventLocation 事件地点
     * @param event         组装的事件
     */
    fun setupEvent(startTime: Long, endTime: Long, eventTitle: String, eventDes: String,
                   eventLocation: String, event: ContentValues) {
        // 事件开始时间
        event.put(CalendarContract.Events.DTSTART, startTime)
        // 事件结束时间
        event.put(CalendarContract.Events.DTEND, endTime)
        // 事件标题
        event.put(CalendarContract.Events.TITLE, eventTitle)
        // 事件描述(对应手机系统日历备注栏)
        event.put(CalendarContract.Events.DESCRIPTION, eventDes)
        // 事件地点
        event.put(CalendarContract.Events.EVENT_LOCATION, eventLocation)
        // 事件时区
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        // 定义事件的显示，默认即可
        event.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT)
        // 事件的状态
        event.put(CalendarContract.Events.STATUS, 0)
        // 设置事件提醒警报可用
        event.put(CalendarContract.Events.HAS_ALARM, 1)
        // 设置事件忙
        event.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        // 设置事件重复规则
        // event.put(CalendarContract.Events.RRULE, );
    }


    fun addEvent(context: Context,eventID:Int,action: ContentValues.() ->Unit){
        val cr: ContentResolver = context.contentResolver
        val values = ContentValues()
        values.put(Reminders.EVENT_ID, eventID)
        action.invoke(values)
        val uri = cr.insert(Reminders.CONTENT_URI, values)
    }
}