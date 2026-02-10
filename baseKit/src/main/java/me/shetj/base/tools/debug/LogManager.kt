package me.shetj.base.tools.debug

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

object LogManager {

    private val config = LogConfig()
    private val logChannel = Channel<LogRecord>(Channel.UNLIMITED)
    private val buffer = ConcurrentLinkedQueue<LogRecord>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val isRunning = AtomicBoolean(false)

    private val fileDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun init(init: LogConfig.() -> Unit) {
        config.init()
        start()
    }

    fun getConfig() = config

    fun log(level: LogLevel, tag: String, message: String) {
        if (!config.isEnable) return

        val record = LogRecord(level, tag, message)

        // 1. Logcat Output
        if (config.isPrintToConsole) {
            when (level) {
                LogLevel.ERROR -> Timber.e(tag, message)
                else -> Timber.i(tag, message)
            }
        }

        // 2. File Output
        if (config.isSaveToFile) {
            logChannel.trySend(record)
        }
    }

    private fun start() {
        if (isRunning.getAndSet(true)) return

        // 消费者协程：负责从 Channel 读取到 Buffer
        scope.launch {
            while (isActive) {
                val record = logChannel.receive() // 挂起直到有数据
                buffer.add(record)
                if (buffer.size >= config.bufferSize) {
                    flushBuffer()
                }
            }
        }

        // 定时刷新协程：负责定时 Flush
        scope.launch {
            while (isActive) {
                delay(config.flushInterval)
                flushBuffer()
            }
        }
    }

    /**
     * 同步刷新，通常在 Crash 时调用
     * 会尝试将 Channel 中剩余的数据全部取出并写入文件
     */
    fun flushSync() {
        // 把 channel 里的东西都取出来放入 buffer
        var next = logChannel.tryReceive().getOrNull()
        while (next != null) {
            buffer.add(next)
            next = logChannel.tryReceive().getOrNull()
        }
        flushBuffer()
    }

    fun getLogFiles(): List<File> {
        val dir = File(config.logDir)
        if (!dir.exists() || !dir.isDirectory) return emptyList()
        return dir.listFiles()?.filter { it.isFile && it.name.startsWith("Log_") }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    fun readLogFile(file: File): List<String> {
        return try {
            if (!file.exists()) return emptyList()
            file.readLines()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    @Synchronized
    private fun flushBuffer() {
        if (buffer.isEmpty()) return

        val recordsToWrite = mutableListOf<LogRecord>()
        // 也就是一次最多写 bufferSize * N 条，或者全部写完
        // 这里选择全部写完
        while (!buffer.isEmpty()) {
            buffer.poll()?.let { recordsToWrite.add(it) }
        }

        if (recordsToWrite.isEmpty()) return

        try {
            val dateStr = fileDateFormat.format(Date())
            val fileName = "Log_$dateStr.txt"
            val file = File(config.logDir, fileName)

            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }

            FileWriter(file, true).use { writer ->
                recordsToWrite.forEach { record ->
                    writer.write(record.toString() + "\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果写入失败，可能需要根据策略决定是否丢弃，这里暂时丢弃
            Log.e("LogManager", "Failed to write logs to file: ${e.message}")
        }
    }
}
