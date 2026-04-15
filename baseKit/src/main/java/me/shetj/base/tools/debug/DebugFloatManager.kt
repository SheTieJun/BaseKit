package me.shetj.base.tools.debug

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import me.shetj.base.tools.app.AppUtils
import me.shetj.base.tools.app.Utils
import me.shetj.base.tools.file.SPUtils

/**
 * 调试悬浮窗管理器
 * 在 Debug 环境下显示当前版本号、Activity 界面名称以及自定义信息。
 * 采用在 DecorView 中注入的方式，避免了悬浮窗权限的申请。
 */
object DebugFloatManager : Application.ActivityLifecycleCallbacks {

    private const val KEY_IS_SHOW_FLOAT = "debug_is_show_float"

    private var isInit = false
    
    var isShow: Boolean
        get() = SPUtils.get(Utils.app, KEY_IS_SHOW_FLOAT, false) as Boolean
        set(value) {
            SPUtils.put(Utils.app, KEY_IS_SHOW_FLOAT, value)
            if (value) {
                currentActivity?.let { attachToActivity(it) }
            } else {
                detachFromActivity()
            }
        }

    private val version by lazy { AppUtils.appVersionName ?: "Unknown" }
    private var currentActivityName = ""
    private var customInfo = ""

    // 记录悬浮窗最后的位置
    private var lastTranslationX = 0f
    private var lastTranslationY = 100f // 默认稍微靠下

    private var currentFloatView: DebugFloatView? = null
    private var currentActivity: Activity? = null

    fun init(application: Application) {
        if (isInit) return
        isInit = true
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 切换显示状态
     */
    fun toggle() {
        isShow = !isShow
    }

    /**
     * 设置自定义信息
     */
    fun setCustomInfo(info: String) {
        customInfo = info
        updateViewInfo()
    }

    private fun updateViewInfo() {
        currentFloatView?.updateInfo(version, currentActivityName, customInfo)
    }

    private fun attachToActivity(activity: Activity) {
        if (!isShow) return
        val decorView = activity.window.decorView as? ViewGroup ?: return
        
        // 避免重复添加
        if (currentFloatView?.parent == decorView) {
            updateViewInfo()
            return
        }

        // 如果存在于别的 Activity，先移除
        (currentFloatView?.parent as? ViewGroup)?.removeView(currentFloatView)

        if (currentFloatView == null) {
            currentFloatView = DebugFloatView(activity).apply {
                translationX = lastTranslationX
                translationY = lastTranslationY
                onDragListener = { x, y ->
                    lastTranslationX = x
                    lastTranslationY = y
                }
            }
        }

        // 重新设置 translation 以恢复位置
        currentFloatView?.translationX = lastTranslationX
        currentFloatView?.translationY = lastTranslationY

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.START or android.view.Gravity.TOP
        }
        decorView.addView(currentFloatView, params)
        updateViewInfo()
    }

    private fun detachFromActivity() {
        (currentFloatView?.parent as? ViewGroup)?.removeView(currentFloatView)
        currentFloatView = null
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
        currentActivityName = activity.javaClass.simpleName
        if (isShow) {
            attachToActivity(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
        detachFromActivity()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
