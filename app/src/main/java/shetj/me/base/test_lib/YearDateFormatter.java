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

package shetj.me.base.test_lib;

import com.github.gzuliyujiang.wheelpicker.contract.DateFormatter;

/**
 * 年日期格式化程序
 *
 * @author shetj
 * @date 2022/09/01
 */
class YearDateFormatter implements DateFormatter {

    /**
     * 格式一年
     *
     * @param year 一年
     * @return {@link String}
     */
    @Override
    public String formatYear(int year) {
        return "" + year % 100+"";
    }

    /**
     * 格式月
     *
     * @param month 月
     * @return {@link String}
     */
    @Override
    public String formatMonth(int month) {
        return month < 10 ? "0" + month : "" + month;
    }

    /**
     * 格式一天
     *
     * @param day 一天
     * @return {@link String}
     */
    @Override
    public String formatDay(int day) {
        return day < 10 ? "0" + day : "" + day;
    }

}