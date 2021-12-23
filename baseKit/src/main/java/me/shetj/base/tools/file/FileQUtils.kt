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

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

/**
 * 安卓Q 文件基础操作
 */
object FileQUtils {
//
//    /**
//     * 搜索文档
//     */
//    fun AppCompatActivity.searchTypeFile(type:String = "image/*",callback:ActivityResultCallback<Uri?>): ActivityResultLauncher<String> {
//        return registerForActivityResult(ActivityResultContracts.GetContent(),callback)
//    }
//
//    /**
//     * 搜索多类型的文件
//     */
//    fun AppCompatActivity.searchFile(vararg type:String ,callback:ActivityResultCallback<Uri?>){
//        registerForActivityResult(ActivityResultContracts.OpenDocument(),callback).launch(type)
//    }
//
//
//    /**
//     * 创建文件
//     */
//    fun AppCompatActivity.createFile( fileName: String,callback:ActivityResultCallback<Uri>){
//        registerForActivityResult(ActivityResultContracts.CreateDocument(), callback).launch(fileName)
//    }

    /**
     * 删除文件
     */
    fun Context.delFile(uri: Uri) {
        DocumentsContract.deleteDocument(contentResolver, uri)
    }

    @WorkerThread
    fun writeDataToDocument(context: Context, uri: Uri, content: String) {
        try {
            context.contentResolver.openFileDescriptor(uri, "w").use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor?.fileDescriptor).use { fos ->
                    fos.write(content.toByteArray())
                    fos.close()
                    parcelFileDescriptor?.close()
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 兼容Q
     */
    fun getFileByUri(activity: Activity, uri: Uri): File? {
        var path: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return uriToFileQ(activity, uri)
        } else if ("file" == uri.scheme) {
            path = uri.encodedPath
            if (path != null) {
                path = Uri.decode(path)
                val cr = activity.contentResolver
                val buff = StringBuffer()
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                    .append("'$path'").append(")")
                val cur = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA
                    ),
                    buff.toString(),
                    null,
                    null
                )
                var index = 0
                var dataIdx: Int
                cur!!.moveToFirst()
                while (!cur.isAfterLast) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID)
                    index = cur.getInt(index)
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    path = cur.getString(dataIdx)
                    cur.moveToNext()
                }
                cur.close()
                if (index != 0) {
                    val u = Uri.parse("content://media/external/images/media/$index")
                    if (path != null) {
                        return File(path)
                    }
                }
            }
        } else if ("content" == uri.scheme) {
            // 4.2.2以后
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = activity.contentResolver.query(uri, proj, null, null, null)
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = cursor.getString(columnIndex)
            }
            cursor.close()
            if (path != null) {
                return File(path)
            }
        } else {
            return null
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uriToFileQ(context: Context, uri: Uri): File? =
        if (uri.scheme == ContentResolver.SCHEME_FILE)
            File(requireNotNull(uri.path))
        else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            // 把文件保存到沙盒
            val start = uri.path?.lastIndexOf(".") ?: -1
            //把文件保存到沙盒
            val contentResolver = context.contentResolver
            val displayName = if (start > 0) {
                //因为存在部分文件的扩展名称获取错误，所以先用文件原有的扩展名称，在使用
                "${System.currentTimeMillis()}${Random.nextInt(0, 9999)}.${
                    uri.path?.substring(start+1) ?: MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(contentResolver.getType(uri))
                }"
            } else {
                "${System.currentTimeMillis()}${Random.nextInt(0, 9999)}.${
                    MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(contentResolver.getType(uri))
                }"
            }
            val ios = contentResolver.openInputStream(uri)
            if (ios != null) {
                File("${context.cacheDir.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }
            } else null
        } else null
}
