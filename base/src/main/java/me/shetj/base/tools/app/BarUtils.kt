package me.shetj.base.tools.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.FloatRange
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import timber.log.Timber
import java.util.regex.Pattern

/**
 * <pre>
 * desc  : 栏相关工具类
</pre> *
 */
@Keep
class BarUtils private constructor() {


    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    @Suppress("DEPRECATION")
    companion object {


        var DEFAULT_COLOR = 0

        /**
         * Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 0.2f : 0.3f;
         */
        var DEFAULT_ALPHA = 0f
        val MIN_API = 19

        /**
         * 沉侵
         */
        @JvmStatic
        fun immersive(activity: Activity) {
            immersive(activity.window)
        }

        @JvmStatic
        fun immersive(activity: Activity, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
            immersive(activity.window, color, alpha)
        }

        @JvmStatic
        fun immersive(activity: Activity, color: Int) {
            immersive(activity.window, color, 1f)
        }

        @JvmStatic
        fun immersive(window: Window) {
            immersive(window, DEFAULT_COLOR, DEFAULT_ALPHA)
        }

        @JvmOverloads
        fun immersive(window: Window, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1f) {
            if (Build.VERSION.SDK_INT >= 21) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = mixtureColor(color, alpha)

                var systemUiVisibility = window.decorView.systemUiVisibility
                systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.decorView.systemUiVisibility = systemUiVisibility
            } else if (Build.VERSION.SDK_INT >= 19) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                setTranslucentView(window.decorView as ViewGroup, color, alpha)
            } else if (Build.VERSION.SDK_INT >= MIN_API && Build.VERSION.SDK_INT > 16) {
                var systemUiVisibility = window.decorView.systemUiVisibility
                systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.decorView.systemUiVisibility = systemUiVisibility
            }
        }

        @JvmStatic
        fun darkMode(activity: Activity, dark: Boolean) {
            if (isFlyme4Later) {
                darkModeForFlyme4(activity.window, dark)
            } else if (isMIUI6Later) {
                darkModeForMIUI6(activity.window, dark)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                darkModeForM(activity.window, dark)
            }
        }

        /** 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)  */
        @JvmStatic
        fun darkMode(activity: Activity) {
            darkMode(activity.window, DEFAULT_COLOR, DEFAULT_ALPHA)
        }

        @JvmStatic
        fun darkMode(activity: Activity, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
            darkMode(activity.window, color, alpha)
        }

        /** 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)  */
        @JvmStatic
        fun darkMode(window: Window, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
            when {
                isFlyme4Later -> {
                    darkModeForFlyme4(window, true)
                    immersive(window, color, alpha)
                }
                isMIUI6Later -> {
                    darkModeForMIUI6(window, true)
                    immersive(window, color, alpha)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    darkModeForM(window, true)
                    immersive(window, color, alpha)
                }
                Build.VERSION.SDK_INT >= 19 -> {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    setTranslucentView(window.decorView as ViewGroup, color, alpha)
                }
                else -> immersive(window, color, alpha)
            }

        }

        /** android 6.0设置字体颜色  */
        @RequiresApi(Build.VERSION_CODES.M)
        private fun darkModeForM(window: Window, dark: Boolean) {
            var systemUiVisibility = window.decorView.systemUiVisibility
            systemUiVisibility = if (dark) {
                systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            window.decorView.systemUiVisibility = systemUiVisibility
        }

        /**
         * 设置Flyme4+的darkMode,darkMode时候字体颜色及icon变黑
         * http://open-wiki.flyme.cn/index.php?title=Flyme%E7%B3%BB%E7%BB%9FAPI
         */
        @JvmStatic
        fun darkModeForFlyme4(window: Window?, dark: Boolean): Boolean {
            var result = false
            if (window != null) {
                try {
                    val e = window.attributes
                    val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                    val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
                    darkFlag.isAccessible = true
                    meizuFlags.isAccessible = true
                    val bit = darkFlag.getInt(null)
                    var value = meizuFlags.getInt(e)
                    if (dark) {
                        value = value or bit
                    } else {
                        value = value and bit.inv()
                    }

                    meizuFlags.setInt(e, value)
                    window.attributes = e
                    result = true
                } catch (var8: Exception) {
                    Timber.e("StatusBar: darkIcon: failed")
                }

            }

            return result
        }

        /**
         * 设置MIUI6+的状态栏是否为darkMode,darkMode时候字体颜色及icon变黑
         * http://dev.xiaomi.com/doc/p=4769/
         */
        @SuppressLint("PrivateApi")
        @JvmStatic
        fun darkModeForMIUI6(window: Window, darkmode: Boolean): Boolean {
            val clazz = window.javaClass
            return try {
                var darkModeFlag: Int
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                extraFlagField.invoke(window, if (darkmode) darkModeFlag else 0, darkModeFlag)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        }

        internal var pattern = Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE)

        /** 判断是否Flyme4以上  */
        private val isFlyme4Later: Boolean
            get() = (Build.FINGERPRINT.contains("Flyme_OS_4")
                    || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4")
                    || pattern.matcher(Build.DISPLAY).find())

        /** 判断是否为MIUI6以上  */
        private val isMIUI6Later: Boolean
            @SuppressLint("PrivateApi")
            get() {
                return try {
                    val clz = Class.forName("android.os.SystemProperties")
                    val mtd = clz.getMethod("get", String::class.java)
                    var `val` = mtd.invoke(null, "ro.miui.ui.version.name") as String
                    `val` = `val`.replace("[vV]".toRegex(), "")
                    val version = Integer.parseInt(`val`)
                    version >= 6
                } catch (e: Exception) {
                    false
                }

            }

        /** 增加View的paddingTop,增加的值为状态栏高度  */
        @JvmStatic
        fun setPadding(context: Context, view: View) {
            if (Build.VERSION.SDK_INT >= MIN_API) {
                view.setPadding(view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                        view.paddingRight, view.paddingBottom)
            }
        }

        /** 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度) */
        @JvmStatic
        fun setPaddingSmart(context: Context, view: View) {
            if (Build.VERSION.SDK_INT >= MIN_API) {
                val lp = view.layoutParams
                if (lp != null && lp.height > 0) {
                    //增高
                    lp.height += getStatusBarHeight(context)
                }
                view.setPadding(view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                        view.paddingRight, view.paddingBottom)
            }
        }

        /** 增加View的高度以及paddingTop,增加的值为状态栏高度.一般是在沉浸式全屏给ToolBar用的  */
        @JvmStatic
        fun setHeightAndPadding(context: Context, view: View) {
            if (Build.VERSION.SDK_INT >= MIN_API) {
                val lp = view.layoutParams
                //增高
                lp.height += getStatusBarHeight(context)
                view.setPadding(view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                        view.paddingRight, view.paddingBottom)
            }
        }

        /** 增加View上边距（MarginTop）一般是给高度为 WARP_CONTENT 的小控件用的 */
        @JvmStatic
        fun setMargin(context: Context, view: View) {
            if (Build.VERSION.SDK_INT >= MIN_API) {
                val lp = view.layoutParams
                if (lp is ViewGroup.MarginLayoutParams) {
                    //增高
                    lp.topMargin += getStatusBarHeight(context)
                }
                view.layoutParams = lp
            }
        }

        /**
         * 创建假的透明栏
         */
        @JvmStatic
        fun setTranslucentView(container: ViewGroup, color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) {
            if (Build.VERSION.SDK_INT >= 19) {
                val mixtureColor = mixtureColor(color, alpha)
                var translucentView: View? = container.findViewById(android.R.id.custom)
                if (translucentView == null && mixtureColor != 0) {
                    translucentView = View(container.context)
                    translucentView.id = android.R.id.custom
                    val lp = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.context))
                    container.addView(translucentView, lp)
                }
                translucentView?.setBackgroundColor(mixtureColor)
            }
        }

        @JvmStatic
        fun mixtureColor(color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
            val a = if (color and -0x1000000 == 0) 0xff else color.ushr(24)
            return color and 0x00ffffff or ((a * alpha).toInt() shl 24)
        }

        /** 获取状态栏高度  */
        @JvmStatic
        fun getStatusBarHeight(context: Context): Int {
            var result = 24
            val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            result = if (resId > 0) {
                context.resources.getDimensionPixelSize(resId)
            } else {
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        result.toFloat(), Resources.getSystem().displayMetrics).toInt()
            }
            return result
        }
    }

}