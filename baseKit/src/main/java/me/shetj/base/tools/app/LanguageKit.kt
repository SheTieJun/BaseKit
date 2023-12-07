package me.shetj.base.tools.app

import android.app.LocaleConfig
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.LocaleManagerCompat
import androidx.core.content.getSystemService
import me.shetj.base.tools.file.SPUtils
import java.util.*

object LanguageKit {
    private const val SP_LANGUAGE = "SP_LANGUAGE"
    private const val SP_COUNTRY = "SP_COUNTRY"

    /**
     * 修改应用内语言设置
     * @param language  语言
     * @param area      地区
     */
    fun changeLanguage(context: Context, newLocale: Locale = Locale.ROOT) {
        setAppLanguage(context, newLocale)
        saveLanguageSetting(context, newLocale)
        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()
        if (defaultNightMode != AppCompatDelegate.MODE_NIGHT_YES) {
            // ActivityCompat recreate
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AppCompatDelegate.setDefaultNightMode(defaultNightMode)
        }
        if (defaultNightMode != AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            AppCompatDelegate.setDefaultNightMode(defaultNightMode)
        }
    }

    /**
     * 更新应用语言（核心）
     * @param context
     * @param locale
     */
    @Suppress("DEPRECATION")
    private fun setAppLanguage(context: Context, locale: Locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            context.getSystemService<LocaleManager>()?.overrideLocaleConfig = LocaleConfig(LocaleList(locale))
        } else {
            val resources = context.resources
            val metrics = resources.displayMetrics
            val configuration = resources.configuration
            configuration.setLocale(locale)
            configuration.setLocales(LocaleList(locale))
            context.createConfigurationContext(configuration)
            resources.updateConfiguration(configuration, metrics)
        }

    }

    /**
     * 跟随系统语言
     */
    fun attachBaseContext(context: Context): Context {
        val language = SPUtils.get(context, SP_LANGUAGE, "") as String
        val country = SPUtils.get(context, SP_COUNTRY, "") as String
        if (!TextUtils.isEmpty(language)) {
            // 强制修改应用语言
            if (!isSameWithSetting(context)) {
                val locale = Locale(language, country)
                setAppLanguage(context, locale)
            }
        }
        return context
    }

    /**
     * 判断SharedPrefences中存储和app中的多语言信息是否相同
     */
    fun isSameWithSetting(context: Context): Boolean {
        val locale = getAppLocale(context)
        val language = locale.language
        val country = locale.country
        val spLanguage = SPUtils.get(context, SP_LANGUAGE, "") as String
        val spCountry = SPUtils.get(context, SP_COUNTRY, "") as String
        return language == spLanguage && country == spCountry
    }

    /**
     * 保存多语言信息到sp中
     */
    fun saveLanguageSetting(context: Context, locale: Locale) {
        SPUtils.put(context, SP_LANGUAGE, locale.language)
        SPUtils.put(context, SP_COUNTRY, locale.country)
    }

    /**
     * 获取应用语言
     */
    fun getAppLocale(context: Context): Locale {
        return context.resources.configuration.locales[0]
    }
}
