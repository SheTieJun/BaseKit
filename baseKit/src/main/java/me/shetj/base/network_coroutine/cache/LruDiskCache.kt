/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.network_coroutine.cache

import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import me.shetj.base.ktx.md5
import me.shetj.base.tools.file.CloseUtils
import okhttp3.internal.cache.DiskLruCache
import okhttp3.internal.io.FileSystem
import okio.Okio
import timber.log.Timber

/**
 * Source对应InputStream， Sink对应OutputStream
 */
class LruDiskCache constructor(diskDir: File?, appVersion: Int, diskMaxSize: Long) :
    IResultCache() {

    private val charset: Charset = Charset.forName("UTF-8")

    private var mDiskLruCache: DiskLruCache? = null

    override fun doLoad(key: String): String? {
        if (mDiskLruCache == null) {
            return null
        }
        try {
            val edit = mDiskLruCache?.edit(key.md5) ?: return null
            val source = Okio.buffer(edit.newSource(0))
            var value: String? = null
            if (source != null) {
                try {
                    value = source.readString(charset)
                } catch (e: JsonIOException) {
                    Timber.e(e.message)
                } catch (e: IOException) {
                    Timber.e(e.message)
                } catch (e: ConcurrentModificationException) {
                    Timber.e(e.message)
                } catch (e: JsonSyntaxException) {
                    Timber.e(e.message)
                } catch (e: Exception) {
                    Timber.e(e.message)
                } finally {
                    CloseUtils.closeIO(source)
                }
                edit.commit()
                return value
            }
            edit.abort()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun doSave(key: String, value: String): Boolean {
        if (mDiskLruCache == null) {
            return false
        }
        try {
            val edit = mDiskLruCache?.edit(key.md5) ?: return false
            val sink = Okio.buffer(edit.newSink(0))
            try {
                sink.writeString(value, charset)
                sink.flush()
                edit.commit()
                return true
            } catch (e: JsonIOException) {
                Timber.e(e.message)
            } catch (e: JsonSyntaxException) {
                Timber.e(e.message)
            } catch (e: ConcurrentModificationException) {
                Timber.e(e.message)
            } catch (e: IOException) {
                Timber.e(e.message)
            } catch (e: Exception) {
                Timber.e(e.message)
            } finally {
                CloseUtils.closeIO(sink)
            }
            edit.abort()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun doContainsKey(key: String): Boolean {
        if (mDiskLruCache == null) {
            return false
        }
        try {
            return mDiskLruCache!!.get(key.md5) != null
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun doRemove(key: String): Boolean {
        if (mDiskLruCache == null) {
            return false
        }
        try {
            return mDiskLruCache!!.remove(key.md5)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun doClear(): Boolean {
        var statu = false
        try {
            mDiskLruCache!!.delete()
            statu = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return statu
    }

    override fun isExpiry(key: String, existTime: Long): Boolean {
        if (mDiskLruCache == null) {
            return false
        }
        if (existTime > -1) { // -1表示永久性存储 不用进行过期校验
            // 为什么这么写，请了解DiskLruCache，看它的源码
            val file = File(mDiskLruCache!!.directory, "${key.md5}.0")
            if (isCacheDataFailure(file, existTime)) { // 没有获取到缓存,或者缓存已经过期!
                return true
            }
        }
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
            mDiskLruCache =
                DiskLruCache.create(FileSystem.SYSTEM, diskDir, appVersion, 1, diskMaxSize)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}