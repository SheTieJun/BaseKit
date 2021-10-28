package me.shetj.base.network_coroutine

import me.shetj.base.network_coroutine.cache.CacheMode
import me.shetj.base.network_coroutine.cache.KCCache.Companion.CACHE_NEVER_EXPIRE

/**
 * 请求选项选项
 */
class RequestOption {
    var cacheKey: String? = null //缓存的key
    var cacheTime: Long = CACHE_NEVER_EXPIRE //缓存的时间 单位:秒
    var cacheMode: CacheMode = CacheMode.DEFAULT //不使用自定义缓存 cacheKey,cacheTime 无效，默认缓存规则，走OKhttp的Cache缓存
    var timeout :Long = -1L // <= 0 表示不处理; 单位：毫秒；自定义超时处理,走的是协程的超时处理
    var repeatNum :Int = -1// <= 0 表不处理； 重试次数
}