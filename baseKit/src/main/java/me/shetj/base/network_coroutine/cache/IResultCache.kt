package me.shetj.base.network_coroutine.cache

/**
 * @author stj
 * @Date 2021/10/21-15:12
 * @Email 375105540@qq.com
 *  结果缓存
 */
abstract class IResultCache {


    /**
     *是否包含
     */
    abstract fun doContainsKey(key: String): Boolean

    /**
     * 是否过期
     */
    abstract fun isExpiry(key: String, existTime: Long): Boolean

    /**
     * 读取缓存
     */
    abstract fun doLoad(key: String): String?

    /**
     * 保存
     */
    abstract fun doSave(key: String, value: String): Boolean

    /**
     * 删除缓存
     */
    abstract fun doRemove(key: String): Boolean

    /**
     * 清空缓存
     */
    abstract fun doClear(): Boolean

}