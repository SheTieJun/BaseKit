

package me.shetj.base.ktx

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

/**
 * ```
 * Kotlin：
 * - Fragment.findNavController()
 *- View.findNavController()
 *- Activity.findNavController(viewId: Int)
 *Java：
 *- NavHostFragment.findNavController(Fragment)
 *- Navigation.findNavController(Activity, @IdRes int viewId)
 *- Navigation.findNavController(View)
 * ```
 *
 * 使用 FragmentContainerView 创建 NavHostFragment，
 * 或通过 FragmentTransaction 手动将 NavHostFragment 添加到您的 Activity 时，
 * 尝试通过 Navigation.findNavController(Activity, @IdRes int) 检索 Activity 的 onCreate() 中的 NavController 将失败。
 * 所以需要以下的方法获取
 */
fun FragmentActivity.fixFindNavController(@IdRes idRes: Int): NavController {
    return kotlin.runCatching {
        findNavController(idRes)
    }.getOrElse {
        val navHostFragment =
            supportFragmentManager.findFragmentById(idRes) as NavHostFragment
        return navHostFragment.navController
    }
}

/**
 * 当前数据
 */
fun NavController.getCurSavedStateHandle(): SavedStateHandle? {
    return currentBackStackEntry?.savedStateHandle
}

/**
 * 上一个栈的数据
 */
fun NavController.getPreSavedStateHandle(): SavedStateHandle? {
    return previousBackStackEntry?.savedStateHandle
}
