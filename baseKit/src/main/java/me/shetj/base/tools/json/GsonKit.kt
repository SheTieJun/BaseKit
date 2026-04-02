package me.shetj.base.tools.json

import androidx.annotation.Keep
import androidx.annotation.NonNull
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import me.shetj.base.ktx.GsonExt
import timber.log.Timber
import java.lang.reflect.Modifier
import java.lang.reflect.Type

fun getGson(): Gson = GsonBuilder()
    .excludeFieldsWithModifiers(
        Modifier.TRANSIENT,
        Modifier.STATIC,
        Modifier.NATIVE
    ) // 比如我们想排除私有字段不被序列化/反序列，默认
    .registerTypeAdapter(Int::class.java, IntTypeAdapter())
    .registerTypeAdapter(Float::class.java, FloatTypeAdapter())
    .registerTypeAdapter(Double::class.java, DoubleTypeAdapter())
    .registerTypeAdapter(Long::class.java, LongTypeAdapter())
    .registerTypeAdapter(String::class.java, StringTypeAdapter())
    .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
    .registerTypeAdapter(List::class.java, ListTypeAdapter())
    .serializeNulls() // serializeNulls支持空对象序列化
    .create()

/**
 * @author shetj
 */
@Keep
object GsonKit {
    val gson: Gson by lazy { getGson() }

    /**
     * 将对象转换成json格式
     */
    @JvmStatic
    fun objectToJson(@NonNull ts: Any): String? {
        return runCatching {
            gson.toJson(ts)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 转成list中有map的
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    inline fun <reified T> jsonToListMaps(@NonNull gsonString: String): List<Map<String, T>>? {
        return runCatching {
            val type = GsonExt.list(GsonExt.map(String::class.java, T::class.java))
            gson.fromJson<List<Map<String, T>>>(gsonString, type)
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
            gson.fromJson(json, GsonExt.list(cls))
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
            gson.fromJson(
                gsonString,
                GsonExt.map(String::class.java, Any::class.java)
            )
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
                GsonExt.map(String::class.java, String::class.java)
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
        return runCatching {
            val element = JsonParser.parseString(jsonStr).asJsonObject.get(key)
            if (element != null && !element.isJsonNull) {
                gson.fromJson(element, Any::class.java)
            } else {
                null
            }
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    // ========================== 以下为新增的常用扩展方法 ==========================

    /**
     * 利用 Kotlin 内联函数和 reified 实化类型参数，直接转换 JSON 到对象
     * 用法: val user = GsonKit.fromJson<User>(jsonStr)
     */
    inline fun <reified T> fromJson(jsonStr: String): T? {
        return runCatching<T> {
            gson.fromJson(jsonStr, object : TypeToken<T>() {}.type)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 利用 Kotlin 内联函数和 reified 实化类型参数，直接转换 JSON 到 List<T>
     * 用法: val list = GsonKit.fromJsonList<User>(jsonStr)
     */
    inline fun <reified T> fromJsonList(jsonStr: String): List<T>? {
        return runCatching<List<T>> {
            val type = GsonExt.list(T::class.java)
            gson.fromJson(jsonStr, type)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 格式化 JSON 字符串（Pretty Print）
     */
    @JvmStatic
    fun formatJson(jsonStr: String): String? {
        return runCatching {
            val jsonElement = JsonParser.parseString(jsonStr)
            GsonBuilder().setPrettyPrinting().create().toJson(jsonElement)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 解析为 JsonObject
     */
    @JvmStatic
    fun parseObject(jsonStr: String): JsonObject? {
        return runCatching {
            JsonParser.parseString(jsonStr).asJsonObject
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 解析为 JsonArray
     */
    @JvmStatic
    fun parseArray(jsonStr: String): JsonArray? {
        return runCatching {
            JsonParser.parseString(jsonStr).asJsonArray
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }
}
