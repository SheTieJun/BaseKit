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