package me.shetj.base.tools.app

import androidx.annotation.Keep

@Keep
object SingletonManager {
    private val objMap = HashMap<String, Any>()

    fun registerService(key: String, instance: Any) {
        if (!objMap.containsKey(key)) {
            objMap[key] = instance
        }
    }

    fun getService(key: String): Any? {
        return objMap[key]
    }

    fun reMove(key: String) {
        objMap.remove(key)
    }
}
