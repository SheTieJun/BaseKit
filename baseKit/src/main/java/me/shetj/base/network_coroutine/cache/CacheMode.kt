package me.shetj.base.network_coroutine.cache

enum class CacheMode {

    /**
     * 不使用自定义缓存,完全按照HTTP协议的默认缓存规则，走OKhttp的Cache缓存
     */
    DEFAULT,

    /**
     * 先请求网络，请求网络失败后再加载缓存
     */
    FIRST_NET,

    /**
     * 先加载缓存，缓存没有再去请求网络
     */
    FIRST_CACHE,

    /**
     * 仅加载网络，但数据依然会被缓存
     */
    ONLY_NET,

    /**
     * 只读取缓存
     */
    ONLY_CACHE,

    /**
     * 先使用缓存，不管是否存在，仍然请求网络，会先把缓存回调给你，
     * 等网络请求回来发现数据是一样的就不会再返回，否则再返回
     * （这样做的目的是防止数据是一样的你也需要刷新界面）
     *
     * 感觉有点没必要，可以后续处理，不需要再缓存进行处理
     */
    CACHE_NET_DISTINCT;
}