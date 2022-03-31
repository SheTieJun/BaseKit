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
package me.shetj.base.tools.debug

import android.content.Context
import android.os.Environment
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import me.shetj.base.BuildConfig
import me.shetj.base.base.TaskExecutor
import me.shetj.base.constant.Constant.Companion.KEY_IS_OUTPUT_HTTP
import me.shetj.base.constant.Constant.Companion.KEY_IS_OUTPUT_LOG
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.base.tools.file.FileUtils
import me.shetj.base.tools.file.SPUtils
import timber.log.Timber

/**
 * debug 功能扩展 必须开启debug的情况下
 * 1. 对一些日志进行特殊的保留
 * 2. 对http请求日志进行文件输出
 */
class DebugFunc private constructor() {

    private var mContext: Context? = null

    var isOutputHttp = mContext?.let { SPUtils.get(it, KEY_IS_OUTPUT_HTTP, BuildConfig.DEBUG) as Boolean }
        ?: false
    var isOutputLog = mContext?.let { SPUtils.get(it, KEY_IS_OUTPUT_LOG, BuildConfig.DEBUG) as Boolean }
        ?: false

    companion object {
        val saveLogFile =
            EnvironmentStorage.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) +
                File.separatorChar + "BaseDebug.text"
        val saveHttpFile =
            EnvironmentStorage.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) +
                File.separatorChar + "HttpDebug.text"
        val logFilePath =
            EnvironmentStorage.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + "crashLog"

        @Volatile
        private var mDebugFunc: DebugFunc? = null

        @JvmStatic
        fun getInstance(): DebugFunc {
            return mDebugFunc ?: DebugFunc().also {
                mDebugFunc = it
            }
        }
    }

    //region 必须设置
    fun initContext(context: Context) {
        mContext = context.applicationContext
    }
    //endregion

    //region httpSetting
    fun saveHttpToFile(info: String?) {
        if (!isOutputHttp) return
        outputToFile(info, saveHttpFile)
    }

    fun getHttpSetting(): Boolean {
        return isOutputHttp
    }

    fun setIsOutputHttp(isOutput: Boolean) {
        isOutputHttp = isOutput
        mContext?.let {
            SPUtils.put(it, KEY_IS_OUTPUT_HTTP, isOutput)
        }
    }
    //endregion httpSetting

    //region logSetting
    fun getLogSetting(): Boolean {
        return isOutputLog
    }

    fun setIsOutputLog(isOutput: Boolean) {
        isOutputLog = isOutput
        mContext?.let {
            SPUtils.put(it, KEY_IS_OUTPUT_LOG, isOutput)
        }
    }

    fun saveLogToFile(info: String?) {
        if (!isOutputLog) return
        outputToFile(info, saveLogFile)
    }
    //endregion logSetting


    fun outputToFile(info: String?, path: String? = saveLogFile) {
        TaskExecutor.executeOnIO {
            if (info.isNullOrEmpty()) return@executeOnIO
            if (path.isNullOrEmpty()) return@executeOnIO
            try {
                val fw = BufferedWriter(FileWriter(path, true))
                fw.write("$info \n\n")
                fw.close()
                Timber.tag("error").e("写入本地文件成功：%s", path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearAll() {
        FileUtils.deleteFile(saveHttpFile)
        FileUtils.deleteFile(saveLogFile)
        FileUtils.deleteDir(
            EnvironmentStorage.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + "crashLog"
        )
    }
}
