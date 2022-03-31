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
package me.shetj.base.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import me.shetj.base.ktx.hasPermission

/**
 * fragment基类
 * @author shetj
 * 不需要懒加载，viewPager2 执行顺序就是
 * 开始必须可见才会初始化:[onCreateView]-> [Lifecycle.Event.ON_CREATE] -> [onViewCreated]-> [Lifecycle.Event.ON_START] -> [Lifecycle.Event.ON_RESUME]
 *
 * 结束前现会:[Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_STOP] -> [Lifecycle.Event.ON_DESTROY]
 *
 * 不可见:   [Lifecycle.Event.ON_RESUME]  ->[Lifecycle.Event.ON_PAUSE]
 *
 * 可见: [Lifecycle.Event.ON_PAUSE] -> [Lifecycle.Event.ON_RESUME]
 */
@Keep
abstract class AbBaseFragment : Fragment(), LifecycleEventObserver {

    //region registerForActivityResult :1.RequestMultiplePermissions ,2.StartActivityForResult
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            isPermissionGranted(it)
        }

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onStartActivityForResult(it)
    }

    /**
     * 请求权限
     */
    fun requestPermission(permission: String): Boolean {
        val isGranted =
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            permissionLauncher.launch(arrayOf(permission))
        }
        return isGranted
    }

    /**
     * 请求权限
     */
    fun requestPermissions(permissions: Array<String>): Boolean {
        val isGranted = requireActivity().hasPermission(*permissions, isRequest = false)
        if (!isGranted) {
            permissionLauncher.launch(permissions)
        }
        return isGranted
    }

    /**
     * 新的方式startActivityForResult
     */
    fun startActivityForResult(intent: Intent) {
        activityLauncher.launch(intent)
    }

    /**
     * all has permission : permissions.filter { !it.value }.isEmpty()
     */
    open fun isPermissionGranted(permissions: Map<String, Boolean>) {
    }

    /**
     * handle activityResult
     */
    open fun onStartActivityForResult(activityResult: ActivityResult?) {
    }

    //endregion

    protected var enabledOnBack: Boolean = false
        set(value) {
            field = value
            onBackPressedCallback.isEnabled = value
        }

    // 拦截activity的onBack，默认不拦截
    protected val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            onBack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
        if (savedInstanceState != null) {
            val flag = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN)
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            if (flag) {
                transaction.show(this)
            } else {
                transaction.hide(this)
            }
            transaction.commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(this)
    }

    open fun onBack() {}

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackPressedCallback.isEnabled = enabledOnBack
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        when (event) {
            ON_START -> viewBindData()
            ON_RESUME -> onVisible()
            ON_PAUSE -> onInvisible()
            ON_CREATE -> initEventAndData()
            else -> {}
        }
    }

    /**
     * 初始化数据和界面绑定
     */
    open fun viewBindData() {
    }

    /**
     * On visible.
     */
    open fun onVisible() {
    }

    /**
     * On invisible.
     */
    open fun onInvisible() {
    }

    /**
     * Init event and data.
     */
    protected fun initEventAndData() {
    }

    companion object {
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }
}
