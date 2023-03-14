package me.shetj.base.ktx

import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.Type

/**
 * list<type>
 */
fun list(type: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, MutableList::class.java, type)
}

/**
 * set<type>
 */
fun set(type: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, MutableSet::class.java, type)
}

/**
 * hashMap<type,type2>
 */
fun hashMap(type: Type?, type2: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, HashMap::class.java, type, type2)
}

/**
 * map<type,type2>
 */
fun map(type: Type?, type2: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(null, MutableMap::class.java, type, type2)
}

/**
 * ownerType.rawType<typeArguments>
 */
fun parameterized(ownerType: Type?, rawType: Type?, vararg typeArguments: Type?): Type {
    return `$Gson$Types`.newParameterizedTypeWithOwner(ownerType, rawType, *typeArguments)
}

/**
 * 数组类型
 */
fun array(type: Type?): Type {
    return `$Gson$Types`.arrayOf(type)
}

/**
 * type的子类
 */
fun subtypeOf(type: Type?): Type {
    return `$Gson$Types`.subtypeOf(type)
}

/**
 * type的父类
 */
fun supertypeOf(type: Type?): Type {
    return `$Gson$Types`.supertypeOf(type)
}
