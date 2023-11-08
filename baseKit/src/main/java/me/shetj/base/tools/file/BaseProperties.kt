package me.shetj.base.tools.file

import me.shetj.base.BaseKit
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

/**
 * 获取assent属性
 */
object BaseProperties {

    private const val TRUE_STRING = "true"

    private const val properties = "base.properties"
    private val p: Properties = Properties()

    init {
        try {
            loadInputStream()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadInputStream(inputStream: InputStream = BaseKit.app.applicationContext.assets.open(properties)) {
        p.load(inputStream)
    }

    fun getProperty(key: String): String {
        return p.getProperty(key)
    }

    fun setProperty(key: String, value: String) {
        p.setProperty(key, value)
    }

    fun isTrue(property: String): Boolean = property == TRUE_STRING

    /**
     * 保存到其他地方
     */
    fun saveConfig(file: String, properties: Properties) {
        try {
            val s = FileOutputStream(file, false)
            properties.store(s, "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
