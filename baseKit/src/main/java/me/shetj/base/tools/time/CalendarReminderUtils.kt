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
package me.shetj.base.tools.time

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.CalendarContract
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.TimeZone
import me.shetj.base.ktx.hasPermission

/**
 * AndroidManifest 中 对应activity加入
 *    <intent-filter>
<action android:name="android.provider.calendar.action.HANDLE_CUSTOM_EVENT" />

<category android:name="android.intent.category.DEFAULT" />

<data android:mimeType="vnd.android.cursor.item/event" />
</intent-filter>
 */
object CalendarReminderUtils {
    private const val CALENDER_URL = "content://com.android.calendar/calendars"
    private const val CALENDER_EVENT_URL = "content://com.android.calendar/events"
    private const val CALENDER_REMINDER_URL = "content://com.android.calendar/reminders"
    private const val CALENDARS_NAME = "JUN"
    private const val CALENDARS_ACCOUNT_NAME = "375105540@qq.com"
    private const val CALENDARS_ACCOUNT_TYPE = "com.android.shetj"
    private const val CALENDARS_DISPLAY_NAME = "shetj"

    fun checkPermission(context: AppCompatActivity): Boolean {
        return context.hasPermission(
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR,
            isRequest = true
        )
    }

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    private fun checkAndAddCalendarAccount(context: Context): Int {
        val oldId = checkCalendarAccount(context)
        return if (oldId >= 0) {
            oldId
        } else {
            val addId = addCalendarAccount(context)
            if (addId >= 0) {
                checkCalendarAccount(context)
            } else {
                -1
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    private fun checkCalendarAccount(context: Context): Int {
        val userCursor =
            context.contentResolver.query(Uri.parse(CALENDER_URL), null, null, null, null)
        return userCursor.use { cursor ->
            if (cursor == null) { // 查询返回空值
                return -1
            }
            val count = cursor.count
            if (count > 0) { // 存在现有账户，取第一个账户的id返回
                cursor.moveToFirst()
                cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars._ID))
            } else {
                -1
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private fun addCalendarAccount(context: Context): Long {
        val timeZone = TimeZone.getDefault()
        val value = ContentValues()
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME)
        value.put(CalendarContract.Calendars.VISIBLE, 1)
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE)
        value.put(
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
            CalendarContract.Calendars.CAL_ACCESS_OWNER
        )
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.id)
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0)
        var calendarUri = Uri.parse(CALENDER_URL)
        calendarUri = calendarUri.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
            .build()
        val result = context.contentResolver.insert(calendarUri, value)
        return if (result == null) -1 else ContentUris.parseId(result)
    }

    /**
     * 打开日历界面
     */
    fun Context.addCalendar(title: String?, des: String?, scheme: String?) {
        val startMillis: Long = System.currentTimeMillis() + 60 * 1000
        val endMillis: Long = System.currentTimeMillis() + 60 * 5
        val tz = TimeZone.getDefault()
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            .putExtra(CalendarContract.Events.TITLE, title)
            .putExtra(CalendarContract.Events.DESCRIPTION, des)
            .putExtra(CalendarContract.Events.EVENT_LOCATION, tz.displayName)
            .putExtra(CalendarContract.Events.CUSTOM_APP_PACKAGE, packageName)
            .putExtra(CalendarContract.Events.CUSTOM_APP_URI, scheme)
        startActivity(intent)
    }

    /**
     * @param remindTime 提醒的事件
     * @param previousTime 提前时间分钟开始提示
     * @param packageName 包名
     * @param scheme 部分手机支持跳转
     */
    fun addCalendarEvent(
        context: Context?,
        title: String?,
        des: String?,
        remindTime: Long,
        endTime: Long?,
        previousTime: Long,
        packageName: String? = null,
        scheme: String? = null
    ): Long {
        if (context == null) {
            return -1
        }
        val calId = checkAndAddCalendarAccount(context) // 获取日历账户的id
        if (calId < 0) { // 获取账户id失败直接返回，添加日历事件失败
            return -1
        }

        // 添加日历事件
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = remindTime // 设置开始时间
        val start = mCalendar.time.time
        mCalendar.timeInMillis = start + 10 * 60 * 1000 // 设置终止时间，开始时间加10分钟
        val end = endTime ?: mCalendar.time.time
        val event = ContentValues()
        event.put(CalendarContract.Events.TITLE, title)
        event.put(CalendarContract.Events.DESCRIPTION, des)
        event.put(CalendarContract.Events.CALENDAR_ID, calId) // 插入账户的id
        event.put(CalendarContract.Events.DTSTART, start)
        event.put(CalendarContract.Events.DTEND, end)
        event.put(CalendarContract.Events.HAS_ALARM, 1) // 设置有闹钟提醒
        event.put(
            CalendarContract.Events.EVENT_TIMEZONE,
            TimeZone.getDefault().displayName
        ) // 这个是时区，必须有
        event.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, packageName)
        event.put(CalendarContract.Events.CUSTOM_APP_URI, scheme)
        val newEvent = context.contentResolver.insert(Uri.parse(CALENDER_EVENT_URL), event)
            ?: // 添加日历事件失败直接返回
            return -1 // 添加事件

