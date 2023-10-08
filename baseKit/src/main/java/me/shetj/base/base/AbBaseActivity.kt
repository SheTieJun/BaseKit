package me.shetj.base.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Keep
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import me.shetj.base.ktx.getWindowContent
import me.shetj.base.ktx.grayThemChange
import me.shetj.base.ktx.logUILife
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.tools.app.LanguageKit
import me.shetj.base.tools.app.WindowKit
import me.shetj.base.tools.app.WindowKit.WindowSizeClass

/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class AbBaseActivity : AppCompatActivity() {

    protected val TAG: String = this::class.java.simpleName

    protected val windowSizeStream: MutableLiveData<Pair<WindowSizeClass, WindowSizeClass>> =
        MutableLiveData<Pair<WindowSizeClass, WindowSizeClass>>()

    protected var enabledOnBack: Boolean = false
        set(value) {
            field = value
            onBackPressedCallback.isEnabled = value
        }

    protected val onBackPressedCallback = object : OnBackPressedCallback(false) {
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
        configWindow()
    }


    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        "$TAG : onMultiWindowModeChanged:$isInMultiWindowMode".logUILife()
    }

    private fun configWindow() {
        getWindowContent()?.addView(object : View(this) {
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)
                computeWindowSizeClasses()
            }
        })
        computeWindowSizeClasses()
        windowSizeStream.observe(this) {
            "$TAG onWindowSizeChange : widthWindowSizeClass = ${it.first},heightWindowSizeClass = ${it.second}".logUILife()
            onWindowSizeChange(it)
        }
    }

    /**
     * On window size change
     * 当activity界面屏幕大小改变的时候
     * @param windowSizeWH
     */
    open fun onWindowSizeChange(windowSizeWH: Pair<WindowSizeClass, WindowSizeClass>) {
        onWindowSizeChangeWidth(windowSizeWH.first)
        onWindowSizeChangeHeight(windowSizeWH.second)
    }

    open fun onWindowSizeChangeHeight(windowSizeH: WindowSizeClass) {

    }

    open fun onWindowSizeChangeWidth(windowSizeW: WindowSizeClass) {

    }

    protected fun computeWindowSizeClasses() {
        windowSizeStream.postValue(WindowKit.windowSize(this@AbBaseActivity))
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
        computeWindowSizeClasses()
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
