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
package me.shetj.base.network.model

import java.io.File
import java.io.InputStream
import java.io.Serializable
import java.net.URLConnection
import me.shetj.base.network.callBack.ProgressResponseCallBack
import okhttp3.MediaType

class HttpParams : Serializable {
    /**
     * 普通的键值对参数
     */
    var urlParamsMap: LinkedHashMap<String, String>? = null

    /**
     * 文件的键值对参数
     */
    var fileParamsMap: LinkedHashMap<String, MutableList<FileWrapper<*>>>? = null

    constructor() {
        init()
    }

    constructor(key: String, value: String) {
        init()
        put(key, value)
    }

    private fun init() {
        urlParamsMap = LinkedHashMap()
        fileParamsMap = LinkedHashMap()
    }

    fun put(params: HttpParams?) {
        if (params != null) {
            if (params.urlParamsMap != null && params.urlParamsMap!!.isNotEmpty())
                urlParamsMap!!.putAll(params.urlParamsMap!!)
            if (params.fileParamsMap != null && params.fileParamsMap!!.isNotEmpty()) {
                fileParamsMap!!.putAll(params.fileParamsMap!!)
            }
        }
    }

    fun put(params: Map<String, String>?) {
        if (params == null || params.isEmpty()) return
        urlParamsMap!!.putAll(params)
    }

    fun put(key: String, value: String) {
        urlParamsMap!![key] = value
    }

    fun <T : File?> put(key: String?, file: T, responseCallBack: ProgressResponseCallBack?) {
        put(key, file, file!!.name, responseCallBack)
    }

    fun <T : File?> put(
        key: String?,
        file: T,
        fileName: String,
        responseCallBack: ProgressResponseCallBack?
    ) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack)
    }

    fun <T : InputStream?> put(
        key: String?,
        file: T,
        fileName: String,
        responseCallBack: ProgressResponseCallBack?
    ) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack)
    }

    fun put(
        key: String?,
        bytes: ByteArray,
        fileName: String,
        responseCallBack: ProgressResponseCallBack?
    ) {
        put(key, bytes, fileName, guessMimeType(fileName), responseCallBack)
    }

    fun put(key: String?, fileWrapper: FileWrapper<*>?) {
        if (key != null && fileWrapper != null) {
            put(
                key,
                fileWrapper.file as Any,
                fileWrapper.fileName,
                fileWrapper.contentType,
                fileWrapper.responseCallBack
            )
        }
    }

    fun <T> put(
        key: String?,
        countent: T,
        fileName: String,
        contentType: MediaType?,
        responseCallBack: ProgressResponseCallBack?
    ) {
        if (key != null) {
            var fileWrappers = fileParamsMap!![key]
            if (fileWrappers == null) {
                fileWrappers = ArrayList()
                fileParamsMap!![key] = fileWrappers
            }
            fileWrappers.add(FileWrapper<Any?>(countent, fileName, contentType, responseCallBack))
        }
    }

    fun <T : File?> putFileParams(
        key: String?,
        files: List<T>?,
        responseCallBack: ProgressResponseCallBack?
    ) {
        if (key != null && files != null && files.isNotEmpty()) {
            for (file in files) {
                put<File>(key, file!!, responseCallBack)
            }
        }
    }

    fun putFileWrapperParams(key: String?, fileWrappers: List<FileWrapper<*>?>?) {
        if (key != null && fileWrappers != null && fileWrappers.isNotEmpty()) {
            for (fileWrapper in fileWrappers) {
                put(key, fileWrapper)
            }
        }
    }

    fun removeUrl(key: String?) {
        urlParamsMap!!.remove(key)
    }

    fun removeFile(key: String) {
        fileParamsMap!!.remove(key)
    }

    fun remove(key: String) {
        removeUrl(key)
        removeFile(key)
    }

    fun clear() {
        urlParamsMap!!.clear()
        fileParamsMap!!.clear()
    }

    private fun guessMimeType(path: String): MediaType? {
        var pathClone = path
        val fileNameMap = URLConnection.getFileNameMap()
        pathClone = pathClone.replace("#", "") // 解决文件名中含有#号异常的问题
        var contentType = fileNameMap.getContentTypeFor(pathClone)
        if (contentType == null) {
            contentType = "application/octet-stream"
        }
        return MediaType.parse(contentType)
    }

    /**
     * 文件类型的包装类
     */
    class FileWrapper<T>( // 可以是
        var file: T,
        var fileName: String,
        var contentType: MediaType?,
        responseCallBack: ProgressResponseCallBack?
    ) {
        var fileSize: Long = 0
        var responseCallBack: ProgressResponseCallBack?
        override fun toString(): String {
            return "FileWrapper{countent=$file, fileName='$fileName, " +
                "contentType=$contentType, fileSize=$fileSize}"
        }

        init {
            if (file is File) {
                fileSize = (file as File).length()
            } else if (file is ByteArray) {
                fileSize = (file as ByteArray).size.toLong()
            }
            this.responseCallBack = responseCallBack
        }
    }

    override fun toString(): String {
        val result = StringBuilder()
        for ((key, value) in urlParamsMap!!) {
            if (result.isNotEmpty()) result.append("&")
            result.append(key).append("=").append(value)
        }
        for ((key, value) in fileParamsMap!!) {
            if (result.isNotEmpty()) result.append("&")
            result.append(key).append("=").append(value)
        }
        return result.toString()
    }
}
