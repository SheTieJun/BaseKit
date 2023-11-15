package me.shetj.base.netcoroutine.cache

/**
 * @author stj
 * @Date 2021/10/21-15:12
 * @Email 375105540@qq.com
 *  结果缓存
 */
interface IResultCache {

    /**
     *是否包含
     */
    fun doContainsKey(key: String): Boolean

    /**
     * 是否过期
     */
    fun isExpiry(key: String, existTime: Long): Boolean

    /**
     * 读取缓存
     */
    fun doLoad(key: String): String?

    /**
     * 保存
     */
    fun doSave(key: String, value: String): Boolean

    /**
     * 删除缓存
     */
    fun doRemove(key: String): Boolean

    /**
     * 清空缓存
     */
    fun doClear(): Boolean
}
