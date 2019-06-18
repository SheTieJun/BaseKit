package me.shetj.base.tools.app

import android.os.Environment
import android.util.Log

import java.io.File

import timber.log.Timber

object TimberUtil {

    private val LOG_FILE_PATH = Environment.getExternalStorageDirectory().path + File.separator + "baseLog.test"

    /**
     * 设置log自动在debug打开，在release关闭，可以在Application的onCreate中设置
     * @param isDebug
     */
    fun setLogAuto(isDebug: Boolean) {
        //打印关，同时gradle中的release的debuggable要设置为false
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        } else {//release版本
            Timber.plant(CrashReportingTree())
        }
    }

    /**
     * 设置log自动，并且想在release时仅在测试时有打印，
     * 在release版本时增加判断磁盘目录下是否存在文件 log.test，
     * 测试时让测试人员在磁盘目录下建立这么个文件。
     * 注意，如果读取存储需要权限申请的话，需要先获得权限，才能调用
     */
    fun setLogAutoEx(isDebug: Boolean) {
        if (isDebug) {//debug版本
            Timber.plant(Timber.DebugTree())
        } else {//release版本
            val logFile = File(LOG_FILE_PATH)
            if (logFile.exists()) {
                //打印开
                Timber.plant(Timber.DebugTree())
            } else {
                //打印关，同时gradle中的release的debuggable要设置为false
                Timber.plant(CrashReportingTree())
            }
        }
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    //FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    // FakeCrashLibrary.logWarning(t);
                } else {

                }
            }
        }
    }

}