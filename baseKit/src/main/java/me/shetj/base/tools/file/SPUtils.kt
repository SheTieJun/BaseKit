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
package me.shetj.base.tools.file

import android.content.Context
import androidx.annotation.Keep

@Keep
class SPUtils {
    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {

        /**
         * 保存在手机里面的文件名
         */
        private const val FILE_NAME = "sharep_data"

        /**
         * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
         *
         * @param context
         * @param key
         * @param object
         */
        @JvmStatic
        fun put(context: Context, key: String, `object`: Any) {

            val sp = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            val editor = sp.edit()
            when (`object`) {
                is String -> editor.putString(key, `object`)
                is Int -> editor.putInt(key, `object`)
                is Boolean -> editor.putBoolean(key, `object`)
                is Float -> editor.putFloat(key, `object`)
                is Long -> editor.putLong(key, `object`)
                else -> editor.putString(key, `object`.toString())
            }
            editor.apply()
        }

        /**
         * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
         *
         * @param context
         * @param key
         * @param defaultObject
         * @return
         */
        @JvmStatic
        fun get(context: Context, key: String, defaultObject: Any): Any? {
            val sp = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            return when (defaultObject) {
                is String -> sp.getString(key, defaultObject)
                is Int -> sp.getInt(key, defaultObject)
                is Boolean -> sp.getBoolean(key, defaultObject)
                is Float -> sp.getFloat(key, defaultObject)
                is Long -> sp.getLong(key, defaultObject)
                else -> null
            }
        }

        /**
         * 移除某个key值已经对应的值
         *
         * @param context
         * @param key
         */
        @JvmStatic
        fun remove(context: Context, key: String) {
            val sp = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            val editor = sp.edit()
            editor.remove(key)
            editor.apply()
        }

        /**
         * 清除所有数据
         *
         * @param context
         */
        @JvmStatic
        fun clear(context: Context) {
            val sp = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            val editor = sp.edit()
            editor.clear()
            editor.apply()
        }

        /**
         * 查询某个key是否已经存在
         *
         * @param context
         * @param key
         * @return
         */
        @JvmStatic
        fun contains(context: Context, key: String): Boolean {
            val sp = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            return sp.contains(key)
        }

        /**
         * 返回所有的键值对
         *
         * @param context
         * @return
         */
        @JvmStatic
        fun getAll(context: Context): Map<String, *> {
            val sp = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            return sp.all
        }
    }
}