        // 事件提醒的设定
        val values = ContentValues()
        val eventID = ContentUris.parseId(newEvent)
        values.put(CalendarContract.Reminders.EVENT_ID, eventID)
        values.put(CalendarContract.Reminders.MINUTES, previousTime) // 提前previousDate分钟有提醒
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        context.contentResolver.insert(Uri.parse(CALENDER_REMINDER_URL), values)
            ?: // 添加事件提醒失败直接返回
            return -1
        return eventID
    }

    /**
     * 更新日历优化版
     */
    fun updateRemindEvent(
        context: Context,
        calId: Int,
        eventId: Long,
        title: String?,
        des: String?,
        remindTime: Long,
        endTime: Long?,
        previousTime: Long,
        packageName: String? = null,
        scheme: String? = null
    ): Boolean {

        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = remindTime // 设置开始时间
        val start = mCalendar.time.time
        mCalendar.timeInMillis = start + 10 * 60 * 1000 // 设置终止时间，开始时间加10分钟
        val end = endTime ?: mCalendar.time.time

        return try {
            val tz = TimeZone.getDefault() // 获取默认时区
            /* 更新日程 */
            val values = ContentValues()
            values.put(CalendarContract.Events.DTSTART, start)
            values.put(CalendarContract.Events.DTEND, end)
            values.put(CalendarContract.Events.TITLE, title)
            values.put(CalendarContract.Events.DESCRIPTION, des)
            values.put(CalendarContract.Events.CALENDAR_ID, calId)
            values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT)
            values.put(CalendarContract.Events.EVENT_LOCATION, tz.displayName)
            values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.id)
            val updateUri = ContentUris.withAppendedId(Uri.parse(CALENDER_REMINDER_URL), eventId)
            val rowNum = context.contentResolver.update(updateUri, values, null, null)
            if (rowNum <= 0) {
                /*更新event不成功，说明用户在日历中删除了提醒事件，重新添加*/
                if (addCalendarEvent(
                        context,
                        title,
                        des,
                        remindTime,
                        endTime,
                        previousTime,
                        packageName,
                        scheme
                    ) != -1L
                ) {
                    return true
                }
                return false
            } else {
                val reminderValues = ContentValues()
                reminderValues.put(CalendarContract.Reminders.MINUTES, previousTime) // 提前提醒
                val rUri = Uri.parse(CALENDER_REMINDER_URL)
                context.contentResolver.update(
                    rUri,
                    reminderValues,
                    CalendarContract.Reminders.EVENT_ID + "= ?",
                    arrayOf(eventId.toString())
                )
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 删除日历事件
     */
    fun deleteCalendarEvent(context: Context?, title: String) {
        if (context == null) {
            return
        }
        val eventCursor =
            context.contentResolver.query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null)
        eventCursor.use { cursor ->
            if (cursor == null) { // 查询返回空值
                return
            }
            if (cursor.count > 0) {
                // 遍历所有事件，找到title跟需要查询的title一样的项
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val eventTitle = cursor.getString(cursor.getColumnIndex("title"))
                    if (!TextUtils.isEmpty(title) && title == eventTitle) {
                        val id =
                            cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars._ID)) // 取得id
                        val deleteUri =
                            ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id.toLong())
                        val rows = context.contentResolver.delete(deleteUri, null, null)
                        if (rows == -1) { // 事件删除失败
                            return
                        }
                    }
                    cursor.moveToNext()
                }
            }
        }
    }

    /**
     * 小于0 修改失败
     */
    fun deleteCalendarEvent(context: Context, id: Long): Int {
        val deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id)
        return context.contentResolver.delete(deleteUri, null, null)
    }

    fun updateCalendarEvent(
        context: Context,
        id: Long,
        title: String?,
        des: String?,
        remindTime: Long,
        endTime: Long?,
        previousTime: Long
    ): Int {
        val values = ContentValues().apply {
            val calId = checkAndAddCalendarAccount(context)
            val timezone = TimeZone.getTimeZone("Asia/Shanghai")
            // 添加日历事件
            val mCalendar = Calendar.getInstance()
            mCalendar.timeInMillis = remindTime // 设置开始时间
            val start = mCalendar.time.time
            mCalendar.timeInMillis = start + 10 * 60 * 1000 // 设置终止时间，开始时间加10分钟
            val end = endTime ?: mCalendar.time.time
            title?.let {
                put(CalendarContract.Events.TITLE, title)
            }
            des?.let {
                put(CalendarContract.Events.DESCRIPTION, des)
            }
            put(CalendarContract.Events.CALENDAR_ID, calId) // 插入账户的id
            put(CalendarContract.Events.DTSTART, start)
            put(CalendarContract.Events.DTEND, end)
            put(CalendarContract.Events.EVENT_TIMEZONE, timezone.displayName) // 这个是时区，必须有
            put(CalendarContract.Events.CUSTOM_APP_PACKAGE, context.packageName)
            put(CalendarContract.Reminders.EVENT_ID, id)
            put(CalendarContract.Reminders.MINUTES, previousTime) // 提前previousDate天有提醒
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        val updateUri: Uri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, id)
        return context.contentResolver.update(updateUri, values, null, null)
    }
}
