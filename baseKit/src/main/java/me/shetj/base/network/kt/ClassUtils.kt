package me.shetj.base.network.kt

import okhttp3.RequestBody
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.util.Collections
import kotlin.collections.ArrayList

/**
 * [Type] 父类
 * [ParameterizedType] 泛型List<T>
 * [TypeVariable]
 * [GenericArrayType] 泛型数组
 * [java.lang.reflect.WildcardType] type 的子类
 */
object ClassUtils {

    /**
     * find the type by interfaces
     *
     * @param cls
     * @param <R>
     * @return
     </R> */
    fun <R> findNeedType(cls: Class<R>): Type {
        val typeList = getMethodTypes(cls)
        return if (typeList == null || typeList.isEmpty()) {
            RequestBody::class.java
        } else {
            typeList[0]
        }
    }

    /**
     * MethodHandler
     */
    fun <T> getMethodTypes(cls: Class<T>): List<Type>? {
        val typeOri = cls.genericSuperclass
        var needTypes: MutableList<Type>? = null
        // if Type is T
        if (typeOri is ParameterizedType) {
            needTypes = ArrayList()
            val parenTypes = typeOri.actualTypeArguments
            for (childType in parenTypes) {
                needTypes.add(childType)
                if (childType is ParameterizedType) {
                    val childTypes = childType.actualTypeArguments
                    Collections.addAll(needTypes, *childTypes)
                }
            }
        }
        return needTypes
    }

    fun getClass(type: Type, i: Int): Class<*> {
        return when (type) {
            is ParameterizedType -> { // 处理泛型类型
                getGenericClass(type, i)
            }
            is TypeVariable<*> -> {
                getClass(type.bounds[0], 0) // 处理泛型擦拭对象
            }
            else -> { // class本身也是type，强制转型
                type as Class<*>
            }
        }
    }

    fun getType(type: Type, i: Int): Type {
        return when (type) {
            is ParameterizedType -> { // 处理泛型类型
                getGenericType(type, i)
            }
            is TypeVariable<*> -> {
                getType(type.bounds[0], 0) // 处理泛型擦拭对象
            }
            else -> { // class本身也是type，强制转型
                type
            }
        }
    }

    fun getParameterizedType(type: Type, i: Int): Type {
        return when (type) {
            is ParameterizedType -> { // 处理泛型类型
                type.actualTypeArguments[i]
            }
            is TypeVariable<*> -> {
                getType(type.bounds[0], 0) // 处理泛型擦拭对象
            }
            else -> { // class本身也是type，强制转型
                type
            }
        }
    }

    fun getGenericClass(parameterizedType: ParameterizedType, i: Int): Class<*> {
        return when (val genericClass = parameterizedType.actualTypeArguments[i]) {
            is ParameterizedType -> { // 处理多级泛型
                genericClass.rawType as Class<*>
            }
            is GenericArrayType -> { // 处理数组泛型
                genericClass.genericComponentType as Class<*>
            }
            is TypeVariable<*> -> { // 处理泛型擦拭对象
                getClass(genericClass.bounds[0], 0)
            }
            else -> {
                genericClass as Class<*>
            }
        }
    }

    fun getGenericType(parameterizedType: ParameterizedType?, i: Int): Type {
        return when (val genericType = parameterizedType!!.actualTypeArguments[i]) {
            is ParameterizedType -> { // 处理多级泛型
                genericType.rawType
            }
            is GenericArrayType -> { // 处理数组泛型
                genericType.genericComponentType
            }
            is TypeVariable<*> -> { // 处理泛型擦拭对象
                getClass(genericType.bounds[0], 0)
            }
            else -> {
                genericType
            }
        }
    }

    /**
     * 普通类反射获取泛型方式，获取需要实际解析的类型
     *
     * @param <T>
     * @return
     </T> */
    fun <T> findNeedClass(cls: Class<T>): Type {
        // 以下代码是通过泛型解析实际参数,泛型必须传
        val genType = cls.genericSuperclass // 获取父类
        val params = (genType as ParameterizedType?)!!.actualTypeArguments // 获取<>
        val type = params[0] // date?
        val finalNeedType: Type = if (params.size > 1) { // 这个类似是：CacheResult<SkinTestResult> 2层
            check(type is ParameterizedType) { "没有填写泛型参数" }
            type.actualTypeArguments[0]
            // Type rawType = ((ParameterizedType) type).getRawType();
        } else { // 这个类似是:SkinTestResult  1层
            type
        }
        return finalNeedType
    }

    /**
     * 普通类反射获取泛型方式，获取最顶层的类型
     */
    fun <T> findRawType(cls: Class<T>): Type {
        val genType = cls.genericSuperclass // getGenericSuperclass() 获得该类带有泛型的父类
        return getGenericType(genType as ParameterizedType?, 0)
    }
}
