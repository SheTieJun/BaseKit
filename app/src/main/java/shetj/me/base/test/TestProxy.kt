package shetj.me.base.test

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


class TestProxy : InvocationHandler {

    override fun invoke(proxy: Any, method: Method?, args: Array<out Any>?): Any? {
        println("测试：${method?.name}")
        method?.genericReturnType
        method?.genericParameterTypes  //获取到参数类型
        method?.annotations //获取到所有注解
        method?.parameterTypes

        return method?.invoke(this,args)
    }
}


/**
 * 必须是接口
 */
class ProxyFactory {
    companion object {
        inline fun <reified T> getProxy(): T {
            val proxy = TestProxy()
            val clazz = T::class.java
            return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), proxy) as T
        }
    }
}
