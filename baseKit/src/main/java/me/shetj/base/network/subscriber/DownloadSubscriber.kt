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
package me.shetj.base.network.subscriber

import android.content.Context
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.text.TextUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import me.shetj.base.network.callBack.DownloadProgressCallBack
import me.shetj.base.network.callBack.NetCallBack
import me.shetj.base.network.exception.ApiException
import okhttp3.ResponseBody
import timber.log.Timber

class DownloadSubscriber(
    private val path: String?,
    private val name: String?,
    val callBack: NetCallBack<*>
) : BaseSubscriber<ResponseBody>(callBack.context) {
    private var lastRefreshUiTime: Long
    public override fun onStart() {
        super.onStart()
        callBack.onStart()
    }

    override fun onComplete() {
        /* if (mCallBack != null) {
            mCallBack.onCompleted();
        }*/
    }

    override fun onError(e: ApiException) {
        finallyError(e)
    }

    override fun onNext(t: ResponseBody) {
        writeResponseBodyToDisk(path, name, callBack.context, t)
    }

    private fun writeResponseBodyToDisk(
        path: String?,
        name: String?,
        context: Context?,
        body: ResponseBody
    ): Boolean {
        // HttpLog.d("contentType:>>>>" + body.contentType().toString());
        var savePath: String? = path
        var saveName = name
        if (!TextUtils.isEmpty(saveName)) { // text/html; charset=utf-8
            val type: String
            if (!saveName!!.contains(".")) {
                type = body.contentType().toString()
                fileSuffix = when (type) {
                    APK_CONTENTTYPE -> {
                        ".apk"
                    }
                    PNG_CONTENTTYPE -> {
                        ".png"
                    }
                    JPG_CONTENTTYPE -> {
                        ".jpg"
                    }
                    else -> {
                        "." + body.contentType()!!.subtype()
                    }
                }
                saveName += fileSuffix
            }
        } else {
            saveName = System.currentTimeMillis().toString() + fileSuffix
        }
        if (savePath == null) {
            savePath = context!!.getExternalFilesDir(DIRECTORY_DOWNLOADS)
                .toString() + File.separator + saveName
        } else {
            val file = File(savePath)
            if (!file.exists()) {
                file.mkdirs()
            }
            savePath = savePath + File.separator + saveName
            savePath = savePath.replace("//".toRegex(), "/")
        }
        return try {
            val futureStudioIconFile = File(savePath)
            /* if (!futureStudioIconFile.exists()) {
                futureStudioIconFile.createNewFile();
            }*/
            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                // byte[] fileReader = new byte[2048];
                val fileReader = ByteArray(1024 * 128)
                val fileSize: Long = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                val callBack = callBack
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    // 下载进度
                    val progress = fileSizeDownloaded * 1.0f / fileSize
                    val curTime = System.currentTimeMillis()
                    // 每200毫秒刷新一次数据,防止频繁更新进度
                    if (curTime - lastRefreshUiTime >= 200 || progress == 1.0f) {
                        val finalFileSizeDownloaded = fileSizeDownloaded
                        Observable.just(finalFileSizeDownloaded)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (callBack is DownloadProgressCallBack<*>) {
                                    callBack.update(
                                        finalFileSizeDownloaded,
                                        fileSize,
                                        finalFileSizeDownloaded == fileSize
                                    )
                                }
                            }) { }
                        lastRefreshUiTime = System.currentTimeMillis()
                    }
                }
                outputStream.flush()
                Timber.i("file downloaded: $fileSizeDownloaded of $fileSize")
                // final String finalName = name;
                val finalPath: String = savePath
                Observable.just(finalPath).observeOn(AndroidSchedulers.mainThread()).subscribe({
                    if (callBack is DownloadProgressCallBack<*>) {
                        callBack.onComplete(finalPath)
                    }
                }) { }
                true
            } catch (e: IOException) {
                finallyError(e)
                false
            } finally {
                outputStream?.close()
                inputStream?.close()
            }
        } catch (e: IOException) {
            finallyError(e)
            false
        }
    }

    private fun finallyError(e: Exception) {
        Observable.just(ApiException(e, 100)).observeOn(AndroidSchedulers.mainThread())
            .doOnNext(callBack::onError)
            .subscribe()
    }

    companion object {
        private const val APK_CONTENTTYPE = "application/vnd.android.package-archive"
        private const val PNG_CONTENTTYPE = "image/png"
        private const val JPG_CONTENTTYPE = "image/jpg"

        // private static String TEXT_CONTENTTYPE = "text/html; charset=utf-8";
        private var fileSuffix = ""
    }

    init {
        lastRefreshUiTime = System.currentTimeMillis()
    }
}
