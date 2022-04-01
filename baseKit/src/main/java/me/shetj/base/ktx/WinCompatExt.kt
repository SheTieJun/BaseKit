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

package me.shetj.base.ktx

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


val FragmentActivity.windowInsetsCompat: WindowInsetsCompat?
    get() = ViewCompat.getRootWindowInsets(findViewById<FrameLayout>(android.R.id.content))


val Fragment.windowInsetsCompat: WindowInsetsCompat?
    get() = kotlin.runCatching { ViewCompat.getRootWindowInsets(requireActivity().findViewById<FrameLayout>(android.R.id.content)) }
        .getOrNull()


/**
 * 是否具有 hasNavigationBars()
 */
fun FragmentActivity.hasNavigationBars(): Boolean {
    val windowInsetsCompat = windowInsetsCompat ?: return false
    return windowInsetsCompat.isVisible(Type.navigationBars())
            && windowInsetsCompat.getInsets(Type.navigationBars()).bottom > 0
}

/**
 * 获取NavigationBarsHeight
 */
fun FragmentActivity.getNavigationBarsHeight(): Int {
    val windowInsetsCompat = windowInsetsCompat ?: return 0
    return windowInsetsCompat.getInsets(Type.navigationBars()).bottom
}

/**
 * 获取StatusBarsHeight
 */
fun FragmentActivity.getStatusBarsHeight(): Int {
    val windowInsetsCompat = windowInsetsCompat ?: return 0
    return windowInsetsCompat.getInsets(Type.statusBars()).top
}

/**
 * 隐藏导航栏
 */
fun FragmentActivity.hideNavigationBars() {
    ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))?.let { controller ->
        controller.hide(Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * 全屏
 * {@see [theme:EdgeStyle]} 填充刘海屏幕
 */
fun Activity.hideSystemUI() {
    //decorFitsSystemWindows 表示是否沉浸，false 表示沉浸，true表示不沉浸
    WindowCompat.setDecorFitsSystemWindows(window, false)
    ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))?.let { controller ->
        controller.hide(Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * 展示系统UI:状态栏和导航栏
 */
fun Activity.showSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
        ?.show(Type.systemBars())
}

/**
 * 不沉侵，只修改状态栏的颜色
 */
fun Activity.setAppearance(
    isBlack: Boolean,
    @ColorInt color: Int = Color.TRANSPARENT
) {
    updateSystemUIColor(color)
    ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))?.let { controller ->
        controller.isAppearanceLightStatusBars = isBlack
        controller.isAppearanceLightNavigationBars = isBlack
    }
}

/**
 * 修改状态栏、底部的导航栏的颜色
 */
fun Activity.updateSystemUIColor(@ColorInt color: Int = Color.TRANSPARENT) {
    window.statusBarColor = color
    window.navigationBarColor = color
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.navigationBarDividerColor = color
    }
}

