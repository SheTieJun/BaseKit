package me.shetj.base.tools.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import androidx.annotation.Keep

import java.io.File
import java.util.ArrayList

import me.shetj.base.tools.file.FileUtils

@Keep
class AppUtils private constructor() {
    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * 封装App信息的Bean类
     */
    class AppInfo
    /**
     * @param name        名称
     * @param icon        图标
     * @param packageName 包名
     * @param packagePath 包路径
     * @param versionName 版本号
     * @param versionCode 版本码
     * @param isSystem    是否系统应用
     */
    (packageName: String, name: String, icon: Drawable, packagePath: String,
     versionName: String, versionCode: Int, isSystem: Boolean) {

        var name: String? = null
        var icon: Drawable? = null
        var packageName: String? = null
        var packagePath: String? = null
        var versionName: String? = null
        var versionCode: Int = 0
        var isSystem: Boolean = false

        init {
            this.name = name
            this.icon = icon
            this.packageName = packageName
            this.packagePath = packagePath
            this.versionName = versionName
            this.versionCode = versionCode
            this.isSystem = isSystem
        }

        override fun toString(): String {
            return "pkg name: " + packageName +
                    "\napp name: " + name +
                    "\napp path: " + packagePath +
                    "\napp v name: " + versionName +
                    "\napp v code: " + versionCode +
                    "\nis system: " + isSystem
        }
    }

    companion object {
        fun scanLocalInstallAppList(packageManager: PackageManager, name: String): List<AppInfos> {
            val myAppInfos = ArrayList<AppInfos>()
            val packages = packageManager.getInstalledPackages(0)
            for (packageInfo in packages) {
                // 判断系统/非系统应用
                if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0)
                // 非系统应用
                {
                    if (packageInfo.packageName.contains(name)) {
                        val myAppInfo = AppInfos()
                        myAppInfo.bundleID = packageInfo.packageName
                        myAppInfo.version = packageInfo.versionName
                        myAppInfos.add(myAppInfo)
                    }
                }
            }
            return myAppInfos

        }


        /**
         * 判断App是否安装
         *
         * @param action   action
         * @param category category
         * @return `true`: 已安装<br></br>`false`: 未安装
         */
        fun isInstallApp(action: String, category: String): Boolean {
            val intent = Intent(action)
            intent.addCategory(category)
            val pm = Utils.app.packageManager
            val info = pm.resolveActivity(intent, 0)
            return info != null
        }

        /**
         * 判断App是否安装
         *
         * @param packageName 包名
         * @return `true`: 已安装<br></br>`false`: 未安装
         */
        fun isInstallApp(packageName: String): Boolean {
            return !isSpace(packageName) && IntentUtils.getLaunchAppIntent(packageName) != null
        }

        /**
         * 安装App(支持8.0)
         *
         * 8.0需添加权限 `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param filePath  文件路径
         * @param authority 7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         */
        fun installApp(filePath: String, authority: String) {
            installApp(FileUtils.getFileByPath(filePath), authority)
        }

        /**
         * 安装App（支持8.0）
         *
         * 8.0需添加权限 `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param file      文件
         * @param authority 7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         */
        fun installApp(file: File?, authority: String) {
            if (!FileUtils.isFileExists(file)) {
                return
            }
            Utils.app.startActivity(IntentUtils.getInstallAppIntent(file, authority))
        }

        /**
         * 安装App（支持8.0）
         *
         * 8.0需添加权限 `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param activity    activity
         * @param filePath    文件路径
         * @param authority   7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         * @param requestCode 请求值
         */
        fun installApp(activity: Activity, filePath: String, authority: String, requestCode: Int) {
            installApp(activity, FileUtils.getFileByPath(filePath), authority, requestCode)
        }

        /**
         * 安装App（支持8.0）
         *
         * 8.0需添加权限 `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param activity    activity
         * @param file        文件
         * @param authority   7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
         * <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
         * @param requestCode 请求值
         */
        fun installApp(activity: Activity, file: File?, authority: String, requestCode: Int) {
            if (!FileUtils.isFileExists(file)) {
                return
            }
            activity.startActivityForResult(IntentUtils.getInstallAppIntent(file, authority), requestCode)
        }


        /**
         * 卸载App
         *
         * @param packageName 包名
         */
        fun uninstallApp(packageName: String) {
            if (isSpace(packageName)) {
                return
            }
            Utils.app.startActivity(IntentUtils.getUninstallAppIntent(packageName))
        }

