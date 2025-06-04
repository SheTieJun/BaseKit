package me.shetj.base.netcoroutine

import androidx.annotation.Keep
import me.shetj.base.ktx.md5
import me.shetj.base.netcoroutine.cache.CacheMode
import me.shetj.base.netcoroutine.cache.KCCache.Companion.CACHE_NEVER_EXPIRE

/**
 * 请求选项选项
 */
@Keep
data class RequestOption(
    /**
     * 缓存的key,if null ,cache no work
     */
    var cacheKey: String? = null,

    /**
     * 缓存的时间 单位:秒
     */
    var cacheTime: Long = CACHE_NEVER_EXPIRE,

    /**
     * 不使用自定义缓存 cacheKey,cacheTime 无效，默认缓存规则，走OKhttp的Cache缓存
     */
    var cacheMode: CacheMode = CacheMode.DEFAULT,

    /**
     * timeout <= 0 表示不处理; 单位：毫秒；自定义超时处理,走的是协程的超时处理
     */
    var timeout: Long = -1L,

    /**
     * repeatNum <= 0 表不处理； 重试请求次数
     */
    var repeatNum: Int = -1
) {
    companion object {
        val DEFAULT = RequestOption().apply {
            cacheKey = "default"
            cacheTime = 36_000
            cacheMode = CacheMode.FIRST_NET
            repeatNum = 3
            timeout = 10_000L
        }
    }
}

/**
 * Get def req option
 *
 * `key.getDefReqOption()`
 * @return [RequestOption]
 */
@Keep
fun String.getDefReqOption(): RequestOption {
    return buildRequest {
        cacheKey = this@getDefReqOption.md5
        cacheTime = 36_00
        cacheMode = CacheMode.FIRST_NET
        repeatNum = 3
        timeout = 10_000L
    }
}

@Keep
fun buildRequest(requestOption: (RequestOption.() -> Unit)? = null): RequestOption {
    return RequestOption().also { option ->
        requestOption?.let { option.apply(requestOption) }
        checkNotNull(option.cacheKey) { "cacheKey must not be null" }
    }
}
