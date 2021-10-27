package me.shetj.base.network.exception

class CacheException(val errCode: Int, override val message: String?) : RuntimeException(message)