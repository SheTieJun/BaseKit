package me.shetj.base.tools.json

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import me.shetj.base.ktx.logE
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

internal class BooleanTypeAdapter : JsonDeserializer<Boolean?> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement?, typeOfT: Type, context: JsonDeserializationContext): Boolean {
        return when {
            json == null -> false
            !json.isJsonPrimitive || json.isJsonObject || json.isJsonNull -> false
            (json as JsonPrimitive).isBoolean -> {
                json.getAsBoolean()
            }

            json.isString -> {
                val jsonValue = json.getAsString()
                if (jsonValue.equals("true", ignoreCase = true)) {
                    true
                } else if (jsonValue.equals("false", ignoreCase = true)) {
                    false
                } else {
                    false
                }
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
        if (json == null || !json.isJsonArray) return Collections.EMPTY_LIST
        val array: JsonArray = json.asJsonArray
        val itemType: Type = (typeOfT as ParameterizedType).actualTypeArguments[0]
        val list: MutableList<Any> = ArrayList<Any>()
        for (i in 0 until array.size()) {
            val element: JsonElement = array.get(i)
            val item: Any = context.deserialize(element, itemType)
            list.add(item)
        }
        return list
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
                val boolean = json.getAsBoolean()
                if (boolean) 1 else 0
            }

            json.isString -> {
                try {
                    Integer.valueOf(json.getAsString())
                } catch (e: NumberFormatException) {
//                    e.logE("IntTypeAdapter Fixed")
                    0
                }
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

            json.isJsonObject || json.isJsonNull -> {
                0f
            }

            json.isBoolean -> {
                val boolean = json.getAsBoolean()
                if (boolean) 1f else 0f
            }

            json.isString -> {
                try {
                    java.lang.Float.valueOf(json.getAsString())
                } catch (e: NumberFormatException) {
//                    e.logE("FloatTypeAdapter Fixed")
                    0f
                }
            }

            else -> json.asFloat
        }
    }
}

internal class DoubleTypeAdapter : JsonDeserializer<Double> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Double {
        return when {
            json == null -> 0.0
            json.isJsonObject || json.isJsonNull -> {
                0.0
            }

            !json.isJsonPrimitive -> 0.0
            (json as JsonPrimitive).isNumber -> {
                json.asNumber.toDouble()
            }

            json.isString -> {
                try {
                    java.lang.Double.valueOf(json.getAsString())
                } catch (e: NumberFormatException) {
//                    e.logE("DoubleTypeAdapter Fixed")
                    0.0
                }
            }

            json.isBoolean -> {
                val boolean = json.getAsBoolean()
                if (boolean) 1.0 else 0.0
            }

            else -> json.asDouble
        }
    }
}
