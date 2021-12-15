/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package me.shetj.base.tools.app

import android.util.Log
import timber.log.Timber
import java.io.File

object Tim {

    private val LOG_FILE_PATH = Utils.app.externalCacheDir?.path + File.separator + "baseLog.test"

    /**
     * 设置log自动在debug打开，在release关闭
     * @param isDebug
     */
    fun setLogAuto(isDebug: Boolean) {
        Timber.uprootAll()
        //打印关，同时gradle中的release的debuggable要设置为false
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        } else {//release版本
            Timber.plant(CrashReportingTree())
        }
    }

    /**
     * 设置log自动，并且想在release时仅在测试时有打印，
     * 在release版本时增加判断磁盘目录下是否存在文件 baseLog.test，
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
                when (priority) {
                    Log.ERROR -> {
                        Log.e(tag, message)
                    }
                    Log.WARN -> {
                        // FakeCrashLibrary.logWarning(t);
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun getTag(o: Any): String {
        return if (o is Class<*>) o.simpleName else o.javaClass.simpleName
    }
}