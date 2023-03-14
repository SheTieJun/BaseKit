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
        get() = (
            "xiaomi".equals(Build.BRAND, ignoreCase = true) ||
                "xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
            )

    /**
     * 判断是否为华为设备
     */
    val isBrandHuawei: Boolean
        get() = (
            "huawei".equals(Build.BRAND, ignoreCase = true) ||
                "huawei".equals(Build.MANUFACTURER, ignoreCase = true)
            )

    /**
     * 判断是否为魅族设备
     */
    val isBrandMeizu: Boolean
        get() = (
            "meizu".equals(Build.BRAND, ignoreCase = true) ||
                "meizu".equals(Build.MANUFACTURER, ignoreCase = true) ||
                "22c4185e".equals(Build.BRAND, ignoreCase = true)
            )

    /**
     * 判断是否是oppo设备
     *
     * @return
     */
    val isBrandOppo: Boolean
        get() = (
            "oppo".equals(Build.BRAND, ignoreCase = true) ||
                "oppo".equals(Build.MANUFACTURER, ignoreCase = true)
            )

    /**
     * 判断是否是vivo设备
     *
     * @return
     */
    val isBrandVivo: Boolean
        get() = (
            "vivo".equals(Build.BRAND, ignoreCase = true) ||
                "vivo".equals(Build.MANUFACTURER, ignoreCase = true)
            )
}
