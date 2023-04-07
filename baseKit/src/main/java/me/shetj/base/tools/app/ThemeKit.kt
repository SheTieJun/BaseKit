package me.shetj.base.tools.app

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.annotation.StyleRes
import me.shetj.base.ktx.logD

class ColorsActivityLifecycleCallbacks(var newStyle: Int) :
    ActivityLifecycleCallbacks {


    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        applyThemeOverlay(activity, newStyle)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}

fun applyThemeOverlay(context: Context, @StyleRes theme: Int) {
    context.theme.applyStyle(theme, true)
    if (context is Activity) {
        val windowDecorViewTheme = getWindowDecorViewTheme(context)
        windowDecorViewTheme?.applyStyle(theme, true)
    }
}

private fun getWindowDecorViewTheme(activity: Activity): Theme? {
    val window = activity.window
    if (window != null) {
        val decorView = window.peekDecorView()
        if (decorView != null) {
            val context = decorView.context
            if (context != null) {
                return context.theme
            }
        }
    }
    return null
}