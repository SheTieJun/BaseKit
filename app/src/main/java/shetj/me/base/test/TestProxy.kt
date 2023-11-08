

package shetj.me.base.test

import me.shetj.base.ktx.toJson
import java.lang.reflect.GenericArrayType
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType
import java.util.*

class TestProxy<T> : InvocationHandler {
    private var target: T? = // 维护一个目标对象
        null

    constructor(target: T?) {
        this.target = target
    }

    override fun invoke(proxy: Any, method: Method?, args: Array<out Any>?): Any? {
        println(
            "测试：${method?.name.toJson()}\n" +
                "genericReturnType:${method?.genericReturnType}\n" +
                "parameterAnnotations：${method?.parameterAnnotations?.size}\n" +
                "annotations：${method?.annotations?.size}\n" +
                "parameterTypes：${method?.parameterTypes?.size}\n" +
                "genericParameterTypes:${method?.genericParameterTypes?.size}\n"
        )

        method?.parameterAnnotations?.forEach {
        }

        method?.parameterTypes?.forEach {
            println(getRawType(it)?.simpleName)
        }

        method?.genericParameterTypes?.forEach {
            println(getRawType(it)?.simpleName)
        }
        target.let {
            method!!.invoke(target, *(args ?: arrayOfNulls<Any>(0)))
        }
        return null
    }

    fun getRawType(type: Type): Class<*>? {
        Objects.requireNonNull(type, "type == null")
        if (type is Class<*>) {
            // Type is a normal class.
            return type
        }
        if (type is ParameterizedType) {
            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            val rawType = type.rawType
            require(rawType is Class<*>)
            return rawType
        }
        if (type is GenericArrayType) {
            val componentType = type.genericComponentType
            return getRawType(componentType)?.let { java.lang.reflect.Array.newInstance(it, 0).javaClass }
        }
        if (type is TypeVariable<*>) {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            return Any::class.java
        }
        if (type is WildcardType) {
            return getRawType(type.upperBounds[0])
        }
        throw IllegalArgumentException(
            "Expected a Class, ParameterizedType, or " +
                "GenericArrayType, but <" +
                type +
                "> is of type " +
                type.javaClass.name
        )
    }
}

/**
 * 必须是接口
 */
class ProxyFactory {
    companion object {

        // 动态代理本质是静态代理是一样的，本质还是会有具体类进行实现

        inline fun <reified T> getProxy(tag: T?): T {
            var clazz = T::class.java
            val proxy = TestProxy(tag)
            return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), proxy) as T
        }
    }
}
