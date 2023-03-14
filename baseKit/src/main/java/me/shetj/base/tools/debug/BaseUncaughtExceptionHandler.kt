package me.shetj.base.tools.debug

import android.os.Process
import android.os.SystemClock
import android.text.TextUtils
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import me.shetj.base.ktx.logE
import me.shetj.base.tools.debug.DebugFunc.Companion.logFilePath
import me.shetj.base.tools.time.DateUtils
import timber.log.Timber

/**
 * 发现错误并输出
 */
class BaseUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        ("Thread = ${t.name} : Throwable = ${e.message}".trimIndent()).logE("error")
        val stackTraceInfo = getStackTraceInfo(e)
        stackTraceInfo.logE("error")
        saveThrowableMessage(stackTraceInfo)
        SystemClock.sleep(1000)
        Process.killProcess(Process.myPid())
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

    private fun saveThrowableMessage(errorMessage: String) {
        if (TextUtils.isEmpty(errorMessage)) {
            return
        }
        val file = File(logFilePath)
        if (!file.exists()) {
            val mkdirs = file.mkdirs()
            if (mkdirs) {
                writeStringToFile(errorMessage, file)
            }
        } else {
            writeStringToFile(errorMessage, file)
        }
    }

    private fun writeStringToFile(errorMessage: String, file: File) {
        Thread {
            var outputStream: FileOutputStream? = null
            try {
                val inputStream = ByteArrayInputStream(errorMessage.toByteArray())
                outputStream = FileOutputStream(File(file, DateUtils.timeString + ".txt"))
                var len: Int
                val bytes = ByteArray(1024)
                while (inputStream.read(bytes).also { len = it } != -1) {
                    outputStream.write(bytes, 0, len)
                }
                outputStream.flush()
                ("写入本地文件成功：${file.absolutePath}").logE("error")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }
}
