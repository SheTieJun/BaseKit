package me.shetj.base.tools.json

import androidx.annotation.Keep
import androidx.annotation.NonNull
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

fun getJson(): Json = Json {
    ignoreUnknownKeys = true // 忽略未知键
    isLenient = true         // 允许宽松语法
    encodeDefaults = true    // 序列化时包含默认值
    explicitNulls = true     // 明确输出null
    coerceInputValues = true // 将 null 转换为对应类型的默认值（如果可能）
}

/**
 * @author shetj
 */
@Keep
object JsonKit {
    val json: Json by lazy { getJson() }

    /**
     * 将对象转换成json格式
     */
    @JvmStatic
    inline fun <reified T> objectToJson(@NonNull ts: T): String? {
        return runCatching {
            json.encodeToString(ts)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 将对象转换成json格式，指定序列化器
     */
    @JvmStatic
    fun <T> objectToJson(@NonNull ts: T, serializer: KSerializer<T>): String? {
        return runCatching {
            json.encodeToString(serializer, ts)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 将json转换成bean对象
     */
    @JvmStatic
    inline fun <reified T> jsonToBean(@NonNull jsonStr: String): T? {
        return runCatching<T> {
            json.decodeFromString<T>(jsonStr)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }

    /**
     * 将json转换成bean对象，指定反序列化器
     */
    @JvmStatic
    fun <T> jsonToBean(@NonNull jsonStr: String, deserializer: KSerializer<T>): T? {
        return runCatching<T> {
            json.decodeFromString(deserializer, jsonStr)
        }.onFailure {
            Timber.e(it)
        }.getOrNull()
    }
}
