package shetj.me.base.common.other

import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import me.shetj.base.tools.app.AppUtils

import java.io.File

import timber.log.Timber

/**
 * ** [DownloadService] 主要是为了app更新下载，直接执行安装处理</br>
 * ** 获取APPName [getApkName] ****<br></br>
 * ** 开启下载  [install]****<br></br>
 * ** @author shetj<br></br>
 */
class DownloadService : Service() {
    private var mReceiver: BroadcastReceiver? = null
    private var mDownLoadAPKId: Long = -1
    private var mVersionName: String? = null
    private var mNewestAppName: String? = null
    private var mDownloadUrl: String? = null

    private var mIsDownloading = false

    //--- Override methods -------------------------------------------------------------------------

    override fun onCreate() {
        super.onCreate()

        mDm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d("start download service")

        if (mIsDownloading) {
            Timber.d("download apk task is running, skip...")
        } else {
            mReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                    if (id == mDownLoadAPKId) {
                        if (null != mReceiver) {
                            unregisterReceiver(mReceiver)
                        }

                        mIsDownloading = false
                        installApk(context, mNewestAppName!!)
                        cleanUpOldApkThan(mVersionName)
                        stopSelf()
                    }
                }
            }

            registerReceiver(mReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            mVersionName = intent.getStringExtra(EXTRA_DOWNLOAD_VERSION)
            mNewestAppName = intent.getStringExtra(EXTRA_DOWNLOAD_APK_NAME)
            mDownloadUrl = intent.getStringExtra(EXTRA_DOWNLOAD_APK_URL)
            startDownLoad()
        }
        return Service.START_STICKY
    }

    override fun onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }
        super.onDestroy()
    }

    //--- Private methods --------------------------------------------------------------------------

    private fun startDownLoad() {
        Timber.d("start download apk")
        val request = DownloadManager.Request(
                Uri.parse(mDownloadUrl))
        request.setMimeType("application/vnd.android.package-archive")
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setTitle(mNewestAppName)
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mNewestAppName)
        mDownLoadAPKId = mDm!!.enqueue(request)
        mIsDownloading = true
    }

    companion object {

        //--- Private attributes -----------------------------------------------------------------------

        private val EXTRA_DOWNLOAD_VERSION = "com.shetj.me.DOWNLOAD_VERSION"
        private val EXTRA_DOWNLOAD_APK_NAME = "com.shetj.me.DOWNLOAD_APK_NAME"
        private val EXTRA_DOWNLOAD_APK_URL = "com.shetj.me.DOWNLOAD_APK_URL"

        private val APK_SUFFIX = "app-"

        private var mDm: DownloadManager? = null

        //--- Public static methods --------------------------------------------------------------------

        /**
         * 开启下载
         * @param appContext 上下文
         * @param versionName 版本名称
         * @param appName     使用[.getApkName]获取
         * @param downloadUrl 下载的路径
         */
        fun install(appContext: Context, versionName: String,
                    appName: String,
                    downloadUrl: String) {
            if (hasDownloadedApk(appName)) {
                installApk(appContext, appName)
            } else {
                val updateApkService = Intent(appContext, DownloadService::class.java)
                updateApkService.putExtra(EXTRA_DOWNLOAD_VERSION, versionName)
                updateApkService.putExtra(EXTRA_DOWNLOAD_APK_NAME, appName)
                updateApkService.putExtra(EXTRA_DOWNLOAD_APK_URL, downloadUrl)
                appContext.startService(updateApkService)
            }
        }

        /**
         * 判断是否下载了app
         * @param apkName 使用[.getApkName]获取
         * @return 判断是否下载了app true 下载了
         */
        fun hasDownloadedApk(apkName: String): Boolean {
            val apkPath = getDownloadedApkPath(apkName)
            val apkFile = File(apkPath)
            return apkFile.exists()
        }

        /**
         * 安装APK
         * @param context 上下文
         * @param apkName 使用[.getApkName]获取
         */
        fun installApk(context: Context, apkName: String) {
            val path = getDownloadedApkPath(apkName)
            Timber.i("%s%s", path, context.packageName)
            AppUtils.installApp(File(path), context.packageName + ".FileProvider")
        }

        /**
         * @param versionName 版本号
         * @param fileName 文件名称
         * @return 获取app名称
         */
        fun getApkName(versionName: String, fileName: String): String {
            return "$APK_SUFFIX$versionName-$fileName"
        }

        //--- Private static methods -------------------------------------------------------------------

        private val downloadDir: String
            get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .absolutePath

        private fun getDownloadedApkPath(apkName: String): String {
            return downloadDir + File.separator + apkName
        }

        private fun cleanUpOldApkThan(newestVersion: String?) {
            val downloadDir = File(downloadDir)
            if (downloadDir.isDirectory) {
                val files = downloadDir.listFiles()
                for (file in files) {
                    if (file.name.startsWith(APK_SUFFIX)) {
                        if (!file.name.contains(newestVersion!!)) {
                            val isDeleted = file.delete()
                            Timber.v("cleanUpOldApkThan: $isDeleted: ${file.absolutePath}")
                        }
                    }
                }
            }
        }
    }
}