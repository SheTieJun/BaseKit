/*
 * MIT License
 *
 * Copyright (c) 2021 SheTieJun
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

package shetj.me.base.test_lib

import android.app.Activity
import android.graphics.Color
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.impl.SimpleDateFormatter
import com.github.gzuliyujiang.wheelpicker.widget.DateWheelLayout
import me.shetj.base.ktx.dp2px


fun Activity.onYearMonthDay() {
    val picker = DatePicker(this)
    //picker.setBodyWidth(240);
    val wheelLayout: DateWheelLayout = picker.getWheelLayout()
    wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY)
    wheelLayout.setDateLabel("年", "月", "日");
    wheelLayout.setDateFormatter(SimpleDateFormatter())
    //wheelLayout.setRange(DateEntity.target(2021, 1, 1), DateEntity.target(2050, 12, 31), DateEntity.today());
    wheelLayout.setRange(DateEntity.today(), DateEntity.monthOnFuture(3))
    //注：建议通过`setStyle`定制样式设置文字加大，若通过`setSelectedTextSize`设置，该解决方案会导致选择器展示时跳动一下
    //wheelLayout.setSelectedTextSize(16 * getResources().getDisplayMetrics().scaledDensity);
    //wheelLayout.getYearLabelView().setTextColor(0xFF999999);
    //wheelLayout.getMonthLabelView().setTextColor(0xFF999999);
    picker.setOnDatePickedListener { year, month, day ->


    }
    picker.getWheelLayout().setResetWhenLinkage(false)
    picker.show()
}

fun DateWheelLayout.defSet() {
    val mainColor = Color.parseColor("#7E5ED5")
    setDateMode(DateMode.YEAR_MONTH_DAY)
    dayLabelView.setTextColor(mainColor)
    monthLabelView.setTextColor(mainColor)
    yearLabelView.setTextColor(mainColor)
    setDateFormatter(YearDateFormatter())
    setRange(DateEntity.target(2020, 1, 1), DateEntity.target(2050, 12, 31), DateEntity.today());
    setTextColor(mainColor)
    setSelectedTextColor(mainColor)
    setSelectedTextSize(20f.dp2px.toFloat())
}