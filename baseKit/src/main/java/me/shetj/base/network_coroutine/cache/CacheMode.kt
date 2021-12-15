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