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

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import me.shetj.base.R
import me.shetj.base.ktx.array
import me.shetj.base.ktx.hasPermission
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class AbBaseActivity : AppCompatActivity(), LifecycleEventObserver {

    //region registerForActivityResult 权限判断
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            isPermissionGranted(it)
        }

    /**
     * 请求权限
     */
    fun requestPermission(permission: String): Boolean {
        val isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            permissionLauncher.launch(arrayOf(permission))
        }
        return isGranted
    }

    /**
     * 请求权限
     */
    fun requestPermissions(permissions: Array<String>): Boolean {
        val isGranted = hasPermission(*permissions, isRequest = false)
        if (!isGranted) {
            permissionLauncher.launch(permissions)
        }
        return isGranted
    }

    /**
     * all has permission : permissions.filter { !it.value }.isEmpty()
     */
    open fun isPermissionGranted(permissions: MutableMap<String, Boolean>) {

    }
    //endregion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAnimation()
        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                onActivityCreate()
            }
            Lifecycle.Event.ON_DESTROY -> {
                onActivityDestroy()
            }
        }
    }

    open fun onActivityCreate() {
        if (useEventBus()) {
            // 注册到事件主线
            EventBus.getDefault().register(this)
        }
        findViewById<View>(R.id.toolbar_back)?.setOnClickListener { back() }
        initView()
        initData()
    }

    open fun onActivityDestroy() {
        if (useEventBus()) {
            // 如果要使用eventbus请将此方法返回true
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * 让[EventBus] 默认主线程处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onEvent(message: Message) {
    }

    open fun setTitle(title: String) {
        findViewById<TextView>(R.id.toolbar_title)?.apply {
            text = title
        }
    }

    // 设置横竖屏
    open fun setOrientation(landscape: Boolean) {
        requestedOrientation = if (landscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * 连接view
     */
    protected abstract fun initView()

    /**
     * 连接数据
     */
    protected abstract fun initData()

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // true - 界面加载成功的时候
    }

    /**
     * 是否使用eventBus,默认为使用(false)，
     *
     * @return useEventBus
     */
    open fun useEventBus(): Boolean {
        return false
    }

    /**
     * 界面开始动画 (此处输入方法执行任务.)
     */
    open fun startAnimation() {}

    /**
     * 界面回退动画 (此处输入方法执行任务.)
     */
    open fun endAnimation() {}

    /**
     * 用来替换 [finish] 返回
     */
    open fun back() {
        finishAfterTransition()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
    }
}
