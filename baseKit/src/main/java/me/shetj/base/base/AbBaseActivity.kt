package me.shetj.base.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.appbar.MaterialToolbar
import me.shetj.base.R
import me.shetj.base.ktx.grayThemChange
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.tools.app.LanguageKit

/**
 * 基础类  view 层
 * @author shetj
 */
@Keep
abstract class AbBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startAnimation()
        onActivityCreate()
        if (isEnableGrayTheme()){
            GrayThemeLiveData.getInstance().observe(this,this::grayThemChange)
        }
    }

    open fun onActivityCreate() {
        findViewById<MaterialToolbar>(R.id.toolbar)?.apply {
            setSupportActionBar(this)
            setNavigationOnClickListener {
                back()
            }
        }
        initView()
        initData()
    }

    /**
     * Is enable gray theme
     * 是否可以展示灰色主题
     */
    open fun isEnableGrayTheme() = false

    open fun onActivityDestroy() {
    }

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

    override fun onDestroy() {
        onActivityDestroy()
        super.onDestroy()
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
}
