package me.shetj.base.netcoroutine.cache

import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import me.shetj.base.ktx.md5
import me.shetj.base.tools.file.CloseUtils
import okio.FileSystem
import okio.buffer
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

/**
 * Source对应InputStream， Sink对应OutputStream
 */
class LruDiskCache constructor(diskDir: File?, appVersion: Int, diskMaxSize: Long) :
    IResultCache {

    private val charset: Charset = Charset.forName("UTF-8")


    override fun doLoad(key: String): String? {

        return null
    }

    override fun doSave(key: String, value: String): Boolean {

        return false
    }

    override fun doContainsKey(key: String): Boolean {

        return false
    }

    override fun doRemove(key: String): Boolean {

        return false
    }

    override fun doClear(): Boolean {

        return false
    }

    override fun isExpiry(key: String, existTime: Long): Boolean {

        return false
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
