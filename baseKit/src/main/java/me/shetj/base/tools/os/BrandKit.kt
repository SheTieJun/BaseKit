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


package me.shetj.base.tools.os

import android.os.Build

/**
 * 判断设备
 */
object BrandKit {
    /**
     * 判断是否为小米设备
     */
    val isBrandXiaoMi: Boolean
        get() = ("xiaomi".equals(Build.BRAND, ignoreCase = true)
                || "xiaomi".equals(Build.MANUFACTURER, ignoreCase = true))

    /**
     * 判断是否为华为设备
     */
    val isBrandHuawei: Boolean
        get() = ("huawei".equals(Build.BRAND, ignoreCase = true)
                || "huawei".equals(Build.MANUFACTURER, ignoreCase = true))

    /**
     * 判断是否为魅族设备
     */
    val isBrandMeizu: Boolean
        get() = ("meizu".equals(Build.BRAND, ignoreCase = true)
                || "meizu".equals(Build.MANUFACTURER, ignoreCase = true)
                || "22c4185e".equals(Build.BRAND, ignoreCase = true))

    /**
     * 判断是否是oppo设备
     *
     * @return
     */
    val isBrandOppo: Boolean
        get() = ("oppo".equals(Build.BRAND, ignoreCase = true)
                || "oppo".equals(Build.MANUFACTURER, ignoreCase = true))

    /**
     * 判断是否是vivo设备
     *
     * @return
     */
    val isBrandVivo: Boolean
        get() = ("vivo".equals(Build.BRAND, ignoreCase = true)
                || "vivo".equals(Build.MANUFACTURER, ignoreCase = true))

}