package shetj.me.base.websocket

import android.util.Log

/**
 * WebSocket日志记录器
 * 提供统一的日志管理和格式化
 */
object WebSocketLogger {
    
    private const val DEFAULT_TAG = "WebSocket"
    
    /** 日志级别 */
    enum class LogLevel(val value: Int) {
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR),
        NONE(Int.MAX_VALUE)
    }
    
    /** 当前日志级别 */
    @Volatile
    var logLevel: LogLevel = LogLevel.DEBUG
    
    /** 是否启用日志 */
    @Volatile
    var isEnabled: Boolean = true
    
    /** 自定义日志处理器 */
    @Volatile
    var customLogger: ((level: LogLevel, tag: String, message: String, throwable: Throwable?) -> Unit)? = null
    
    /**
     * 记录详细日志
     */
    fun v(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(LogLevel.VERBOSE, tag, message, throwable)
    }
    
    /**
     * 记录调试日志
     */
    fun d(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(LogLevel.DEBUG, tag, message, throwable)
    }
    
    /**
     * 记录信息日志
     */
    fun i(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(LogLevel.INFO, tag, message, throwable)
    }
    
    /**
     * 记录警告日志
     */
    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(LogLevel.WARN, tag, message, throwable)
    }
    
    /**
     * 记录错误日志
     */
    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(LogLevel.ERROR, tag, message, throwable)
    }
    
    /**
     * 记录连接事件
     */
    fun logConnection(url: String, success: Boolean, duration: Long = 0) {
        if (success) {
            i(message = "连接成功: $url (耗时: ${duration}ms)")
        } else {
            w(message = "连接失败: $url")
        }
    }
    
    /**
     * 记录消息发送
     */
    fun logMessageSent(message: String, success: Boolean) {
        val truncatedMessage = truncateMessage(message)
        if (success) {
            d(message = "消息发送成功: $truncatedMessage")
        } else {
            w(message = "消息发送失败: $truncatedMessage")
        }
    }
    
    /**
     * 记录消息接收
     */
    fun logMessageReceived(message: String) {
        val truncatedMessage = truncateMessage(message)
        d(message = "收到消息: $truncatedMessage")
    }
    
    /**
     * 记录状态变化
     */
    fun logStateChange(oldState: WebSocketState, newState: WebSocketState, reason: String? = null) {
        val reasonText = reason?.let { " (原因: $it)" } ?: ""
        i(message = "状态变化: $oldState -> $newState$reasonText")
    }
    
    /**
     * 记录重连事件
     */
    fun logReconnect(attempt: Int, delay: Long) {
        i(message = "第${attempt}次重连，延迟: ${delay}ms")
    }
    
    /**
     * 记录心跳事件
     */
    fun logHeartbeat(success: Boolean) {
        if (success) {
            d(message = "心跳发送成功")
        } else {
            w(message = "心跳发送失败")
        }
    }
    
    /**
     * 记录异常信息
     */
    fun logException(exception: Throwable, context: String = "") {
        val contextText = if (context.isNotEmpty()) "[$context] " else ""
        e(message = "${contextText}异常: ${exception.message}", throwable = exception)
    }
    
    /**
     * 统一日志记录方法
     */
    private fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        if (!isEnabled || level.value < logLevel.value) {
            return
        }
        
        val formattedMessage = formatMessage(message)
        
        // 使用自定义日志处理器
        customLogger?.let { logger ->
            try {
                logger(level, tag, formattedMessage, throwable)
                return
            } catch (e: Exception) {
                // 自定义日志处理器异常，回退到系统日志
            }
        }
        
        // 使用系统日志
        when (level) {
            LogLevel.VERBOSE -> {
                if (throwable != null) {
                    Log.v(tag, formattedMessage, throwable)
                } else {
                    Log.v(tag, formattedMessage)
                }
            }
            LogLevel.DEBUG -> {
                if (throwable != null) {
                    Log.d(tag, formattedMessage, throwable)
                } else {
                    Log.d(tag, formattedMessage)
                }
            }
            LogLevel.INFO -> {
                if (throwable != null) {
                    Log.i(tag, formattedMessage, throwable)
                } else {
                    Log.i(tag, formattedMessage)
                }
            }
            LogLevel.WARN -> {
                if (throwable != null) {
                    Log.w(tag, formattedMessage, throwable)
                } else {
                    Log.w(tag, formattedMessage)
                }
            }
            LogLevel.ERROR -> {
                if (throwable != null) {
                    Log.e(tag, formattedMessage, throwable)
                } else {
                    Log.e(tag, formattedMessage)
                }
            }
            LogLevel.NONE -> {
                // 不记录日志
            }
        }
    }
    
    /**
     * 格式化日志消息
     */
    private fun formatMessage(message: String): String {
        val timestamp = System.currentTimeMillis()
        val threadName = Thread.currentThread().name
        return "[$timestamp] [$threadName] $message"
    }
    
    /**
     * 截断过长的消息
     */
    private fun truncateMessage(message: String, maxLength: Int = 200): String {
        return if (message.length <= maxLength) {
            message
        } else {
            "${message.substring(0, maxLength)}...(${message.length}字符)"
        }
    }
    
    /**
     * 设置日志配置
     */
    fun configure(
        enabled: Boolean = true,
        level: LogLevel = LogLevel.DEBUG,
        customLogger: ((level: LogLevel, tag: String, message: String, throwable: Throwable?) -> Unit)? = null
    ) {
        isEnabled = enabled
        logLevel = level
        this.customLogger = customLogger
    }
    
    /**
     * 获取当前配置信息
     */
    fun getConfig(): String {
        return "WebSocketLogger配置: enabled=$isEnabled, level=$logLevel, customLogger=${customLogger != null}"
    }
}