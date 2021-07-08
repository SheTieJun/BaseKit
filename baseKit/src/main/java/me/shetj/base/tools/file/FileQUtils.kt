package me.shetj.base.tools.file

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.annotation.WorkerThread
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

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
    fun Context.delFile(uri:Uri){
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


}