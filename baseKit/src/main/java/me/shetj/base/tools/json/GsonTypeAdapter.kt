package me.shetj.base.tools.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class BooleanTypeAdapter : JsonDeserializer<Boolean?> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement?, typeOfT: Type, context: JsonDeserializationContext): Boolean {
        return when {
            json == null -> false
            !json.isJsonPrimitive || json.isJsonObject || json.isJsonNull -> false
            (json as JsonPrimitive).isBoolean -> {
                json.asBoolean
            }

            json.isString -> {
                val jsonValue = json.asString
                jsonValue.equals("true", ignoreCase = true) || jsonValue == "1"
            }

            else -> {
                val code = json.asInt
                if (code == 0) false else code == 1
            }
        }
    }
}

internal class ListTypeAdapter : JsonDeserializer<List<*>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext): List<*> {
        if (json == null) return emptyList<Any>()

        // 修复：防御 ParameterizedType 强转异常
        if (typeOfT !is ParameterizedType) return emptyList<Any>()
        val itemType: Type = typeOfT.actualTypeArguments[0]

        // 修复：应对部分不规范接口单体对象直接返回的问题，将 {...} 包装成单元素列表
        if (!json.isJsonArray) {
            val singleItem = context.deserialize<Any?>(json, itemType)
            return if (singleItem != null) listOf(singleItem) else emptyList<Any>()
        }

        val array = json.asJsonArray
        // 修复：声明为 MutableList<Any?> 避免插入 null 引发空指针
        val list = ArrayList<Any?>(array.size())
        for (i in 0 until array.size()) {
            val element: JsonElement = array.get(i)
            val item: Any? = context.deserialize(element, itemType)
            list.add(item)
        }
        return list.filterNotNull()
    }
}

internal class IntTypeAdapter : JsonDeserializer<Int> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext): Int {
        return when {
            json == null || !json.isJsonPrimitive || json.isJsonObject || json.isJsonNull -> 0
            (json as JsonPrimitive).isNumber -> {
                json.asNumber.toInt()
            }

            json.isBoolean -> {
                val boolean = json.asBoolean
                if (boolean) 1 else 0
            }

            json.isString -> {
                // 修复：兼容带小数的字符串（如 "12.34"），同时避免 catch NumberFormatException 的性能开销
                json.asString.toDoubleOrNull()?.toInt() ?: 0
            }

            else -> json.asInt
        }
    }
}

internal class FloatTypeAdapter : JsonDeserializer<Float> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Float {
        return when {
            json == null || !json.isJsonPrimitive -> 0f
            (json as JsonPrimitive).isNumber -> {
                json.asNumber.toFloat()
            }

            json.isBoolean -> {
                val boolean = json.asBoolean
                if (boolean) 1f else 0f
            }

            json.isString -> {
                json.asString.toFloatOrNull() ?: 0f
            }

            else -> json.asFloat
        }
    }
}

internal class DoubleTypeAdapter : JsonDeserializer<Double> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Double {
        return when {
            json == null || !json.isJsonPrimitive -> 0.0
            (json as JsonPrimitive).isNumber -> {
                json.asNumber.toDouble()
            }

            json.isString -> {
                json.asString.toDoubleOrNull() ?: 0.0
            }

            json.isBoolean -> {
                val boolean = json.asBoolean
                if (boolean) 1.0 else 0.0
            }

            else -> json.asDouble
        }
    }
}

internal class LongTypeAdapter : JsonDeserializer<Long> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long {
        return when {
            json == null || !json.isJsonPrimitive || json.isJsonObject || json.isJsonNull -> 0L
            (json as JsonPrimitive).isNumber -> {
                json.asNumber.toLong()
            }

            json.isBoolean -> {
                val boolean = json.asBoolean
                if (boolean) 1L else 0L
            }

            json.isString -> {
                json.asString.toDoubleOrNull()?.toLong() ?: 0L
            }

            else -> json.asLong
        }
    }
}

internal class StringTypeAdapter : JsonDeserializer<String> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): String {
        return when {
            json == null || json.isJsonNull -> ""
            json.isJsonPrimitive -> {
                val primitive = json as JsonPrimitive
                if (primitive.isString) {
                    val value = primitive.asString
                    if (value.equals("null", ignoreCase = true)) "" else value
                } else if (primitive.isNumber) {
                    primitive.asNumber.toString()
                } else if (primitive.isBoolean) {
                    primitive.asBoolean.toString()
                } else {
                    json.asString
                }
            }

            else -> json.toString()
        }
    }
}
