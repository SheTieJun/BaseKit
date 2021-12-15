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

import com.google.gson.internal.`$Gson$Types`
import me.shetj.base.network.kt.ClassUtils
import me.shetj.base.network.model.ApiResult
import me.shetj.base.network.model.CacheResult
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

//使用代理的方式，把R 默认转换成 ApiResult<R> 来接收数据
abstract class NetCallBackProxy<T : ApiResult<R>, R>(private var mCallBack: NetCallBack<R>) :
    IType<T> {
    val callBack: NetCallBack<R>
        get() = mCallBack

    override fun getType(): Type { //CallBack代理方式，获取需要解析的Type

        val rawCallType: Type = mCallBack.getRawType() //如果用户的信息是返回List需单独处理
        val typeArguments: Type =
            if (MutableList::class.java.isAssignableFrom(ClassUtils.getClass(rawCallType, 0))
                || MutableMap::class.java.isAssignableFrom(ClassUtils.getClass(rawCallType, 0))
            ) {
                mCallBack.getType()
            } else if (CacheResult::class.java.isAssignableFrom(
                    ClassUtils.getClass(
                        rawCallType,
                        0
                    )
                )
            ) {
                val type = mCallBack.getType()
                ClassUtils.getParameterizedType(type, 0)
            } else {
                val type = mCallBack.getType()
                ClassUtils.getClass(type, 0)
            }
        var rawType: Type = ClassUtils.findNeedType(javaClass)
        if (rawType is ParameterizedType) {
            rawType = rawType.rawType
        }
        // $Gson$Types.newParameterizedTypeWithOwner接收三个参数
        // ownerType:所处类的`Type，如果不是内部类或嵌套类则均为null。比如Response不是任何类的子类，因此此处为null
        // rawType:真实类型，在此处就是ApiResult<T>的类型
        // typeArguments:类型参数，此处就是Response<T>中泛型T的真实类型的Type，比如已知是Response<String>，
        // 那么typeArguments就是String.class或String.class.getGenericSuperclass()，其实是一样的
        // 转化成果rawType<typeArguments>
        // Type type = com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, clazz); = ArrayList<clazz>
        return `$Gson$Types`.newParameterizedTypeWithOwner(null, rawType, typeArguments)
    }

}