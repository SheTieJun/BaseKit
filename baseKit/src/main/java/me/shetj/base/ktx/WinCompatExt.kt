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
package me.shetj.base.ktx

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.Window
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

val Activity.windowInsets: WindowInsetsCompat?
    get() = ViewCompat.getRootWindowInsets(findViewById(android.R.id.content))

val Activity.windowInsetsController: WindowInsetsControllerCompat
    get() = WindowCompat.getInsetsController(window,findViewById(android.R.id.content))

val Fragment.windowInsetsCompat: WindowInsetsCompat?
    get() = kotlin.runCatching { requireActivity().windowInsets }
        .getOrNull()

/**
 * 是否具有 hasNavigationBars()
 */
fun Activity.hasNavigationBars(): Boolean {
    val windowInsetsCompat = windowInsets ?: return false
    return windowInsetsCompat.isVisible(Type.navigationBars()) &&
        windowInsetsCompat.getInsets(Type.navigationBars()).bottom > 0
}

/**
 * 获取NavigationBarsHeight
 */
fun Activity.getNavigationBarsHeight(): Int {
    val windowInsetsCompat = windowInsets ?: return 0
    return windowInsetsCompat.getInsets(Type.navigationBars()).bottom
}

/**
 * 获取StatusBarsHeight
 */
fun Activity.getStatusBarsHeight(): Int {
    val windowInsetsCompat = windowInsets ?: return 0
    return windowInsetsCompat.getInsets(Type.statusBars()).top
}

/**
 * 隐藏导航栏
 */
fun Activity.hideNavigationBars() {
    windowInsetsController.let { controller ->
        controller.hide(Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}


/**
 * 展示导航栏
 */
fun Activity.showNavigationBars() {
    windowInsetsController.let { controller ->
        controller.show(Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * 全屏
 * {@see [theme:EdgeStyle]} 填充刘海屏幕
 */
fun Activity.hideSystemUI() {
    // decorFitsSystemWindows 表示是否沉浸，false 表示沉浸，true表示不沉浸
    WindowCompat.setDecorFitsSystemWindows(window, false)
    windowInsetsController.let { controller ->
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
    windowInsetsController.show(Type.systemBars())
}

/**
 * 不沉侵，只修改状态栏的颜色
 */
fun Activity.setAppearance(
    isBlack: Boolean,
    @ColorInt color: Int = Color.TRANSPARENT
) {
    updateSystemUIColor(color)
    windowInsetsController.let { controller ->
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