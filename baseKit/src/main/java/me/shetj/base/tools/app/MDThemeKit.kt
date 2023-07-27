package me.shetj.base.tools.app

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.res.Resources.Theme
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import androidx.annotation.Keep
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.atomic.AtomicBoolean
import me.shetj.base.R
import me.shetj.base.R.style
import me.shetj.base.ktx.logD
import me.shetj.base.ktx.toJson
import me.shetj.base.model.SingleLiveEvent
import me.shetj.base.tools.file.SPUtils


/**
 * MD theme kit
 * MD 主题切换
 *
 * @constructor Create empty M d3theme kit
 */
object MDThemeKit {

    private val themeLiveData = SingleLiveEvent<ThemeBean>()

    val isDark: Boolean
        get() {
            return themeLiveData.value?.nightMode !=  AppCompatDelegate.MODE_NIGHT_YES
        }

    private const val SAVE_KEY = "AppCompatMD3Theme" //SP保持的key

    @Keep
    data class ThemeBean(
        @StyleRes val style: Int,
        val name: String,
        @NightMode val nightMode: Int = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
    )

    private val styleList = ArrayList<ThemeBean>()
    private val isInit = AtomicBoolean(false)
    private var mCurrentWhich = -1
    private val colorCallBacks = ColorsActivityLifecycleCallbacks(R.style.BaseTheme_MD3)
    private val defThemeBean = listOf(
        ThemeBean(style.BaseTheme_MD3, "默认主题-跟随系统"),
        ThemeBean(style.BaseTheme_MD3, "默认主题-黑夜", AppCompatDelegate.MODE_NIGHT_YES),
        ThemeBean(style.BaseTheme_MD3, "默认主题-日间", AppCompatDelegate.MODE_NIGHT_NO)
    )

    //region 以下是初始化方法
    /**
     * Start init
     * 开始初始化使用功能，必要时，请先调用[addTheme]
     * @param styleList 主题style的资源id
     * @param context
     */
    fun startInit(context: Context,styleList: List<ThemeBean> = defThemeBean) {
        if (isInit.compareAndSet(false, true)) {
            styleList.forEach {
                addTheme(it)
            }
            val which = SPUtils.get(context, SAVE_KEY, -1) as Int
            val theme = getThemeByWhich(which)
            colorCallBacks.rStyle = theme.style
            (context.applicationContext as Application).registerActivityLifecycleCallbacks(colorCallBacks)
            themeLiveData.observeForever {
                updateThemeWithTheme(it)
            }
            themeLiveData.value = (theme)
        }
    }
    //endregion

    //region 以下是切换主题的方法
    /**
     * Show change theme dialog
     * 展示切换主题的弹窗
     * @param context
     */
    fun showChangeThemeDialog(context: FragmentActivity) {
        if (styleList.isEmpty()) {
            return
        }
        val selectPosition = SPUtils.get(context, SAVE_KEY, -1) as Int
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.Themes))
            .setSingleChoiceItems(
                getThemeTitleList().toTypedArray(),
                selectPosition
            ) { dialog, which ->
                SPUtils.put(context, SAVE_KEY, which)
                themeLiveData.postValue( getThemeByWhich(which))
                dialog.dismiss()
            }
            .show()
    }
    //endregion

    //region 其他公开方法

    /**
     * Get current theme
     * 当前主题
     * @return
     */
    fun getCurrentTheme(): SingleLiveEvent<ThemeBean> {
        return themeLiveData
    }

    fun clearTheme() {
        styleList.clear()
    }

    fun getThemeList(): ArrayList<ThemeBean> {
        return styleList
    }

    fun getThemeTitleList(): List<String> {
        return styleList.map { it.name }
    }

    fun getThemeByWhich(which: Int): ThemeBean {
        return kotlin.runCatching {
            styleList[which]
        }.getOrDefault(ThemeBean(style.BaseTheme_MD3, "默认主题-跟随系统"))
    }

    fun getThemeNameByWhich(which: Int): String {
        return kotlin.runCatching {
            styleList[which].name
        }.getOrDefault("System")
    }

    fun getNightModeByWhich(which: Int): Int {
        return kotlin.runCatching {
            styleList[which].nightMode
        }.getOrDefault(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
    }

    fun getIndexByTheme(theme: ThemeBean): Int {
        return styleList.indexOfFirst { it.style == theme.style && it.name == theme.name && it.nightMode == theme.nightMode }
    }

    fun updateTheme(theme: ThemeBean) {
        updateTheme(theme.style,theme.name,theme.nightMode)
    }

    fun updateTheme(@StyleRes style: Int, name: String,@NightMode nightMode: Int = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY){
        styleList.indexOfFirst { it.style == style && it.name == name && it.nightMode == nightMode }.let {
            if (it == -1){
                val themeBean = ThemeBean(style, name, nightMode)
                addTheme(themeBean)
                updateThemeWithTheme(themeBean)
                return
            }
            updateThemeWithTheme(getThemeByWhich(it))
        }
    }

    //endregion

    /**
     * Clear theme
     */
    private fun updateThemeWithTheme(theme: ThemeBean) {
        if (mCurrentWhich == getIndexByTheme(theme)) {
            return
        }
        mCurrentWhich = getIndexByTheme(theme)
        colorCallBacks.rStyle = theme.style
        updateTheme(theme.nightMode)
    }

    private fun addTheme(theme: ThemeBean) {
        styleList.indexOfFirst { it.style == theme.style && it.name == theme.name && it.nightMode == theme.nightMode }.let {
            if (it == -1){
                styleList.add(theme)
            }
        }
    }

    private fun updateTheme(modeNightAutoBattery: Int) {
        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()
        if (defaultNightMode != AppCompatDelegate.MODE_NIGHT_YES) {
            //ActivityCompat recreate
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AppCompatDelegate.setDefaultNightMode(modeNightAutoBattery)
            return
        }
        if (defaultNightMode != AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            AppCompatDelegate.setDefaultNightMode(modeNightAutoBattery)
            return
        }
    }

    class ColorsActivityLifecycleCallbacks(var rStyle: Int) :
        ActivityLifecycleCallbacks {


        override fun onActivityPreCreated(
            activity: Activity, savedInstanceState: Bundle?
        ) {
            applyThemeOverlay(activity, rStyle)

        }

        override fun onActivityCreated(
            activity: Activity, savedInstanceState: Bundle?
        ) {
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    fun applyThemeOverlay(context: Context, @StyleRes theme: Int) {
        // Use applyStyle() instead of setTheme() due to Force Dark issue.
        context.theme.applyStyle(theme,  /* force= */true)

        // Make sure the theme overlay is applied to the Window decorView similar to Activity#setTheme,
        // to ensure that it will be applied to things like ContextMenu using the DecorContext.
        if (context is Activity) {
            val windowDecorViewTheme = getWindowDecorViewTheme(
                context
            )
            windowDecorViewTheme?.applyStyle(theme,  /* force= */true)
        }
    }

    private fun getWindowDecorViewTheme(activity: Activity): Theme? {
        val window = activity.window
        if (window != null) {
            // Use peekDecorView() instead of getDecorView() to avoid locking the Window.
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
}






