package me.shetj.base.tools.json

import android.text.TextUtils
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.util.SparseLongArray
import androidx.annotation.Keep
import me.shetj.base.tools.app.ArmsUtils
import java.lang.reflect.Array

@Keep
class EmptyUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 判断对象是否为空
         *
         * @param obj 对象
         * @return `true`: 为空<br></br>`false`: 不为空
         */
        @JvmStatic
        fun isEmpty(obj: Any?): Boolean {
            if (obj == null) {
                return true
            }
            if (obj is String && obj.toString().isEmpty()) {
                return true
            }
            if (obj.javaClass.isArray && Array.getLength(obj) == 0) {
                return true
            }
            if (obj is Collection<*> && obj.isEmpty()) {
                return true
            }
            if (obj is Map<*, *> && obj.isEmpty()) {
                return true
            }
            if (obj is SparseArray<*> && obj.size() == 0) {
                return true
            }
            if (obj is SparseBooleanArray && obj.size() == 0) {
                return true
            }
            if (obj is SparseIntArray && obj.size() == 0) {
                return true
            }
            return obj is SparseLongArray && obj.size() == 0
        }

        /**
         * 判断对象是否非空
         *
         * @param obj 对象
         * @return `true`: 非空<br></br>`false`: 空
         */
        @JvmStatic
        fun isNotEmpty(obj: Any?): Boolean {
            return !isEmpty(obj)
        }

        @JvmStatic
        fun checkStrEmpty(str: String, display: String): Boolean {
            if (TextUtils.isEmpty(str)) {
                ArmsUtils.makeText(display)
                return true
            }
            return false
        }
    }
}
