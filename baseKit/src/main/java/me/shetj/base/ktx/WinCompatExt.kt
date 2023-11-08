@file:JvmName("WinCompat")

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

val Activity.windowInsets: WindowInsetsCompat?
    get() = ViewCompat.getRootWindowInsets(findViewById(android.R.id.content))

val Activity.windowInsetsController: WindowInsetsControllerCompat
    get() = WindowCompat.getInsetsController(window, findViewById(android.R.id.content))

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
 * Immerse 沉浸。设置沉浸的方式
 *
 * @param type Type.systemBars(),Type.statusBars(),Type.navigationBars()
 * @param statusIsBlack 专栏文字 true 黑色,false 白色
 * @param navigationIsBlack 导航栏按钮 true 黑色,false 白色
 * @param color
 */
@JvmOverloads
fun Activity.immerse(
    @Type.InsetsType type: Int = Type.systemBars(),
    statusIsBlack: Boolean = true,
    navigationIsBlack: Boolean = true,
    @ColorInt color: Int = Color.TRANSPARENT
) {
    when (type) {
        Type.systemBars() -> {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            updateSystemUIColor(color)
            windowInsetsController.let { controller ->
                controller.isAppearanceLightStatusBars = statusIsBlack
                controller.isAppearanceLightNavigationBars = navigationIsBlack
            }
            findViewById<FrameLayout>(android.R.id.content).apply {
                setPadding(0, 0, 0, 0)
            }
        }
        Type.statusBars() -> {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = color
            windowInsetsController.isAppearanceLightStatusBars = statusIsBlack
            findViewById<FrameLayout>(android.R.id.content).apply {
                post {
                    setPadding(0, 0, 0, getNavigationBarsHeight())
                }
            }
        }
        Type.navigationBars() -> {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.navigationBarColor = color
            windowInsetsController.isAppearanceLightNavigationBars = statusIsBlack
            findViewById<FrameLayout>(android.R.id.content).apply {
                post {
                    setPadding(0, getStatusBarsHeight(), 0, 0)
                }
            }
        }
        else -> {
            // no work
        }
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
 * 不沉侵，只修改状态栏的颜色,导航栏不修改
 */
fun Activity.setAppearance(
    isBlack: Boolean,
    @ColorInt color: Int = Color.TRANSPARENT
) {
    window.statusBarColor = color
    windowInsetsController.isAppearanceLightStatusBars = isBlack
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
