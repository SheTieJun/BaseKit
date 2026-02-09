package me.shetj.base.netcoroutine.cache

import me.shetj.base.ktx.md5
import me.shetj.base.tools.file.CloseUtils
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.buffer
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

/**
 * Source对应InputStream， Sink对应OutputStream
 */
class LruDiskCache constructor(private val diskDir: File?, appVersion: Int, diskMaxSize: Long) :
    IResultCache {

    private val charset: Charset = Charset.forName("UTF-8")


    override fun doLoad(key: String): String? {
        val file = getCacheFile(key)
        if (!file.exists()) {
            return null
        }
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
            if (!file.exists()) {
                file.createNewFile()
            }
            val sink = FileSystem.SYSTEM.sink(file.toOkioPath()).buffer()
            sink.writeString(value, charset)
            CloseUtils.closeIO(sink)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    override fun doContainsKey(key: String): Boolean {
        return getCacheFile(key).exists()
    }

    override fun doRemove(key: String): Boolean {
        val file = getCacheFile(key)
        if (file.exists()) {
            return file.delete()
        }
        return false
    }

    override fun doClear(): Boolean {
        val file = diskDir
        if (file != null && file.exists()) {
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

    init {
        try {

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
