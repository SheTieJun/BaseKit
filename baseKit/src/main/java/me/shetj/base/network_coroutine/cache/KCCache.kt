package me.shetj.base.network_coroutine.cache

import me.shetj.base.network_coroutine.cache.KCCache.Companion.CACHE_NEVER_EXPIRE
import org.koin.java.KoinJavaComponent
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock


/**
 * 缓存选项
 * //TODO 缓存模式
 */
class CacheOption {

    var cacheKey: String? = null //缓存的key
    var cacheTime: Long = CACHE_NEVER_EXPIRE //缓存的时间 单位:秒
    var cacheMode:CacheMode = CacheMode.DEFAULT //不使用自定义缓存 cacheKey,cacheTime 无效，默认缓存规则，走OKhttp的Cache缓存
}

/**
 * @author stj
 * @Date 2021/10/21-15:55
 * @Email 375105540@qq.com
 */

class KCCache {
    private val mLock: ReadWriteLock = ReentrantReadWriteLock()

    private val diskCache: LruDiskCache = KoinJavaComponent.get(LruDiskCache::class.java)

    companion object {
        const val CACHE_NEVER_EXPIRE: Long = -1 //永久不过期
    }

    /**
     * 读取缓存
     *
     * @param key       缓存key
     * @param existTime 缓存时间
     */
    fun load(key: String?, existTime: Long): String? {
        //1.先检查key
        requireNotNull(key, { "key == null" })

        //2.判断key是否存在,key不存在去读缓存没意义
        if (!containsKey(key)) {
            return null
        }

        //3.判断是否过期，过期自动清理
        if (diskCache.isExpiry(key, existTime)) {
            remove(key)
            return null
        }

        //4.开始真正的读取缓存
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
        //1.先检查key
        requireNotNull(key, { "key == null" })

        //2.如果要保存的值为空,则删除
        if (value == null) {
            return remove(key)
        }

        //3.写入缓存
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
