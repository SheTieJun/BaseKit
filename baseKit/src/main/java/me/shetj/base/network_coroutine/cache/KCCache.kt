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

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.shetj.base.network_coroutine.HttpKit
import me.shetj.base.network_coroutine.RequestOption
import org.koin.java.KoinJavaComponent

/**
 * @author stj
 * @Date 2021/10/21-15:55
 * @Email 375105540@qq.com
 */

class KCCache {
    private val mLock: ReadWriteLock = ReentrantReadWriteLock()

    private val diskCache: LruDiskCache = KoinJavaComponent.get(LruDiskCache::class.java)

    companion object {
        const val CACHE_NEVER_EXPIRE: Long = -1 // 永久不过期
    }

    /**
     * 读取缓存
     *
     * @param key       缓存key
     * @param existTime 缓存时间
     */
    fun load(key: String?, existTime: Long): String? {
        // 1.先检查key
        requireNotNull(key) { "key == null" }

        // 2.判断key是否存在,key不存在去读缓存没意义
        if (!containsKey(key)) {
            return null
        }

        // 3.判断是否过期，过期自动清理
        if (diskCache.isExpiry(key, existTime)) {
            remove(key)
            return null
        }

        // 4.开始真正的读取缓存
        mLock.readLock().lock()
        return try {
            // 读取缓存
            diskCache.doLoad(key)
        } finally {
            mLock.readLock().unlock()
        }
    }

    /**
     * 保存缓存
     *
     * @param key   缓存key
     * @param value 缓存内容
     * @return
     */
    fun save(key: String?, value: String?): Boolean {
        // 1.先检查key
        requireNotNull(key) { "key == null" }

        // 2.如果要保存的值为空,则删除
        if (value == null) {
            return remove(key)
        }

        // 3.写入缓存
        val status: Boolean
        mLock.writeLock().lock()
        try {
            status = diskCache.doSave(key, value)
        } finally {
            mLock.writeLock().unlock()
        }
        return status
    }

    /**
     * 删除缓存
     */
    fun remove(key: String): Boolean {
        mLock.writeLock().lock()
        return try {
            diskCache.doRemove(key)
        } finally {
            mLock.writeLock().unlock()
        }
    }

    /**
     * 清空缓存
     */
    fun clear(): Boolean {
        mLock.writeLock().lock()
        return try {
            diskCache.doClear()
        } finally {
            mLock.writeLock().unlock()
        }
    }

    /**
     * 是否包含 加final 是让子类不能被重写，只能使用doContainsKey<br></br>
     * 这里加了锁处理，操作安全。<br></br>
     *
     * @param key 缓存key
     * @return 是否有缓存
     */
    fun containsKey(key: String): Boolean {
        mLock.readLock().lock()
        return try {
            diskCache.doContainsKey(key)
        } finally {
            mLock.readLock().unlock()
        }
    }
}

suspend fun saveCache(block: RequestOption.() -> Unit, data: String) {
    saveCache(RequestOption().also(block), data)
}

suspend fun saveCache(requestOption: RequestOption?, data: String) {
    if (!requestOption?.cacheKey.isNullOrBlank()) {
        withContext(Dispatchers.IO) {
            HttpKit.getKCCache().save(requestOption?.cacheKey, data)
        }
    }
}
