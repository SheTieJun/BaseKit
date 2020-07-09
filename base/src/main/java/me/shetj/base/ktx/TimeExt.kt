package me.shetj.base.ktx

import java.util.*


fun Date?.getWeekOfDate(aLocale:Locale  =Locale.CHINA ): String? {
    this?.apply {
        val weekDaysName = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        // String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        val calendar = Calendar.getInstance(aLocale)
        calendar.time = this
        val intWeek = calendar[Calendar.DAY_OF_WEEK] - 1
        return weekDaysName[intWeek]
    }
    return null
}
