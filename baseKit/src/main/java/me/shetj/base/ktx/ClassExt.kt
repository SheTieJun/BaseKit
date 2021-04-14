package me.shetj.base.ktx

import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.*
import java.util.*

/**
 * 参数化类型
 * A<C>  -> getClazz(this) -> C
 * 获取当前类的第一个泛型
 * tip：必须是泛型
 */
@Suppress("UNCHECKED_CAST")
fun <C> getClazz(obj: Any,position: Int = 0): Class<C> {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[position] as Class<C>
}

/**
 * 通过class<T>、T的无参数的构造函数，创建对象T
 */
fun <T> getObjByClassArg(obj: Any,position:Int = 0):T{
    return  getClazz<T>(obj,position).newInstance()
}

fun getParameterizedType(type: Type,typeArguments:Type): ParameterizedType? {
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
            "Expected a Class, ParameterizedType, or "
                    + "GenericArrayType, but <"
                    + type
                    + "> is of type "
                    + type.javaClass.name
    )
}