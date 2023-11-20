@file:Suppress("DEPRECATION")

package me.shetj.base.tools.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Process
import androidx.annotation.Keep
import me.shetj.base.ktx.drawableToBitmap
import me.shetj.base.tools.file.FileUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.system.exitProcess

@Keep
class AppUtils private constructor() {
    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * 封装App信息的Bean类
     * @param name        名称
     * @param icon        图标
     * @param packageName 包名
     * @param packagePath 包路径
     * @param versionName 版本号
     * @param versionCode 版本码
     * @param isSystem    是否系统应用
     */
    class AppInfo(
        packageName: String,
        name: String,
        icon: Drawable,
        packagePath: String,
        versionName: String,
        versionCode: Int,
        isSystem: Boolean
    ) {

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
        @JvmStatic
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
        @JvmStatic
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
        @JvmStatic
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
        @JvmStatic
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
        @JvmStatic
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
        @JvmStatic
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
        @JvmStatic
        fun installApp(activity: Activity, file: File?, authority: String, requestCode: Int) {
            if (!FileUtils.isFileExists(file)) {
                return
            }
            activity.startActivityForResult(
                IntentUtils.getInstallAppIntent(file, authority),
                requestCode
            )
        }

        /**
         * 卸载App
         *
         * @param packageName 包名
         */
        @JvmStatic
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
        @JvmStatic
        fun uninstallApp(activity: Activity, packageName: String, requestCode: Int) {
            if (isSpace(packageName)) {
                return
            }
            activity.startActivityForResult(
                IntentUtils.getUninstallAppIntent(packageName),
                requestCode
            )
        }

        /**
         * 打开App
         *
         * @param packageName 包名
         */
        @JvmStatic
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
        @JvmStatic
        fun launchApp(activity: Activity, packageName: String, requestCode: Int) {
            if (isSpace(packageName)) {
                return
            }
            activity.startActivityForResult(
                IntentUtils.getLaunchAppIntent(packageName),
                requestCode
            )
        }

        /**
         * 关闭App
         */
        @JvmStatic
        fun exitApp() {
            val activityList = Utils.sActivityList
            for (i in activityList.indices.reversed()) {
                activityList[i].finish()
                activityList.removeAt(i)
            }
            exitProcess(0)
        }

        /**
         * 获取App包名
         *
         * @return App包名
         */
        @JvmStatic
        val appPackageName: String
            get() = Utils.app.packageName

        /**
         * 获取App具体设置
         *
         * @param packageName 包名
         */
        @JvmOverloads
        @JvmStatic
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
        @JvmStatic
        val appName: String?
            get() = getAppName(Utils.app.packageName)

        /**
         * 获取App名称
         *
         * @param packageName 包名
         * @return App名称
         */
        @JvmStatic
        fun getAppName(packageName: String): String? {
            if (isSpace(packageName)) {
                return null
            }
            return try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                pi?.applicationInfo?.loadLabel(pm)?.toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 获取App图标
         *
         * @return App图标
         */
        @JvmStatic
        val appIcon: Drawable?
            get() = getAppIcon(Utils.app.packageName)

        /**
         * 获取App图标
         *
         * @param packageName 包名
         * @return App图标
         */
        @JvmStatic
        fun getAppIcon(packageName: String): Drawable? {
            if (isSpace(packageName)) {
                return null
            }
            return try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                pi?.applicationInfo?.loadIcon(pm)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 获取App路径
         *
         * @return App路径
         */
        @JvmStatic
        val appPath: String?
            get() = getAppPath(Utils.app.packageName)

        /**
         * 获取App路径
         *
         * @param packageName 包名
         * @return App路径
         */
        @JvmStatic
        fun getAppPath(packageName: String): String? {
            if (isSpace(packageName)) {
                return null
            }
            return try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                pi?.applicationInfo?.sourceDir
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 获取App版本号
         *
         * @return App版本号
         */
        @JvmStatic
        val appVersionName: String?
            get() = getAppVersionName(Utils.app.packageName)

        /**
         * 获取App版本号
         *
         * @param packageName 包名
         * @return App版本号
         */
        @JvmStatic
        fun getAppVersionName(packageName: String): String? {
            if (isSpace(packageName)) {
                return null
            }
            return try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                pi?.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 获取App版本码
         *
         * @return App版本码
         */
        @JvmStatic
        val appVersionCode: Int
            get() = getAppVersionCode(Utils.app.packageName)

        /**
         * 获取App版本码
         *
         * @param packageName 包名
         * @return App版本码
         */
        @JvmStatic
        fun getAppVersionCode(packageName: String): Int {
            if (isSpace(packageName)) {
                return -1
            }
            return try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pi?.longVersionCode?.toInt() ?: -1
                } else {
                    pi?.versionCode ?: -1
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                -1
            }
        }

        /**
         * 判断App是否是系统应用
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        val isSystemApp: Boolean
            get() = isSystemApp(Utils.app.packageName)

        /**
         * 判断App是否是系统应用
         *
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        fun isSystemApp(packageName: String): Boolean {
            if (isSpace(packageName)) {
                return false
            }
            return try {
                val pm = Utils.app.packageManager
                val ai = pm.getApplicationInfo(packageName, 0)
                ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                false
            }
        }

        /**
         * 判断App是否是Debug版本
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        val isAppDebug: Boolean
            get() = isAppDebug(Utils.app.packageName)

        /**
         * 判断App是否是Debug版本
         *
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        fun isAppDebug(packageName: String): Boolean {
            if (isSpace(packageName)) {
                return false
            }
            return try {
                val pm = Utils.app.packageManager
                val ai = pm.getApplicationInfo(packageName, 0)
                ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                false
            }
        }

        /**
         * 获取App签名
         *
         * @return App签名
         */
        @JvmStatic
        val appSignature: Array<Signature>?
            get() = getAppSignature(Utils.app.packageName)

        /**
         * 获取App签名
         *
         * @param packageName 包名
         * @return App签名
         */
        @JvmStatic
        fun getAppSignature(packageName: String): Array<Signature>? {
            if (isSpace(packageName)) {
                return null
            }
            return try {
                val pm = Utils.app.packageManager

                @SuppressLint("PackageManagerGetSignatures")
                val pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                pi?.signatures
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 判断App是否处于前台
         * 可能触发隐私协议
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        val isAppForeground: Boolean
            get() {
                val manager =
                    Utils.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val info = manager.runningAppProcesses
                if (info.isNullOrEmpty()) {
                    return false
                }
                for (aInfo in info) {
                    if (aInfo.importance ==
                        ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    ) {
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
        @JvmStatic
        fun isAppForeground(packageName: String): Boolean {
            return !isSpace(packageName) && packageName == ProcessUtils.foregroundProcessName
        }

        /**
         * 获取App信息
         *
         * AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）
         *
         * @return 当前应用的AppInfo
         */
        @JvmStatic
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
        @JvmStatic
        fun getAppInfo(packageName: String): AppInfo? {
            return try {
                val pm = Utils.app.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                getBean(pm, pi)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
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
        @JvmStatic
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

        @JvmStatic
        val appIconBitmap: Bitmap? = try {
            val drawableIcon =
                Utils.app.packageManager.getApplicationIcon(Utils.app.applicationContext.packageName)
            drawableToBitmap(drawableIcon) ?: throw PackageManager.NameNotFoundException()
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }

        /**
         * 清除App所有数据
         *
         * @param dirPaths 目录路径
         * @return `true`: 成功<br></br>`false`: 失败
         */
        @JvmStatic
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
        @JvmStatic
        fun cleanAppData(dirs: Array<File?>): Boolean {
            var isSuccess = CleanUtils.cleanInternalCache()
            isSuccess = isSuccess and CleanUtils.cleanInternalDbs()
            isSuccess = isSuccess and CleanUtils.cleanInternalSP()
            isSuccess = isSuccess and CleanUtils.cleanInternalFiles()
            isSuccess = isSuccess and CleanUtils.cleanExternalCache()
            for (dir in dirs) {
                isSuccess = isSuccess and (dir?.let { CleanUtils.cleanCustomCache(it) } ?: true)
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

        /**
         * 通过文件获取当前进程
         * 没有同意协议之前无法进行获取进程
         */
        fun getCurrentProcessNameByFile(context: Context): String? {
            return try {
                val file = File("/proc/" + Process.myPid() + "/cmdline")
                val mBufferedReader = BufferedReader(FileReader(file))
                val processName = mBufferedReader.readLine().trim { it <= ' ' }
                mBufferedReader.close()
                processName
            } catch (e: Exception) {
                e.printStackTrace()
                context.packageName
            }
        }
    }
}
