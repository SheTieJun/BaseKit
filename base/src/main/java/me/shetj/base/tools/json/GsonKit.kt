package me.shetj.base.tools.json

import androidx.annotation.Keep

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap

import io.reactivex.annotations.NonNull
import timber.log.Timber

/**
 * @author shetj
 */
@Keep
object GsonKit {
    private var gson: Gson? = null

    init {
        if (gson == null) {
            gson = Gson()
        }
    }

    /**
     * 将对象转换成json格式
     * @param ts
     * @return
     */
    @JvmStatic
    fun objectToJson(@NonNull ts: Any): String? {
        try {
            var jsonStr: String? = null
            if (gson != null) {
                jsonStr = gson!!.toJson(ts)
            }
            return jsonStr
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }

    /**
     * 将对象转换成json格式(并自定义日期格式)
     *
     * @param ts
     * @return
     */
    @JvmStatic
    fun objectToJson(@NonNull ts: Any, dateformat: String): String? {
        try {
            val gson = GsonBuilder().registerTypeHierarchyAdapter(Date::class.java, JsonSerializer<Date> { src, typeOfSrc, context ->
                val format = SimpleDateFormat(dateformat)
                JsonPrimitive(format.format(src))
            }).setDateFormat(dateformat).create()
            return gson.toJson(ts)
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }

    /**
     * 将json格式转换成list对象
     * @param jsonStr
     * @return
     */
    @JvmStatic
    fun <T> jsonToList(@NonNull jsonStr: String): List<T>? {
        try {
            var objList: List<T>? = null
            if (gson != null) {
                val type = object : TypeToken<List<T>>() {

                }.type
                objList = gson!!.fromJson<List<T>>(jsonStr, type)
            }
            return objList
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }

    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    @JvmStatic
    fun <T> GsonToListMaps(@NonNull gsonString: String): List<Map<String, T>>? {
        try {
            var list: List<Map<String, T>>? = null
            if (gson != null) {
                list = gson!!.fromJson<List<Map<String, T>>>(gsonString,
                        object : TypeToken<List<Map<String, T>>>() {

                        }.type)
            }
            return list
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }

    /**
     * 转成list
     * 解决泛型问题
     * @param json
     * @param cls
     * @param <T>
     * @return
    </T> */
    @JvmStatic
    fun <T> jsonToList(@NonNull json: String, cls: Class<T>): List<T>? {
        try {
            val gson = Gson()
            val list = ArrayList<T>()
            val array = JsonParser().parse(json).asJsonArray
            for (elem in array) {
                list.add(gson.fromJson(elem, cls))
            }
            return list
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    @JvmStatic
    fun jsonToMap(@NonNull gsonString: String): Map<String, Any>? {

        try {
            var map: Map<String, Any>? = null
            if (gson != null) {
                map = gson!!.fromJson<Map<String, Any>>(gsonString, object : TypeToken<Map<String, Any>>() {

                }.type)
            }
            return map
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    @JvmStatic
    fun jsonToStringMap(@NonNull gsonString: String): Map<String, String>? {

        try {
            var map: Map<String, String>? = null
            if (gson != null) {
                map = gson!!.fromJson<Map<String, String>>(gsonString, object : TypeToken<Map<String, String>>() {

                }.type)
            }
            return map
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }

    /**
     * 将json转换成bean对象
     * @param jsonStr
     * @return
     */
    @JvmStatic
    fun <T> jsonToBean(@NonNull jsonStr: String, cl: Class<T>): T? {
        try {
            var obj: T? = null
            if (gson != null) {
                obj = gson!!.fromJson(jsonStr, cl)
            }
            return obj
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }

    /**
     * 根据key获得value值
     * @param jsonStr
     * @param key
     * @return
     */
    @JvmStatic
    fun getJsonValue(@NonNull jsonStr: String, key: String): Any? {
        try {
            var rusObj: Any? = null
            var rusMap: Map<String, Any>? = null
            if (gson != null) {
                rusMap = gson!!.fromJson<Map<String, Any>>(jsonStr, object : TypeToken<Map<String, Any>>() {

                }.type)
            }
            if (rusMap != null && rusMap.size > 0) {
                rusObj = rusMap[key]
            }
            return rusObj
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }


}

//class TypeFactory {
//
//    public static Type $List(Type type) {
//        return $Gson$Types.newParameterizedTypeWithOwner(null, List.class, type);
//    }
//
//    public static Type $Set(Type type) {
//        return $Gson$Types.newParameterizedTypeWithOwner(null, Set.class, type);
//    }
//
//    public static Type $HashMap(Type type, Type type2) {
//        return $Gson$Types.newParameterizedTypeWithOwner(null, HashMap.class, type, type2);
//    }
//
//    public static Type $Map(Type type, Type type2) {
//        return $Gson$Types.newParameterizedTypeWithOwner(null, Map.class, type, type2);
//    }
//
//    public static Type $Parameterized(Type ownerType, Type rawType, Type... typeArguments) {
//        return $Gson$Types.newParameterizedTypeWithOwner(ownerType, rawType, typeArguments);
//    }
//
//    public static Type $Array(Type type) {
//        return $Gson$Types.arrayOf(type);
//    }
//
//    public static Type $SubtypeOf(Type type) {
//        return $Gson$Types.subtypeOf(type);
//    }
//
//    public static Type $SupertypeOf(Type type) {
//        return $Gson$Types.supertypeOf(type);
//    }
//}