package me.shetj.base.tools.file

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import me.shetj.base.tools.app.Utils
import java.io.File


/**
 * SD卡相关的辅助类
 *
 * 从Android 10开始（API level 29），Android将对外部存储进行一定的限制。
 * 默认情况下，对于外部存储，App只能通过Context.getExternalFilesDir()访问自己的特定文件目录；
 * [Environment]
 *<BR>
 * * 内部存储
 * /data/data/包名/files :
 *context.getFilesDir().getPath()
 *
 * /data/data/包名/cache :
 *context.getCacheDir().getPath()
 *
 * * 外部存储
 * /sdcard/Android/data/包名/cache/dir :
 *context.getExternalFilesDir("dir").getPath()
 *
 * /sdcard/Android/data/包名/cache :
 *context.getExternalCacheDir().getPath()
 *
 *
 * * TIP:
 *  1.不想被轻易删掉的文件，不可以放在 cache 下面
 *  <BR>
 *
 * @author shetj
 */
@Suppress("DEPRECATION")
@Keep
class EnvironmentStorage private constructor() {

    companion object {
        /**
         * 当路径不路径不存在会自动创建
         * @param packagePath 包的路径
         * @return
         */
        @JvmOverloads
        @JvmStatic
        fun getPath(root: String = filesDir, packagePath: String): String {
            val path = StringBuilder(root)
            val f = packagePath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (aF in f) {
                val dirFile = File("$path/$aF")
                if (!dirFile.exists()) {
                    dirFile.mkdir()
                }
                path.append("/").append(aF)
            }
            return path.toString()
        }

        /**
         * 判断SDCard是否可用
         *
         * @return
         */
        @JvmStatic
        val isSDCardEnable: Boolean
            get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED


        @RequiresApi(Build.VERSION_CODES.R)
        fun requestFileAccess(context :Context):Boolean{
            val isHasStoragePermission =  Environment.isExternalStorageManager()
            if (!isHasStoragePermission){
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                context.startActivity(intent)
            }
            return isHasStoragePermission
        }

        /**
         * 获取SD卡路径
         *
         * @return  mnt/sdcard/
         */
        @JvmStatic
        val sdCardPath: String
            get() = if (isSDCardEnable) {
                Environment.getExternalStorageDirectory().absolutePath
            } else {
                throw RuntimeException("sdcard is unmounted")
            }


        /**
         * 获取系统存储路径
         * /system
         */
        @JvmStatic
        val rootDirectoryPath: String
            get() = Environment.getRootDirectory().absolutePath


        /**
         * data/data/< package name >/files/
         */
        @JvmStatic
        val filesDir: String
            get() = Utils.app.filesDir.absolutePath


        /**
         * mnt/sdcard/Android/data/< package name >/files/type
         * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
         * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
         *   type =   {{@link android.os.EnvironmentDIRECTORY_MUSIC}},
         *            {@link android.os.Environment#DIRECTORY_PODCASTS},
         *            {@link android.os.Environment#DIRECTORY_RINGTONES},
         *            {@link android.os.Environment#DIRECTORY_ALARMS},
         *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
         *            {@link android.os.Environment#DIRECTORY_PICTURES}, or
         *            {@link android.os.Environment#DIRECTORY_MOVIES}. or null
         */
        @JvmStatic
        fun getExternalFilesDir(type: String = Environment.DIRECTORY_DOWNLOADS): String {
            val file: File? = Utils.app.getExternalFilesDir(type)
            return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && file != null) {
                file.absolutePath
            } else {
                Utils.app.filesDir.toString() + File.separator + type
            }
        }

        /**
         * data/data/< package name >/cache
         * @return
         */
        @JvmStatic
        val cache: String
            get() = Utils.app.cacheDir.absolutePath


        @JvmStatic
        val downloadCache: String
            get() = if (Utils.app.externalCacheDir == null) {
                Environment.getDownloadCacheDirectory().absolutePath
            } else {
                Utils.app.externalCacheDir!!.absolutePath
            }
    }


}
