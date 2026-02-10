package me.shetj.base.tools.debug

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogLevel {
    INFO,
    ERROR,
    HTTP,
    BEHAVIOR // 用户行为
}

data class LogRecord(
    val level: LogLevel,
    val tag: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    }

    override fun toString(): String {
        return "${dateFormat.format(Date(timestamp))} [${level.name}] $tag: $message"
    }
}
