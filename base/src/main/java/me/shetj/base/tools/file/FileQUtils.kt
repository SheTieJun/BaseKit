package me.shetj.base.tools.file

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 * 安卓Q 文件基础操作
 */
object FileQUtils {

    /**
     * 搜索文档
     */
    fun AppCompatActivity.searchTypeFile(type:String = "image/*",callback:ActivityResultCallback<Uri?>){
        registerForActivityResult(ActivityResultContracts.GetContent(),callback).launch(type)
    }

    /**
     * 搜索多类型的文件
     */
    fun AppCompatActivity.searchFile(vararg type:String ,callback:ActivityResultCallback<Uri?>){
        registerForActivityResult(ActivityResultContracts.OpenDocument(),callback).launch(type)
    }


    /**
     * 创建文件
     */
    fun AppCompatActivity.createFile( fileName: String,callback:ActivityResultCallback<Uri?>){
        registerForActivityResult(ActivityResultContracts.CreateDocument(), callback).launch(fileName)
    }


    /**
     * 删除文件
     */
    fun Context.delFile(uri:Uri){
        DocumentsContract.deleteDocument(contentResolver, uri)
    }


}