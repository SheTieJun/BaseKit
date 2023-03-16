

package me.shetj.base.tools.json

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import me.shetj.base.ktx.logE


internal class BooleanTypeAdapter : JsonDeserializer<Boolean?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement, typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Boolean? {
        if ((json as JsonPrimitive).isBoolean) {
            return json.getAsBoolean()
        }
        if (json.isString) {
            val jsonValue = json.getAsString()
            return if (jsonValue.equals("true", ignoreCase = true)) {
                true
            } else if (jsonValue.equals("false", ignoreCase = true)) {
                false
            } else {
                null
            }
        }
        val code = json.getAsInt()
        return if (code == 0) false else if (code == 1) true else null
    }
}


internal class ListTypeAdapter : JsonDeserializer<List<*>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<*> {
        json?.let {
            if (json.isJsonArray) {
                val array: JsonArray = json.asJsonArray
                val itemType: Type = (typeOfT as ParameterizedType).actualTypeArguments.get(0)
                val list: MutableList<Any> = ArrayList<Any>()
                for (i in 0 until array.size()) {
                    val element: JsonElement = array.get(i)
                    val item: Any = context!!.deserialize(element, itemType)
                    list.add(item)
                }
                return list
            } else {
                //和接口类型不符，返回空List
                return Collections.EMPTY_LIST
            }
        }
        return Collections.EMPTY_LIST
    }
}


internal class IntTypeAdapter : JsonDeserializer<Int> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Int {
        json?.let {
            if (json.isJsonPrimitive) {
                if ((json as JsonPrimitive).isNumber) {
                    return json.asNumber.toInt()
                }
                if (json.isJsonObject || json.isJsonNull) {
                    return 0
                }
                if (json.isBoolean) {
                    val boolean = json.getAsBoolean()
                    return if (boolean) 1 else 0
                }
                if (json.isString) {
                    return try {
                        Integer.valueOf(json.getAsString())
                    } catch (e: NumberFormatException) {
                        e.logE()
                        0
                    }
                }
                return json.getAsInt()
            } else {
                //和接口类型不符，返回空List
                return 0
            }
        }
        return 0
    }
}


internal class FloatTypeAdapter : JsonDeserializer<Float> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Float {
        json?.let {
            if (json.isJsonPrimitive) {
                if ((json as JsonPrimitive).isNumber) {
                    return json.asNumber.toFloat()
                }
                if (json.isJsonObject || json.isJsonNull) {
                    return 0f
                }
                if (json.isBoolean) {
                    val boolean = json.getAsBoolean()
                    return if (boolean) 1f else 0f
                }
                if (json.isString) {
                    return try {
                        java.lang.Float.valueOf(json.getAsString())
                    } catch (e: NumberFormatException) {
                        e.logE()
                        0f
                    }
                }
                return json.asFloat
            } else {
                //和接口类型不符，返回空List
                return 0f
            }
        }
        return 0f
    }
}

internal class DoubleTypeAdapter : JsonDeserializer<Double> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Double {
        json?.let {
            if (json.isJsonPrimitive) {
                if ((json as JsonPrimitive).isNumber) {
                    return json.asNumber.toDouble()
                }
                if (json.isJsonObject || json.isJsonNull) {
                    return 0.0
                }
                if (json.isString) {
                    return try {
                        java.lang.Double.valueOf(json.getAsString())
                    } catch (e: NumberFormatException) {
                        e.logE()
                        0.0
                    }
                }
                if (json.isBoolean) {
                    val boolean = json.getAsBoolean()
                    return if (boolean) 1.0 else   0.0
                }
                return json.asDouble
            } else {
                //和接口类型不符，返回空List
                return 0.0
            }
        }
        return 0.0
    }
}