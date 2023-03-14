package me.shetj.base.network_coroutine

import me.shetj.base.ktx.md5
import me.shetj.base.network_coroutine.cache.CacheMode
import me.shetj.base.network_coroutine.cache.KCCache.Companion.CACHE_NEVER_EXPIRE

/**
 * 请求选项选项
 */
class RequestOption {
    /**
     * 缓存的key,if null ,cache no work
     */
    var cacheKey: String? = null

    /**
     * 缓存的时间 单位:秒
     */
    var cacheTime: Long = CACHE_NEVER_EXPIRE

    /**
     * 不使用自定义缓存 cacheKey,cacheTime 无效，默认缓存规则，走OKhttp的Cache缓存
     */
    var cacheMode: CacheMode = CacheMode.DEFAULT

    /**
     * timeout <= 0 表示不处理; 单位：毫秒；自定义超时处理,走的是协程的超时处理
     */
    var timeout: Long = -1L

    /**
     * repeatNum <= 0 表不处理； 重试请求次数
     */
    var repeatNum: Int = -1
}


/**
 * Get def req option
 *
 * `key.getDefReqOption()`
 * @return [RequestOption]
 */
fun String.getDefReqOption(): RequestOption {
    return RequestOption().also {
        it.cacheKey = this.md5
        it.cacheTime = 36_00
        it.cacheMode = CacheMode.FIRST_NET
        it.repeatNum = 3
        it.timeout = 10_000L
    }
}


fun buildRequest(requestOption: (RequestOption.() -> Unit)? = null): RequestOption {
    return RequestOption().also { option ->
        requestOption?.let { option.apply(requestOption) }
        checkNotNull(option.cacheKey) { "cacheKey must not be null" }
    }
}