        /**
         * 卸载App
         *
         * @param activity    activity
         * @param packageName 包名
         * @param requestCode 请求值
         */
        fun uninstallApp(activity: Activity, packageName: String, requestCode: Int) {
            if (isSpace(packageName)) {
                return
            }
            activity.startActivityForResult(IntentUtils.getUninstallAppIntent(packageName), requestCode)
        }


        /**
         * 打开App
         *
         * @param packageName 包名
         */
        fun launchApp(packageName: String) {
            if (isSpace(packageName)) {
                return
            }
            Utils.app.startActivity(IntentUtils.getLaunchAppIntent(packageName))
        }

        /**
         * 打开App
         *
         * @param activity    activity
         * @param packageName 包名
         * @param requestCode 请求值
         */
        fun launchApp(activity: Activity, packageName: String, requestCode: Int) {
            if (isSpace(packageName)) {
                return
            }
            activity.startActivityForResult(IntentUtils.getLaunchAppIntent(packageName), requestCode)
        }

        /**
         * 关闭App
         */
        fun exitApp() {
            val activityList = Utils.sActivityList
            for (i in activityList.indices.reversed()) {
                activityList[i].finish()
                activityList.removeAt(i)
            }
            System.exit(0)
        }

        /**
         * 获取App包名
         *
         * @return App包名
         */
        val appPackageName: String
            get() = Utils.app.packageName

