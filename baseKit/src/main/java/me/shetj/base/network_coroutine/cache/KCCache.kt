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
import me.shetj.base.network_coroutine.cache.KCCache.Companion.CACHE_NEVER_EXPIRE
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
     * @return 是否缓存成功
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

/**
 * 缓存数据
 * @param key 关键字，唯一
 * @param value 值
 */
suspend fun saveCache(key: String?, value: String?) {
    if (!key.isNullOrBlank()) {
        withContext(Dispatchers.IO) {
            HttpKit.getKCCache().save(key, value)
        }
    }
}

/**
 * 获取缓存数据
 * @param key 关键字，唯一
 * @param existTime 过期时间，
 *  * 如果 existTime =  [CACHE_NEVER_EXPIRE] :表示永不过期
 *  * 如果缓存时间过期，缓存数据会被删除，返回 null
 * @return String 如果缓存时间内，返回具体的缓存数据
 */
suspend fun loadCache(key: String?, existTime: Long = CACHE_NEVER_EXPIRE): String? {
    return key?.let {
        withContext(Dispatchers.IO) {
            HttpKit.getKCCache().load(it, existTime)
        }
    }
}

/**
 * 是否缓存
 */
suspend fun hasCached(key: String?): Boolean {
    return key?.let {
        withContext(Dispatchers.IO) {
            HttpKit.getKCCache().containsKey(it)
        }
    } ?: false
}
