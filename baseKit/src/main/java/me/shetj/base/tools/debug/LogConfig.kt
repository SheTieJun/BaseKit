package me.shetj.base.tools.debug

import me.shetj.base.tools.file.EnvironmentStorage
import java.io.File

data class LogConfig(
    /**
     * 是否启用日志系统
     */
    var isEnable: Boolean = true,
    
    /**
     * 是否输出到文件
     */
    var isSaveToFile: Boolean = true,
    
    /**
     * 是否在控制台打印 (Logcat)
     */
    var isPrintToConsole: Boolean = true,
    
    /**
     * 日志文件保存目录
     */
    var logDir: String = EnvironmentStorage.filesDir + File.separator + "BaseLog",
    
    /**
     * 缓冲区大小 (条数)，达到此数量触发写入
     */
    var bufferSize: Int = 50,
    
    /**
     * 自动刷新间隔 (毫秒)，达到此时间间隔触发写入
     */
    var flushInterval: Long = 5000L,
    
    /**
     * 单个日志文件最大大小 (字节)，默认 10MB
     */
    var maxFileSize: Long = 20 * 1024 * 1024
)
