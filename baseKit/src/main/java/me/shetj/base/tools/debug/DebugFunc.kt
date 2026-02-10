package me.shetj.base.tools.debug

import android.content.Context
import me.shetj.base.BuildConfig
import me.shetj.base.constant.Constant.Companion.KEY_IS_OUTPUT_HTTP
import me.shetj.base.tools.file.FileUtils
import me.shetj.base.tools.file.SPUtils

/**
 * debug 功能扩展
 * 1. 对一些日志进行特殊的保留
 * 2. 对http请求日志进行文件输出
 * 3. 现已全面升级为 [LogManager] 的门面，建议直接使用 [LogManager]
 */
class DebugFunc private constructor() {

    private var mContext: Context? = null
    private var _isOutputHttp: Boolean = false

    val isOutputHttp: Boolean
        get() = _isOutputHttp

    companion object {
        @Volatile
        private var mDebugFunc: DebugFunc? = null

        @JvmStatic
        fun getInstance(): DebugFunc {
            return mDebugFunc ?: DebugFunc().also {
                mDebugFunc = it
            }
        }
    }

    fun initContext(context: Context) {
        mContext = context.applicationContext
        _isOutputHttp = SPUtils.get(context, KEY_IS_OUTPUT_HTTP, BuildConfig.DEBUG) as Boolean

        // 初始化日志管理器
        LogManager.init {
            isEnable = true
            isPrintToConsole = BuildConfig.DEBUG
            // 可以在这里根据需要配置 logDir 等
        }
    }

    fun saveHttpToFile(info: String?) {
        if (_isOutputHttp && !info.isNullOrEmpty()) {
            LogManager.log(LogLevel.HTTP, "HTTP", info)
        }
    }

    fun getHttpSetting(): Boolean {
        return _isOutputHttp
    }

    fun setIsOutputHttp(isOutput: Boolean) {
        _isOutputHttp = isOutput
        mContext?.let {
            SPUtils.put(it, KEY_IS_OUTPUT_HTTP, isOutput)
        }
    }

    fun saveLogToFile(info: String?) {
        if (!info.isNullOrEmpty()) {
            LogManager.log(LogLevel.INFO, "Log", info)
        }
    }

    /**
     * 记录用户行为日志
     */
    fun logBehavior(tag: String, msg: String) {
        LogManager.log(LogLevel.BEHAVIOR, tag, msg)
    }

    fun clearAll() {
        val dir = LogManager.getConfig().logDir
        FileUtils.deleteDir(dir)
    }

    /**
     * 打开调试设置界面
     */
    fun openDebugSettings(context: Context) {
        DebugSettingsActivity.start(context)
    }

    /**
     * 打开日志查看界面
     */
    fun openLogViewer(context: Context) {
        LogViewerActivity.start(context)
    }
}
