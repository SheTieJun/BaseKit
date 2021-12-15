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
