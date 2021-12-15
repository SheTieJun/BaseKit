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


package shetj.me.base.utils

import io.reactivex.rxjava3.observers.DefaultObserver
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

abstract class FileDownLoadObserver<T> : DefaultObserver<T>() {
    override fun onNext(t: T) {
        onDownLoadSuccess(t)
    }

    override fun onError(e: Throwable) {
        onDownLoadFail(e)
    }

    //可以重写，具体可由子类实现
    override fun onComplete() {}

    //下载成功的回调
    abstract fun onDownLoadSuccess(t: T)

    //下载失败回调
    abstract fun onDownLoadFail(throwable: Throwable?)

    //下载进度监听
    abstract fun onProgress(progress: Int, total: Long)

    /**
     * 将文件写入本地
     * @param responseBody 请求结果全体
     * @param destFile 目标文件
     * @return 写入完成的文件
     * @throws IOException IO异常
     */
    @Throws(IOException::class)
    fun saveFile(responseBody: ResponseBody, destFile: String): File {
        var `is`: InputStream? = null
        val buf = ByteArray(2048)
        var len: Int
        var fos: FileOutputStream? = null
        return try {
            `is` = responseBody.byteStream()
            val total = responseBody.contentLength()
            var sum: Long = 0
            val file = File(destFile)
            fos = FileOutputStream(file)
            while (`is`.read(buf).also { len = it } != -1) {
                sum += len.toLong()
                fos.write(buf, 0, len)
                val finalSum = sum
                //这里就是对进度的监听回调
                onProgress((finalSum * 100 / total).toInt(), total)
            }
            fos.flush()
            file
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}