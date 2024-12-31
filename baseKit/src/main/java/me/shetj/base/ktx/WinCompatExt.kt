@file:JvmName("WinCompat")

package me.shetj.base.ktx

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
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
 * Immerse 沉浸。设置沉浸的方式 不可以和setAppearance 一起使用
 *
 * @param type Type.systemBars(),Type.statusBars(),Type.navigationBars()
 * @param statusIsBlackText 专栏文字 true 黑色,false 白色
 * @param navigationIsBlackLine 导航栏按钮 true 黑色,false 白色
 * @param color
 */
@JvmOverloads
fun ComponentActivity.immerse(
    @Type.InsetsType type: Int = Type.statusBars(),
    statusIsBlackText: Boolean = isNeedBlackText,
    navigationIsBlackLine: Boolean = true,
    @ColorInt color: Int = Color.TRANSPARENT
) {

    if (color != Color.TRANSPARENT) {
        if (statusIsBlackText) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(color, color),
                navigationBarStyle = SystemBarStyle.light(color, color)
            )
        } else {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(color),
                navigationBarStyle = SystemBarStyle.dark(color)
            )
        }
    } else {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(color, color, detectDarkMode = { _ ->
                !statusIsBlackText
            }),
            navigationBarStyle = SystemBarStyle.auto(color, color, detectDarkMode = { _ ->
                !navigationIsBlackLine
            })
        )
    }
    when (type) {
        Type.systemBars() -> {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
                v.setPadding(0, 0, 0, 0)
                insets
            }
        }

        Type.statusBars() -> {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
                v.setPadding(0, 0, 0, insets.getInsets(Type.navigationBars()).bottom)
                insets
            }
        }

        Type.navigationBars() -> {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
                v.setPadding(0, insets.getInsets(Type.statusBars()).top, 0, 0)
                insets
            }
        }

        else -> {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
                val systemBars = insets.getInsets(Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
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


val ComponentActivity.isNeedBlackText: Boolean
    get() = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) !=
            Configuration.UI_MODE_NIGHT_YES

/**
 * 不沉侵，只修改状态栏的颜色,导航栏不修改
 */
fun ComponentActivity.setAppearance(
    isBlackText: Boolean = isNeedBlackText,
    @ColorInt color: Int = Color.TRANSPARENT
) {

    if (color != Color.TRANSPARENT) {
        if (isBlackText) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(color, color),
            )
        } else {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(color),
            )
        }
    } else {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(color, color, detectDarkMode = { _ ->
                !isBlackText
            })
        )
    }
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
        val systemBars = insets.getInsets(Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }
}