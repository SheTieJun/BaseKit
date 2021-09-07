package me.shetj.base.tools.json

import androidx.annotation.Keep
import androidx.annotation.NonNull
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Modifier
import java.lang.reflect.Type


/**
 * @author shetj
 */
@Keep
object GsonKit {
    val gson: Gson by lazy {
        GsonBuilder()
            .excludeFieldsWithModifiers(
                Modifier.TRANSIENT,
                Modifier.STATIC
            )
            .registerTypeAdapter(Int::class.java, object : TypeAdapter<Int?>() {
                @Throws(IOException::class)
                override fun write(out: JsonWriter, value: Int?) {
                    out.value(value.toString())
                }

                @Throws(IOException::class)
                override fun read(`in`: JsonReader): Int? {
                    if (`in`.peek() == JsonToken.NULL) {
                        `in`.nextNull()
                        return null
                    }
                    return try {
                        Integer.valueOf(`in`.nextString())
                    } catch (e: NumberFormatException) {
                        0
                    }
                }
            }).registerTypeAdapter(Float::class.java, object : TypeAdapter<Float>() {
                @Throws(IOException::class)
                override fun write(out: JsonWriter, value: Float?) {
                    out.value(value.toString())
                }

                @Throws(IOException::class)
                override fun read(`in`: JsonReader): Float {
                    return try {
                        java.lang.Float.valueOf(`in`.nextString())
                    } catch (e: NumberFormatException) {
                        0f
                    }
                }
            })//比如我们想排除私有字段不被序列化/反序列，默认
            .serializeNulls()//serializeNulls支持空对象序列化
            .create()
    }

    /**
     * 将对象转换成json格式
     */
    @JvmStatic
    fun objectToJson(@NonNull ts: Any): String? {
        return runCatching {
            val jsonStr: String? = gson.toJson(ts)
            jsonStr
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 转成list中有map的
     * TODO need Test
     */
    @JvmStatic
    fun <T> jsonToListMaps(@NonNull gsonString: String): List<Map<String, T>>? {
        return runCatching {
            ArrayList<Map<String, T>>().also { list ->
                JsonParser.parseString(gsonString).asJsonArray.forEach {
                    list.add(gson.fromJson(it, object : TypeToken<Map<String, Any>>() {}.type))
                }
            }
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 转成list
     * 解决泛型擦除问题
     */
    @JvmStatic
    fun <T> jsonToList(@NonNull json: String, cls: Class<T>): List<T>? {
        return runCatching<List<T>> {
            gson.fromJson(json, TypeToken.getParameterized(List::class.java, cls).type)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }


    @JvmStatic
    fun <T> jsonToList2(@NonNull json: String, cls: Class<T>): List<T>? {
        return runCatching<List<T>> {
            gson.fromJson(
                json,
                `$Gson$Types`.newParameterizedTypeWithOwner(null, List::class.java, cls)
            )
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 转成map的
     */
    @JvmStatic
    fun jsonToMap(@NonNull gsonString: String): Map<String, Any>? {
        return runCatching<Map<String, Any>> {
            gson.fromJson(gsonString, object : TypeToken<Map<String, Any>>() {
            }.type)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()

    }

    /**
     * 转成map的
     */
    @JvmStatic
    fun jsonToStringMap(@NonNull gsonString: String): Map<String, String>? {
        return runCatching {
            gson.fromJson<Map<String, String>>(
                gsonString,
                object : TypeToken<Map<String, String>>() {}.type
            )
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 将json转换成bean对象
     */
    @JvmStatic
    fun <T> jsonToBean(@NonNull jsonStr: String, cl: Class<T>): T? {
        return runCatching<T> {
            gson.fromJson(jsonStr, cl)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 将json转换成bean对象
     */
    @JvmStatic
    fun <T> jsonToBean(@NonNull jsonStr: String, type: Type): T? {
        return runCatching<T> {
            gson.fromJson(jsonStr, type)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 根据key获得value值
     */
    @JvmStatic
    fun getJsonValue(@NonNull jsonStr: String, key: String): Any? {
        return kotlin.runCatching {
            gson.fromJson<Map<String, Any>>(jsonStr, object : TypeToken<Map<String, Any>>() {
            }.type)?.let { it[key] }
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

}