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
package me.shetj.base.network.callBack

import android.content.Context
import java.lang.reflect.Type
import me.shetj.base.network.kt.ClassUtils

// T  = List<MusicBean>
abstract class NetCallBack<T>(val context: Context) : IType<T> {

    abstract fun onStart()
    abstract fun onComplete()
    abstract fun onError(e: Exception)
    abstract fun onSuccess(data: T)

    override fun getType(): Type {

        return ClassUtils.findNeedClass(javaClass)
        // 获取当前类型 T java.util.List<? extends shetj.me.base.bean.MusicBean>
    }

    open fun getRawType(): Type {
        // 获取需要解析的泛型 <T> raw类型 interface java.util.List
        return ClassUtils.findRawType(javaClass)
    }
}
