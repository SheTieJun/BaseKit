package me.shetj.base.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Keep
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import me.shetj.base.ktx.grayThemChange
import me.shetj.base.ktx.logUILife
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.tools.app.LanguageKit
import me.shetj.base.tools.app.WindowKit.WindowSizeT

/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
open class AbBaseActivity : AppCompatActivity() {

    protected val TAG: String = this::class.java.simpleName

    protected val windowSizeStream: MutableLiveData<Pair<WindowSizeT, WindowSizeT>> =
        MutableLiveData<Pair<WindowSizeT, WindowSizeT>>()

    protected var enabledOnBack: Boolean = true
        set(value) {
            field = value
            onBackPressedCallback.isEnabled = value
        }

    protected val onBackPressedCallback = object : OnBackPressedCallback(enabledOnBack) {
        override fun handleOnBackPressed() {
            onBack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "$TAG : onCreate".logUILife()
        startAnimation()
        if (isEnableGrayTheme()) {
            GrayThemeLiveData.getInstance().observe(this, this::grayThemChange)
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        "$TAG : onMultiWindowModeChanged:$isInMultiWindowMode".logUILife()
    }

    /**
     * Is enable gray theme
     * 是否可以切换到灰色主题
     */
    open fun isEnableGrayTheme() = true

    open fun setTitle(title: String) {
        supportActionBar?.title = title
    }

    // 设置横竖屏
    open fun setOrientation(landscape: Boolean) {
        requestedOrientation = if (landscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // true - 界面加载成功的时候
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
        onBackPressedDispatcher.onBackPressed()
    }

    @MainThread
    open fun onBack() {
        enabledOnBack = false
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        LanguageKit.attachBaseContext(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LanguageKit.attachBaseContext(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        LanguageKit.attachBaseContext(this)
    }

    override fun onRestart() {
        "$TAG : onStart".logUILife()
        super.onRestart()
    }

    override fun onStart() {
        "$TAG : onStart".logUILife()
        super.onStart()
    }

    override fun onResume() {
        "$TAG : onResume".logUILife()
        super.onResume()
    }

    override fun onPause() {
        "$TAG : onPause".logUILife()
        super.onPause()
    }

    override fun onStop() {
        "$TAG : onStop".logUILife()
        super.onStop()
    }

    override fun onDestroy() {
        "$TAG : onDestroy".logUILife()
        super.onDestroy()
    }
}
