package me.shetj.base.netcoroutine.cache

import androidx.core.util.lruCache
import me.shetj.base.ktx.md5
import me.shetj.base.tools.file.CloseUtils
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.buffer
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicLong

/**
 * Source对应InputStream， Sink对应OutputStream
 * 基于文件 LastModified 的 LRU 缓存实现
 */
class LruDiskCache constructor(private val diskDir: File?, private val diskMaxSize: Long) :
    IResultCache {

    private val charset: Charset = Charset.forName("UTF-8")
    private val cacheSize = AtomicLong(0)

    init {
        // 异步初始化计算当前缓存大小
        Thread {
            calculateCacheSize()
        }.start()
    }

    private fun calculateCacheSize() {
        var size = 0L
        diskDir?.listFiles()?.forEach { file ->
            if (file.isFile) {
                size += file.length()
            }
        }
        cacheSize.set(size)
    }

    override fun doLoad(key: String): String? {
        val file = getCacheFile(key)
        if (!file.exists()) {
            return null
        }
        // 刷新最后修改时间，实现 LRU 策略
        // 注意：这会产生一次文件属性写入，可能会有轻微性能影响，但在缓存场景下通常可接受
        file.setLastModified(System.currentTimeMillis())
        
        return try {
            val source = FileSystem.SYSTEM.source(file.toOkioPath()).buffer()
            val result = source.readString(charset)
            CloseUtils.closeIO(source)
            result
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun doSave(key: String, value: String): Boolean {
        return try {
            val file = getCacheFile(key)
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            
            val oldLength = if (file.exists()) file.length() else 0L
            
            if (!file.exists()) {
                file.createNewFile()
            }
            val sink = FileSystem.SYSTEM.sink(file.toOkioPath()).buffer()
            sink.writeString(value, charset)
            CloseUtils.closeIO(sink)
            
            val newLength = file.length()
            cacheSize.addAndGet(newLength - oldLength)
            
            trimToSize()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun trimToSize() {
        if (diskMaxSize <= 0 || diskDir == null) return

        var currentSize = cacheSize.get()
        if (currentSize > diskMaxSize) {
            val files = diskDir.listFiles()?.filter { it.isFile }?.sortedBy { it.lastModified() }
            
            files?.forEach { file ->
                if (currentSize > diskMaxSize) {
                    val fileSize = file.length()
                    if (file.delete()) {
                        currentSize = cacheSize.addAndGet(-fileSize)
                    }
                } else {
                    return
                }
            }
        }
    }

    override fun doContainsKey(key: String): Boolean {
        return getCacheFile(key).exists()
    }

    override fun doRemove(key: String): Boolean {
        val file = getCacheFile(key)
        if (file.exists()) {
            val length = file.length()
            if (file.delete()) {
                cacheSize.addAndGet(-length)
                return true
            }
        }
        return false
    }

    override fun doClear(): Boolean {
        val file = diskDir
        if (file != null && file.exists()) {
            cacheSize.set(0)
            return file.deleteRecursively()
        }
        return false
    }

    override fun isExpiry(key: String, existTime: Long): Boolean {
        if (existTime == -1L) return false // 永不过期
        val file = getCacheFile(key)
        return isCacheDataFailure(file, existTime)
    }

    private fun getCacheFile(key: String): File {
        return File(diskDir, key.md5 ?: key)
    }

    /**
     * 判断缓存是否已经失效
     */
    private fun isCacheDataFailure(dataFile: File, time: Long): Boolean {
        if (!dataFile.exists()) {
            return false
        }
        val existTime = System.currentTimeMillis() - dataFile.lastModified()
        return existTime > time * 1000
    }
}
