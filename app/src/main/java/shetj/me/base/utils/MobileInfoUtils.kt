package shetj.me.base.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.Keep
import me.shetj.base.tools.app.AppUtils
import me.shetj.base.tools.file.SPUtils
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@Keep
object MobileInfoUtils {


    private var mSetStopAutoStart: Method? = null
    private var mgetStopAutoStart: Method? = null

    /**
     * Get Mobile Type
     *
     * @return
     */
    private val mobileType: String
        get() = Build.MANUFACTURER

    /**
     * GoTo Open Self Setting Layout
     * Compatible Mainstream Models 兼容市面主流机型
     *
     * @param context 上下文
     */
    private fun jumpStartInterface(context: Context) {
        var intent = Intent()
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.e("HLQ_Struggle", "******************当前手机型号为：$mobileType")
            var componentName: ComponentName? = null
            val brand = android.os.Build.BRAND
            when (brand.toLowerCase()) {
                "samsung" -> componentName = ComponentName("com.samsung.android.sm",
                        "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity")
                "huawei" -> componentName = ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
                "xiaomi" -> componentName = ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity")
                "vivo" -> componentName = ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")
                "oppo" -> componentName = ComponentName("com.coloros.oppoguardelf",
                        "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")
                "360" -> componentName = ComponentName("com.yulong.android.coolsafe",
                        "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity")
                "meizu" -> componentName = ComponentName("com.meizu.safe",
                        "com.meizu.safe.permission.SmartBGActivity")
                "oneplus" -> componentName = ComponentName("com.oneplus.security",
                        "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity")
                else -> {
                }
            }
            if (componentName != null) {
                intent.component = componentName
            } else {
                intent.action = Settings.ACTION_SETTINGS
            }
            intent.component = componentName
            context.startActivity(intent)
        } catch (e: Exception) {//抛出异常就直接打开设置页面
            intent = Intent(Settings.ACTION_SETTINGS)
            context.startActivity(intent)
        }

    }


    fun jumpStartInterface(activity: Activity, isSelf: Boolean) {
        if (isOpenAuto(activity)) {
            //一天提醒一次
            SPUtils.put(activity, "AutoStart" + shetj.me.base.utils.TimeUtil.getYMDime(), false)
            try {
//                (activity).show {
//                    title(R.string.app_name)
//                    message(text = "由于安卓系统设置，为获取最新的信息推送，请手动开启自启动权限！")
//                    positiveButton(text = "立即设置") { dialog ->
//                        SPUtils.put(activity, "AutoStart", false)
//                        if (isSelf) {
//                            toSelfSetting(activity)
//                        } else {
//                            jumpStartInterface(activity as Context)
//                        }
//                        dialog.dismiss()
//                    }
//                    negativeButton(text = "暂不设置") { dialog ->
//                        dialog.dismiss()
//                        SPUtils.put(activity, "AutoStart" + AppUtils.appVersionCode, false)
//                    }
//
//                }
            } catch (ignored: Exception) {
            }
        }

    }

    /**
     * 是否开启提醒
     *
     * 默认一天打开一次
     * 如果点击暂不设置，一个版本提醒一次
     * 如果点击设置，则默认打开了自启动，以后不再提醒
     * @param activity
     * @return
     */
    private fun isOpenAuto(activity: Activity): Boolean {
        return (SPUtils.get(activity, "AutoStart", true) as Boolean &&
                //根据版本来，这个版本不提醒
                SPUtils.get(activity, "AutoStart" + AppUtils.appVersionCode, true) as Boolean
                && SPUtils.get(activity, "AutoStart" + shetj.me.base.utils.TimeUtil.getYMDime(), true) as Boolean)
    }


    //需要root
    fun fobidAutoRun(context: Context, pkg: String, isFobid: Boolean) {

        if (isForceStopAutoStartMethodExist(context)) {
            try {
                val am = context
                        .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                mSetStopAutoStart!!.invoke(am, pkg,
                        isFobid)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

        }
    }

    fun isForceStopAutoStartMethodExist(context: Context): Boolean {
        synchronized(MobileInfoUtils::class.java) {
            if (mSetStopAutoStart == null) {
                try {
                    val am = context
                            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    mSetStopAutoStart = am.javaClass.getMethod(
                            "setForbiddenAutorunPackages", String::class.java,
                            Boolean::class.javaPrimitiveType)
                    mgetStopAutoStart = am.javaClass
                            .getMethod("getForbiddenAutorunPackages")
                } catch (e: SecurityException) {
                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                }

            }
            return if (mSetStopAutoStart == null || mgetStopAutoStart == null) {
                false
            } else {
                true
            }
        }
    }

    fun toSelfSetting(context: Context) {
        val mIntent = Intent()
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        mIntent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(mIntent)
    }
}
