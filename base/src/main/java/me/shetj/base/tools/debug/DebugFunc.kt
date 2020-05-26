package me.shetj.base.tools.debug

import android.content.Context
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import me.shetj.base.BuildConfig
import me.shetj.base.constant.Constant.Companion.KEY_IS_OUTPUT_HTTP
import me.shetj.base.constant.Constant.Companion.KEY_IS_OUTPUT_LOG
import me.shetj.base.tools.file.FileUtils
import me.shetj.base.tools.file.SDCardUtils
import me.shetj.base.tools.file.SPUtils
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.concurrent.Executors

/**
 * debug 功能扩展 必须开启debug的情况下
 * 1. 对一些日志进行特殊的保留
 * 2. 对http请求日志进行文件输出
 */
class DebugFunc private constructor() {

    val saveLogFile = SDCardUtils.cache + File.separatorChar + "BaseDebug.text"
    val saveHttpFile = SDCardUtils.cache + File.separatorChar + "HttpDebug.text"

    private val settingDialog: SettingBottomSheetDialog by lazy { SettingBottomSheetDialog(mContext!!) }
    private var mContext: Context? = null

    private var isOutputHttp = mContext?.let { SPUtils.get(it, KEY_IS_OUTPUT_HTTP, BuildConfig.DEBUG) as Boolean }
            ?: false
    private var isOutputLog = mContext?.let { SPUtils.get(it, KEY_IS_OUTPUT_LOG, BuildConfig.DEBUG) as Boolean }
            ?: false

    //用来专门记录文件的IO线程
    private val mDiskIO = Executors.newSingleThreadExecutor() { r -> Thread(r) }

    companion object {
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

    fun showSettingDialog() {
        checkNotNull(mContext, { "need initContext() first" })
        settingDialog.showBottomSheet()
    }

    fun hideSettingDialog() {
        checkNotNull(mContext, { "need initContext() first" })
        settingDialog.dismissBottomSheet()
    }

    fun setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(object : Consumer<Throwable?> {
            @Throws(Exception::class)
            override fun accept(throwable: Throwable?) {
                if (throwable == null) return
                Timber.e("setRxJavaErrorHandler begin=================================")
                throwable.printStackTrace()
                outputToFile(throwable.message)
                Timber.e("setRxJavaErrorHandler end=================================")
            }
        })
    }

    fun outputToFile(info: String?, path: String? = saveLogFile) {
        mDiskIO.execute {
            if (info.isNullOrEmpty()) return@execute
            if (path.isNullOrEmpty()) return@execute
            try {
                val fw = BufferedWriter(FileWriter(path, true))
                fw.write(info)
                fw.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearAll() {
        delFile(saveHttpFile)
        delFile(saveLogFile)
    }

    fun delFile(path: String? = saveLogFile) {
        FileUtils.deleteFile(path)
    }

}