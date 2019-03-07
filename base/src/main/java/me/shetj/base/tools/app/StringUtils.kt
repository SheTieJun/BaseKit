package me.shetj.base.tools.app

import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 *
 * <b>@packageName：</b> me.shetj.base.tools.app<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2019/3/7 0007<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */
@Suppress("DEPRECATION")
fun String.fromHtml(source: String): Spanned {
    val text: Spanned
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        text = Html.fromHtml(source, 0x00000000, null, null)
    } else {
        text = Html.fromHtml(source)
    }
    return text
}