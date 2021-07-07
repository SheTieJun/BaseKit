package me.shetj.base.tools.json

import androidx.annotation.Keep
import androidx.annotation.NonNull
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.lang.reflect.Modifier
import java.lang.reflect.Type


/**
 * @author shetj
 */
@Keep
object GsonKit {
    val gson: Gson by lazy {
        GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC) //比如我们想排除私有字段不被序列化/反序列，默认
                .serializeNulls()//serializeNulls支持空对象序列化
                .create()
    }

    /**
     * 将对象转换成json格式
     */
    @JvmStatic
    fun objectToJson(@NonNull ts: Any): String? {
        return try {
            val jsonStr: String? = gson.toJson(ts)
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
            val array = JsonParser.parseString(gsonString).asJsonArray
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
            return gson.fromJson(json, TypeToken.getParameterized(List::class.java, cls).type)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }


    @JvmStatic
    fun <T> jsonToList2(@NonNull json: String, cls: Class<T>): List<T>? {
        return try {
            return gson.fromJson(json, `$Gson$Types`.newParameterizedTypeWithOwner(null, List::class.java, cls))
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
            val map: Map<String, Any>? = gson.fromJson<Map<String, Any>>(gsonString, object : TypeToken<Map<String, Any>>() {
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
            val map = gson.fromJson<Map<String, String>>(gsonString, object : TypeToken<Map<String, String>>() {

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
            val obj: T? = gson.fromJson(jsonStr, cl)
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
            val obj: T? = gson.fromJson(jsonStr, type)
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
        return try {
            var rusObj: Any? = null
            val rusMap: Map<String, Any>? = gson.fromJson<Map<String, Any>>(jsonStr, object : TypeToken<Map<String, Any>>() {

            }.type)
            if (rusMap != null && rusMap.isNotEmpty()) {
                rusObj = rusMap[key]
            }
            rusObj
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }

}