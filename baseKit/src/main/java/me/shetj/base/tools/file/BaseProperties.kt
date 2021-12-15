/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package me.shetj.base.tools.file

import me.shetj.base.S
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*


/**
 * 获取assent属性
 */
object BaseProperties {

    private const val TRUE_STRING = "true"

    private var properties = "base.properties"
    private val p: Properties = Properties()

    init {
        try {
            loadInputStream()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadInputStream(inputStream: InputStream = S.app.applicationContext.assets.open(properties)) {
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