        /**
         * 获取App具体设置
         *
         * @param packageName 包名
         */
        @JvmOverloads
        fun getAppDetailsSettings(packageName: String = Utils.app.packageName) {
            if (isSpace(packageName)) {
                return
            }
            Utils.app.startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName))
        }

        /**
         * 获取App名称
         *
         * @return App名称
         */
        val appName: String?
            get() = getAppName(Utils.app.packageName)

        /**
         * 获取App名称
         *
         * @param packageName 包名
         * @return App名称
         */
        fun getAppName(packageName: String): String? {
            if (isSpace(packageName)) {
                return null
            }
            try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.applicationInfo?.loadLabel(pm)?.toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取App图标
         *
         * @return App图标
         */
        val appIcon: Drawable?
            get() = getAppIcon(Utils.app.packageName)

        /**
         * 获取App图标
         *
         * @param packageName 包名
         * @return App图标
         */
        fun getAppIcon(packageName: String): Drawable? {
            if (isSpace(packageName)) {
                return null
            }
            try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.applicationInfo?.loadIcon(pm)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取App路径
         *
         * @return App路径
         */
        val appPath: String?
            get() = getAppPath(Utils.app.packageName)

        /**
         * 获取App路径
         *
         * @param packageName 包名
         * @return App路径
         */
        fun getAppPath(packageName: String): String? {
            if (isSpace(packageName)) {
                return null
            }
            try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.applicationInfo?.sourceDir
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取App版本号
         *
         * @return App版本号
         */
        val appVersionName: String?
            get() = getAppVersionName(Utils.app.packageName)

        /**
         * 获取App版本号
         *
         * @param packageName 包名
         * @return App版本号
         */
        fun getAppVersionName(packageName: String): String? {
            if (isSpace(packageName)) {
                return null
            }
            try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取App版本码
         *
         * @return App版本码
         */
        val appVersionCode: Int
            get() = getAppVersionCode(Utils.app.packageName)

        /**
         * 获取App版本码
         *
         * @param packageName 包名
         * @return App版本码
         */
        fun getAppVersionCode(packageName: String): Int {
            if (isSpace(packageName)) {
                return -1
            }
            try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.versionCode ?: -1
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return -1
            }

        }

        /**
         * 判断App是否是系统应用
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isSystemApp: Boolean
            get() = isSystemApp(Utils.app.packageName)

        /**
         * 判断App是否是系统应用
         *
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isSystemApp(packageName: String): Boolean {
            if (isSpace(packageName)) {
                return false
            }
            try {
                val pm = Utils.app.packageManager
                val ai = pm.getApplicationInfo(packageName, 0)
                return ai != null && ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * 判断App是否是Debug版本
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isAppDebug: Boolean
            get() = isAppDebug(Utils.app.packageName)

        /**
         * 判断App是否是Debug版本
         *
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isAppDebug(packageName: String): Boolean {
            if (isSpace(packageName)) {
                return false
            }
            try {
                val pm = Utils.app.packageManager
                val ai = pm.getApplicationInfo(packageName, 0)
                return ai != null && ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * 获取App签名
         *
         * @return App签名
         */
        val appSignature: Array<Signature>?
            get() = getAppSignature(Utils.app.packageName)

        /**
         * 获取App签名
         *
         * @param packageName 包名
         * @return App签名
         */
        fun getAppSignature(packageName: String): Array<Signature>? {
            if (isSpace(packageName)) {
                return null
            }
            try {
                val pm = Utils.app.packageManager
                @SuppressLint("PackageManagerGetSignatures")
                val pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                return pi?.signatures
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }


        /**
         * 判断App是否处于前台
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        val isAppForeground: Boolean
            get() {
                val manager = Utils.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val info = manager.runningAppProcesses
                if (info == null || info.size == 0) {
                    return false
                }
                for (aInfo in info) {
                    if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return aInfo.processName == Utils.app.packageName
                    }
                }
                return false
            }

        /**
         * 判断App是否处于前台
         *
         * 当不是查看当前App，且SDK大于21时，
         * 需添加权限 `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>`
         *
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isAppForeground(packageName: String): Boolean {
            return !isSpace(packageName) && packageName == ProcessUtils.getForegroundProcessName()
        }

        /**
         * 获取App信息
         *
         * AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）
         *
         * @return 当前应用的AppInfo
         */
        val appInfo: AppInfo?
            get() = getAppInfo(Utils.app.packageName)

        /**
         * 获取App信息
         *
         * AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）
         *
         * @param packageName 包名
         * @return 当前应用的AppInfo
         */
        fun getAppInfo(packageName: String): AppInfo? {
            try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return getBean(pm, pi)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 得到AppInfo的Bean
         *
         * @param pm 包的管理
         * @param pi 包的信息
         * @return AppInfo类
         */
        private fun getBean(pm: PackageManager?, pi: PackageInfo?): AppInfo? {
            if (pm == null || pi == null) {
                return null
            }
            val ai = pi.applicationInfo
            val packageName = pi.packageName
            val name = ai.loadLabel(pm).toString()
            val icon = ai.loadIcon(pm)
            val packagePath = ai.sourceDir
            val versionName = pi.versionName
            val versionCode = pi.versionCode
            val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
            return AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem)
        }

        /**
         * 获取所有已安装App信息
         *
         * [.getBean]（名称，图标，包名，包路径，版本号，版本Code，是否系统应用）
         *
         * 依赖上面的getBean方法
         *
         * @return 所有已安装的AppInfo列表
         */
        // 获取系统中安装的所有软件信息
        val appsInfo: List<AppInfo>
            get() {
                val list = ArrayList<AppInfo>()
                val pm = Utils.app.packageManager
                val installedPackages = pm.getInstalledPackages(0)
                for (pi in installedPackages) {
                    val ai = getBean(pm, pi) ?: continue
                    list.add(ai)
                }
                return list
            }

        /**
         * 清除App所有数据
         *
         * @param dirPaths 目录路径
         * @return `true`: 成功<br></br>`false`: 失败
         */
        fun cleanAppData(vararg dirPaths: String): Boolean {
            val dirs = arrayOfNulls<File>(dirPaths.size)
            var i = 0
            for (dirPath in dirPaths) {
                dirs[i++] = File(dirPath)
            }
            return cleanAppData(dirs)
        }

        /**
         * 清除App所有数据
         *
         * @param dirs 目录
         * @return `true`: 成功<br></br>`false`: 失败
         */
        fun cleanAppData(dirs: Array<File?>): Boolean {
            var isSuccess = CleanUtils.cleanInternalCache()
            isSuccess = isSuccess and CleanUtils.cleanInternalDbs()
            isSuccess = isSuccess and CleanUtils.cleanInternalSP()
            isSuccess = isSuccess and CleanUtils.cleanInternalFiles()
            isSuccess = isSuccess and CleanUtils.cleanExternalCache()
            for (dir in dirs) {
                isSuccess = isSuccess and CleanUtils.cleanCustomCache(dir)
            }
            return isSuccess
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) {
                return true
            }
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }
}
/**
 * 获取App具体设置
 */
