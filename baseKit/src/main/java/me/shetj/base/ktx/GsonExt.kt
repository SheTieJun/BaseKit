package me.shetj.base.ktx

import com.google.gson.internal.GsonTypes
import java.lang.reflect.Type

object GsonExt {
    /**
     * list<type>
     */
    @JvmStatic
    fun list(type: Type?): Type {
        return GsonTypes.newParameterizedTypeWithOwner(null, MutableList::class.java, type)
    }

    /**
     * set<type>
     */
    @JvmStatic
    fun set(type: Type?): Type {
        return GsonTypes.newParameterizedTypeWithOwner(null, MutableSet::class.java, type)
    }

    /**
     * hashMap<type,type2>
     */
    @JvmStatic
    fun hashMap(type: Type?, type2: Type?): Type {
        return GsonTypes.newParameterizedTypeWithOwner(null, HashMap::class.java, type, type2)
    }

    /**
     * map<type,type2>
     */
    @JvmStatic
    fun map(type: Type?, type2: Type?): Type {
        return GsonTypes.newParameterizedTypeWithOwner(null, MutableMap::class.java, type, type2)
    }

    /**
     * ownerType.rawType<typeArguments>
     */
    @JvmStatic
    fun parameterized(ownerType: Type?, rawClass: Class<*>?, vararg typeArguments: Type?): Type {
        return GsonTypes.newParameterizedTypeWithOwner(ownerType, rawClass, *typeArguments)
    }

    /**
     * 数组类型
     */
    @JvmStatic
    fun array(type: Type?): Type {
        return GsonTypes.arrayOf(type)
    }

    /**
     * type的子类
     */
    @JvmStatic
    fun subtypeOf(type: Type?): Type {
        return GsonTypes.subtypeOf(type)
    }

    /**
     * type的父类
     */
    @JvmStatic
    fun supertypeOf(type: Type?): Type {
        return GsonTypes.supertypeOf(type)
    }
}
