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