package me.shetj.base.tools.json

import androidx.annotation.Keep
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import io.reactivex.annotations.NonNull
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

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
        return try {
            var jsonStr: String? = null
            if (gson != null) {
                jsonStr = gson!!.toJson(ts)
            }
            jsonStr
        } catch (e: Exception) {
            Timber.e(e)
            null
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
        return try {
            val info = GsonBuilder().registerTypeHierarchyAdapter(Date::class.java,
                    JsonSerializer<Date> { src, _, _ ->
                val format = SimpleDateFormat(dateformat)
                JsonPrimitive(format.format(src))
            }).setDateFormat(dateformat).create()
            info.toJson(ts)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }

    /**
     * 将json格式转换成list对象
     * @param jsonStr
     * @return
     */
    @JvmStatic
    fun <T> jsonToList(@NonNull jsonStr: String): List<T>? {
        return try {
            var objList: List<T>? = null
            if (gson != null) {
                val type = object : TypeToken<List<T>>() {

                }.type
                objList = gson!!.fromJson<List<T>>(jsonStr, type)
            }
            objList
        } catch (e: Exception) {
            Timber.e(e)
            null
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
        return try {
            var list: List<Map<String, T>>? = null
            if (gson != null) {
                list = gson!!.fromJson<List<Map<String, T>>>(gsonString,
                        object : TypeToken<List<Map<String, T>>>() {

                        }.type)
            }
            list
        } catch (e: Exception) {
            Timber.e(e)
            null
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
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    @JvmStatic
    fun jsonToMap(@NonNull gsonString: String): Map<String, Any>? {

        return try {
            var map: Map<String, Any>? = null
            if (gson != null) {
                map = gson!!.fromJson<Map<String, Any>>(gsonString, object : TypeToken<Map<String, Any>>() {

                }.type)
            }
            map
        } catch (e: Exception) {
            Timber.e(e)
            null
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

        return try {
            var map: Map<String, String>? = null
            if (gson != null) {
                map = gson!!.fromJson<Map<String, String>>(gsonString, object : TypeToken<Map<String, String>>() {

                }.type)
            }
            map
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

    }

    /**
     * 将json转换成bean对象
     * @param jsonStr
     * @return
     */
    @JvmStatic
    fun <T> jsonToBean(@NonNull jsonStr: String, cl: Class<T>): T? {
        return try {
            var obj: T? = null
            if (gson != null) {
                obj = gson!!.fromJson(jsonStr, cl)
            }
            obj
        } catch (e: Exception) {
            Timber.e(e)
            null
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
            if (rusMap != null && rusMap.isNotEmpty()) {
                rusObj = rusMap[key]
            }
            return rusObj
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }

    }
}