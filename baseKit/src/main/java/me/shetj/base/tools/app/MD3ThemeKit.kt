package me.shetj.base.tools.app

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.atomic.AtomicBoolean
import me.shetj.base.R
import me.shetj.base.R.style
import me.shetj.base.model.SingleLiveEvent
import me.shetj.base.tools.file.SPUtils


/**
 * MD3 theme kit
 * MD3 主题切换
 *
 * @constructor Create empty M d3theme kit
 */
object MD3ThemeKit {

    private val styleList = ArrayList<Int>()
    private val styleTitleList = ArrayList<String>()
    private val nightModeList = ArrayList<Int>()
    private val isInit = AtomicBoolean(false)
    private var mCurrentWhich = 3
    private val colorCallBacks = ColorsActivityLifecycleCallbacks(R.style.BaseTheme_MD3)

    init {
        initDef()
    }

    private fun initDef() {
        addTheme(style.BaseTheme_MD3, "默认主题-跟随系统")
        addTheme(style.BaseTheme_MD3, "默认主题-黑夜", AppCompatDelegate.MODE_NIGHT_YES)
        addTheme(style.BaseTheme_MD3, "默认主题-日间", AppCompatDelegate.MODE_NIGHT_NO)
    }


    /**
     * Start init
     * 开始初始化使用功能，必要时，请先调用[addTheme]
     * @param context
     */
    fun startInit(context: Context) {
        if (isInit.compareAndSet(false, true)) {
            val which = SPUtils.get(context, "AppCompatTheme", -1) as Int
            val theme = getThemeByWhich(which)

            colorCallBacks.rStyle = theme
            (context.applicationContext as Application).registerActivityLifecycleCallbacks(colorCallBacks)
            ThemeLiveData.observeForever {
                updateThemeByType(it)
            }
            ThemeLiveData.value = (which)
        }
    }


    /**
     * Add theme
     *
     * @param style 主题style的资源id
     * @param name 主题名称
     */
    fun addTheme(@StyleRes style: Int, name: String,@NightMode nightMode: Int = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY) {

        if (styleList.contains(style) && styleTitleList.contains(name) && nightModeList.contains(nightMode)) {
            return
        }
        styleList.add(style)
        styleTitleList.add(name)
        nightModeList.add(nightMode)
    }

    /**
     * Clear theme
     *
     */
    fun clearTheme() {
        styleList.clear()
        styleTitleList.clear()
    }

    fun getThemeList(): ArrayList<Int> {
        return styleList
    }

    fun getThemeTitleList(): ArrayList<String> {
        return styleTitleList
    }

    /**
     * Show change theme dialog
     * 展示切换主题的弹窗
     * @param context
     */
    fun showChangeThemeDialog(context: FragmentActivity) {
        if (styleList.isEmpty()) {
            return
        }
        val selectPosition = SPUtils.get(context, "AppCompatTheme", -1) as Int
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.Themes))
            .setSingleChoiceItems(
                styleTitleList.toTypedArray(),
                selectPosition
            ) { dialog, which ->
                SPUtils.put(context, "AppCompatTheme", which)
                ThemeLiveData.postValue(which)
                dialog.dismiss()
            }
            .show()
    }

    fun getThemeByWhich(which: Int): Int {
        return kotlin.runCatching {
            styleList[which]
        }.getOrDefault(R.style.BaseTheme_MD3)
    }

    fun getThemeNameByWhich(which: Int): String {
        return kotlin.runCatching {
            styleTitleList[which]
        }.getOrDefault("System")
    }

    fun getNightModeByWhich(which: Int): Int {
        return kotlin.runCatching {
            nightModeList[which]
        }.getOrDefault(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
    }

    private fun updateThemeByType(which: Int?) {
        if (mCurrentWhich == which) {
            return
        }
        mCurrentWhich = which ?: 0
        colorCallBacks.rStyle = getThemeByWhich(mCurrentWhich)
        updateTheme(getNightModeByWhich(mCurrentWhich))
    }

    fun updateThemeByType(@StyleRes style: Int, name: String,@NightMode nightMode: Int = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY){
        val indexOfStyle = styleList.indexOf(style)
        val indexOfTitle = styleTitleList.indexOf(name)
        val indexOfNight = nightModeList.indexOf(nightMode)

        if (indexOfStyle == -1){
            addTheme(style,name,nightMode)
            updateThemeByType(styleList.size - 1)
            return
        }
        if (indexOfStyle == indexOfTitle && indexOfStyle == indexOfNight){
            updateThemeByType(indexOfStyle)
            return
        }
        addTheme(style,name,nightMode)
        updateThemeByType(styleList.size - 1)
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

val ThemeLiveData = SingleLiveEvent<Int>()

val isDark: Boolean
    get() {
        return ThemeLiveData.value != 1
    }




