@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package me.shetj.base.ktx

import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType
import java.util.Objects

/**
 * 参数化类型
 * A<C>  -> getClazz(this) -> C
 * 获取当前类的第一个泛型,
 * - 继承基类而来的泛型: getGenericSuperclass() , 转型为 ParameterizedType 来获得实际类型
 * - tip：必须是泛型,否则会异常
 */
@Suppress("UNCHECKED_CAST")
fun <C> getClazz(obj: Any, position: Int = 0): Class<C> {
    return (obj.javaClass.genericSuperclass as ParameterizedType)
        .actualTypeArguments[position] as Class<C>
}

/** -实现接口而来的泛型，就用 getGenericInterfaces() , 针对其中的元素转型为 ParameterizedType 来获得实际类型
 * - tip：必须是泛型,否则会异常
 * @param positionInterface 要获取泛型接口位置
 * @param position 泛型位置
 * @return
 */
@Suppress("UNCHECKED_CAST")
fun <C> getClazzByInterface(obj: Any, positionInterface: Int = 0, position: Int = 0): Class<C> {
    return (obj.javaClass.genericInterfaces[positionInterface] as ParameterizedType)
        .actualTypeArguments[position] as Class<C>
}

/**
 * 通过class<T>、T的无参数的构造函数，创建对象T
 */
fun <T> getObjByClassArg(obj: Any, position: Int = 0): T {
    return getClazz<T>(obj, position).getDeclaredConstructor().newInstance()
}

fun getParameterizedType(type: Type, typeArguments: Type): ParameterizedType? {
    // Type type = com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner(null, ArrayList.class, clazz); = ArrayList<clazz>
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, type, typeArguments)
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
        return java.lang.reflect.Array.newInstance(getRawType(componentType), 0).javaClass
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
