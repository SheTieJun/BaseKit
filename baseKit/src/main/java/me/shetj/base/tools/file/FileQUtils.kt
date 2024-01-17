package me.shetj.base.tools.file

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore.Audio
import android.provider.MediaStore.Images
import android.provider.MediaStore.Images.ImageColumns
import android.provider.MediaStore.MediaColumns
import android.provider.MediaStore.Video
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import me.shetj.base.ktx.getFileProviderAuthority
import me.shetj.base.ktx.md5
import java.io.File
import java.io.FileOutputStream

/**
 * 安卓Q 文件基础操作
 */
object FileQUtils {

    /**
     * 根据Uri获取文件绝对路径，解决Android4.4以上版本Uri转换 兼容Android 10
     *
     * @param context
     * @param uri
     * @param filename10IsTemp 是否是临时文件
     */
    fun getFileAbsolutePath(context: Context?, uri: Uri?, filename10IsTemp: Boolean = true): String? {
        if (context == null || uri == null) {
            return null
        }
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            return getRealFilePath(context, uri)
        }
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT && VERSION.SDK_INT < VERSION_CODES.Q && DocumentsContract.isDocumentUri(
                context,
                uri
            )
        ) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "")
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = Images.Media.EXTERNAL_CONTENT_URI
                    }

                    "video" -> {
                        contentUri = Video.Media.EXTERNAL_CONTENT_URI
                    }

                    "audio" -> {
                        contentUri = Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = MediaColumns._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return contentUri?.let { getDataColumn(context, it, selection, selectionArgs) }
            }
        } // MediaStore (and general)
        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            return uriToFileApiQ(context, uri, filename10IsTemp)
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else {
                getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    // 此方法 只能用于4.4以下的版本
    private fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) {
            return null
        }
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val projection = arrayOf(ImageColumns.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * Android 10 以上适配 另一种写法
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("Range")
    private fun getFileFromContentUri(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        val filePath: String
        val filePathColumn = arrayOf(MediaColumns.DATA, MediaColumns.DISPLAY_NAME)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            uri,
            filePathColumn,
            null,
            null,
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
                return filePath
            } catch (e: Exception) {
            } finally {
                cursor.close()
            }
        }
        return ""
    }

    /**
     * Android 10 以上适配
     * @param context
     * @param uri
     * @return
     */
    @RequiresApi(api = VERSION_CODES.Q)
    private fun uriToFileApiQ(context: Context, uri: Uri, filename10IsTemp: Boolean): String? {
        return if (uri.scheme == ContentResolver.SCHEME_FILE) {
            File(requireNotNull(uri.path)).path
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            // 把文件保存到沙盒,算是临时文件
            val contentResolver = context.contentResolver
            val displayName = getFileName(context, uri, filename10IsTemp).replace("/", "_") // 修复复制文件时，文件名中包含/导致的文件复制失败
            val ios = contentResolver.openInputStream(uri)
            if (ios != null) {
                File("${context.cacheDir.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }.path
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun getFileName(context: Context, uri: Uri, filename10IsTemp: Boolean = true): String {
        var fileName: String? = null
        val contentResolver = context.contentResolver
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor.use { cu ->
                if (cu != null && cu.moveToFirst()) {
                    fileName = cu.getString(cu.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (fileName == null) {
            fileName = uri.lastPathSegment
        }
        if (!filename10IsTemp && fileName != null) {
            fileName = System.currentTimeMillis().toString() + fileName
        }
        return fileName ?: "${uri.toString().md5}.${
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(uri))
        }"
    }

    /**
     * Take file permission
     * 获取长时间的文件读取权限
     * @param uri
     */
    fun takeFilePermission(context: Context, uri: Uri) {
        val flag: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flag)
    }

    fun getUriForFile(context: Context, file: File): Uri? {
        return FileProvider.getUriForFile(context, context.getFileProviderAuthority(), file)
    }
}

/**
 * 删除文件
 */
fun Context.delFile(uri: Uri) {
    DocumentsContract.deleteDocument(contentResolver, uri)
}
