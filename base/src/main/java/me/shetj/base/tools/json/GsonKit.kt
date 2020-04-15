package me.shetj.base.tools.json

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import io.reactivex.annotations.NonNull
import timber.log.Timber
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * @author shetj
 */
@Keep
object GsonKit {
    val gson: Gson by lazy {
        GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create()
    }

    /**
     * 将对象转换成json格式
     */
    @JvmStatic
    fun objectToJson(@NonNull ts: Any): String? {
        return try {
            var jsonStr: String? = null
            jsonStr = gson.toJson(ts)
            jsonStr
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }

    /**
     * 转成list中有map的
     * TODO need Test
     */
    @JvmStatic
    fun <T> jsonToListMaps(@NonNull gsonString: String): List<Map<String, T>>? {
        return try {
            val list: ArrayList<Map<String, T>> = ArrayList()
            val array = JsonParser().parse(gsonString).asJsonArray
            for (elem in array) {
                list.add(gson.fromJson<Map<String, T>>(gsonString, object : TypeToken<Map<String, Any>>() {
                }.type))
            }
            list
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    /**
     * 转成list
     * 解决泛型擦除问题
     */
    @JvmStatic
    fun <T> jsonToList(@NonNull json: String, cls: Class<T>): List<T>? {
        return try {
            val gson = Gson()
            val list = ArrayList<T>()
            val array = JsonParser().parse(json).asJsonArray
            for (elem in array) {
                list.add(gson.fromJson(elem, cls))
            }
            list
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }


    /**
     * 转成list
     * 解决泛型擦除问题
     */
    fun <T> jsonToList2(strJson: String, cls: Class<T>): List<T>? {
        return try {
            var list: List<T>? = ArrayList()
            val gson = Gson()
            val type: Type = ListParameterizedType(cls)
            list = gson.fromJson(strJson, type)
            list
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }

    /**
     * 转成map的
     */
    @JvmStatic
    fun jsonToMap(@NonNull gsonString: String): Map<String, Any>? {

        return try {
            var map: Map<String, Any>? = null
            map = gson.fromJson<Map<String, Any>>(gsonString, object : TypeToken<Map<String, Any>>() {

            }.type)
            map
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }

    /**
     * 转成map的
     */
    @JvmStatic
    fun jsonToStringMap(@NonNull gsonString: String): Map<String, String>? {

        return try {
            var map: Map<String, String>? = null
            map = gson.fromJson<Map<String, String>>(gsonString, object : TypeToken<Map<String, String>>() {

            }.type)
            map
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }

    /**
     * 将json转换成bean对象
     */
    @JvmStatic
    fun <T> jsonToBean(@NonNull jsonStr: String, cl: Class<T>): T? {
        return try {
            var obj: T? = null
            obj = gson.fromJson(jsonStr, cl)
            obj
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }

    /**
     * 将json转换成bean对象
     */
    @JvmStatic
    fun <T> jsonToBean(@NonNull jsonStr: String, type: Type): T? {
        return try {
            var obj: T? = null
            obj = gson.fromJson(jsonStr, type)
            obj
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    /**
     * 根据key获得value值
     */
    @JvmStatic
    fun getJsonValue(@NonNull jsonStr: String, key: String): Any? {
        try {
            var rusObj: Any? = null
            var rusMap: Map<String, Any>? = null
            rusMap = gson.fromJson<Map<String, Any>>(jsonStr, object : TypeToken<Map<String, Any>>() {

            }.type)
            if (rusMap != null && rusMap.isNotEmpty()) {
                rusObj = rusMap[key]
            }
            return rusObj
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }

    private class ListParameterizedType internal constructor(val type: Type) : ParameterizedType {

        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(type)
        }

        override fun getRawType(): Type {
            return ArrayList::class.java
        }

        override fun getOwnerType(): Type? {
            return null
        }
    }
}