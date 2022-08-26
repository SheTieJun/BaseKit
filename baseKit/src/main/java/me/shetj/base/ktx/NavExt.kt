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
