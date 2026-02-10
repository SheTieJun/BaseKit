package me.shetj.base.tools.debug

import android.os.SystemClock
import me.shetj.base.ktx.logE
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import kotlin.system.exitProcess

/**
 * 发现错误并输出
 * 配合 [LogManager] 使用，实现 Crash 日志的自动保存
 */
class BaseUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        val errorMsg = "Thread = ${t.name} : Throwable = ${e.message}".trimIndent()
        errorMsg.logE("error")

        val stackTraceInfo = getStackTraceInfo(e)
        stackTraceInfo.logE("error")

        // 使用 LogManager 保存
        LogManager.log(LogLevel.ERROR, "Crash", errorMsg)
        LogManager.log(LogLevel.ERROR, "Crash", stackTraceInfo)

        // 强制同步写入，确保 Crash 日志不丢失
        LogManager.flushSync()

        SystemClock.sleep(1000)
        exitProcess(0)
    }

    /**
     * 获取错误的信息
     *
     * @param throwable
     * @return
     */
    private fun getStackTraceInfo(throwable: Throwable): String {
        var pw: PrintWriter? = null
        val writer: Writer = StringWriter()
        try {
            pw = PrintWriter(writer)
            throwable.printStackTrace(pw)
        } catch (e: Exception) {
            return ""
        } finally {
            pw?.close()
        }
        return writer.toString()
    }
}
