package me.shetj.base.tools.debug

import android.content.Context
import android.os.Environment
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import me.shetj.base.BuildConfig
import me.shetj.base.base.TaskExecutor
import me.shetj.base.constant.Constant.Companion.KEY_IS_OUTPUT_HTTP
import me.shetj.base.constant.Constant.Companion.KEY_IS_OUTPUT_LOG
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.base.tools.file.FileUtils
import me.shetj.base.tools.file.SPUtils
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

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
        val saveLogFile = EnvironmentStorage.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separatorChar + "BaseDebug.text"
        val saveHttpFile = EnvironmentStorage.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separatorChar + "HttpDebug.text"
        val logFilePath = EnvironmentStorage.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "crashLog"

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

    fun setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(object : Consumer<Throwable?> {
            @Throws(Exception::class)
            override fun accept(throwable: Throwable?) {
                if (throwable == null) return
                throwable.printStackTrace()
                if(isOutputLog) {
                    outputToFile(throwable.message)
                }
            }
        })
    }

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
        FileUtils.deleteDir(EnvironmentStorage.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "crashLog")
    }

}