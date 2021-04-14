package me.shetj.base.network.model

import java.io.Serializable

class CacheResult<T> : Serializable {
    var isCache = false
    var data: T? = null

    constructor() {}
    constructor(isFromCache: Boolean) {
        isCache = isFromCache
    }

    constructor(isFromCache: Boolean, data: T) {
        isCache = isFromCache
        this.data = data
    }

    override fun toString(): String {
        return "CacheResult{" +
                "isCache=" + isCache +
                ", data=" + data +
                '}'
    }
}