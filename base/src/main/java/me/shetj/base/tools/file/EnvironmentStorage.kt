package me.shetj.base.tools.file

import android.os.Environment
import androidx.annotation.Keep
import me.shetj.base.tools.app.Utils

import java.io.File

/**
 * SD卡相关的辅助类
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
        @JvmStatic
        fun getPath(packagePath: String): String {
            val path = StringBuilder(filesDir)
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
            return Utils.app.getExternalFilesDir(type)!!.absolutePath
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
