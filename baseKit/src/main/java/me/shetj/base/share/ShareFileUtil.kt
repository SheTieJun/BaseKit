package me.shetj.base.share

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import me.shetj.base.R
import timber.log.Timber
import java.io.File

@Suppress("DEPRECATION")
object ShareFileUtil {
    private const val TAG = "Share"

    private const val FILE_SELECT_CODE = 3001

    fun Activity.openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.base_select_file)), FILE_SELECT_CODE)
            overridePendingTransition(0, 0)
        } catch (ex: Exception) {
            // Potentially direct the user to the Market with OnProgressChangeListener Dialog
            Toast.makeText(this, getString(R.string.base_please_install_file_manager), Toast.LENGTH_SHORT).show()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) : Uri? {
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
           return  data.data
        }
        return null
    }


    /**
     * Get file uri
     * @param context context
     * @param shareContentType shareContentType [ShareContentType]
     * @param file file
     * @return Uri
     */
    fun getFileUri(context: Context?, @ShareContentType shareContentType: String?, file: File?): Uri? {
        var shareType = shareContentType
        if (context == null) {
            Timber.tag(TAG).e("getFileUri current activity is null.")
            return null
        }
        if (file == null || !file.exists()) {
            Timber.tag(TAG).e("getFileUri file is null or not exists.")
            return null
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Timber.tag(TAG).e("getFileUri miss WRITE_EXTERNAL_STORAGE permission.")
            return null
        }
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file)
        } else {
            if (TextUtils.isEmpty(shareType)) {
                shareType = "*/*"
            }
            when (shareType) {
                ShareContentType.IMAGE -> uri = getImageContentUri(context, file)
                ShareContentType.VIDEO -> uri = getVideoContentUri(context, file)
                ShareContentType.AUDIO -> uri = getAudioContentUri(context, file)
                ShareContentType.FILE -> uri = getFileContentUri(context, file)
                else -> {
                }
            }
        }
        if (uri == null) {
            uri = forceGetFileUri(file)
        }
        return uri
    }

    /**
     * uri convert to file real path, don't support custom FileProvider
     * @param context context
     * @param uri uri
     * @return path
     */
    fun getFileRealPath(context: Context?, uri: Uri?): String? {
        if (context == null) {
            Timber.tag(TAG).e("getFileRealPath current activity is null.")
            return null
        }
        if (uri == null) {
            Timber.tag(TAG).e("getFileRealPath uri is null.")
            return null
        }
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), id.toLong())
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val contentUri: Uri = when (split[0]) {
                    "image" -> {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    else -> {
                        MediaStore.Files.getContentUri("external")
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * forceGetFileUri
     * @param shareFile shareFile
     * @return Uri
     */
    @SuppressLint("DiscouragedPrivateApi")
    private fun forceGetFileUri(shareFile: File): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                @SuppressLint("PrivateApi") val rMethod = StrictMode::class.java.getDeclaredMethod("disableDeathOnFileUriExposure")
                rMethod.invoke(null)
            } catch (e: Exception) {
                Timber.tag(TAG).e( Log.getStackTraceString(e))
            }
        }
        return Uri.parse("file://" + shareFile.absolutePath)
    }

    /**
     * getFileContentUri
     * @param context context
     * @param file file
     * @return Uri
     */
    private fun getFileContentUri(context: Context, file: File): Uri? {
        val volumeName = "external"
        val filePath = file.absolutePath
        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        var uri: Uri? = null
        val cursor = context.contentResolver.query(MediaStore.Files.getContentUri(volumeName), projection,
                MediaStore.Images.Media.DATA + "=? ", arrayOf(filePath), null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                uri = MediaStore.Files.getContentUri(volumeName, id.toLong())
            }
            cursor.close()
        }
        return uri
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context context
     * @param imageFile imageFile
     * @return content Uri
     */
    private fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=? ", arrayOf(filePath), null)
        var uri: Uri? = null
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                val baseUri = Uri.parse("content://media/external/images/media")
                uri = Uri.withAppendedPath(baseUri, "" + id)
            }
            cursor.close()
        }
        if (uri == null) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, filePath)
            uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }
        return uri
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context context
     * @param videoFile videoFile
     * @return content Uri
     */
    private fun getVideoContentUri(context: Context, videoFile: File): Uri? {
        var uri: Uri? = null
        val filePath = videoFile.absolutePath
        val cursor = context.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Video.Media._ID),
                MediaStore.Video.Media.DATA + "=? ", arrayOf(filePath), null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                val baseUri = Uri.parse("content://media/external/video/media")
                uri = Uri.withAppendedPath(baseUri, "" + id)
            }
            cursor.close()
        }
        if (uri == null) {
            val values = ContentValues()
            values.put(MediaStore.Video.Media.DATA, filePath)
            uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        }
        return uri
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context context
     * @param audioFile audioFile
     * @return content Uri
     */
    private fun getAudioContentUri(context: Context, audioFile: File): Uri? {
        var uri: Uri? = null
        val filePath = audioFile.absolutePath
        val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Audio.Media._ID),
                MediaStore.Audio.Media.DATA + "=? ", arrayOf(filePath), null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                val baseUri = Uri.parse("content://media/external/audio/media")
                uri = Uri.withAppendedPath(baseUri, "" + id)
            }
            cursor.close()
        }
        if (uri == null) {
            val values = ContentValues()
            values.put(MediaStore.Audio.Media.DATA, filePath)
            uri = context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
        }
        return uri
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

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     * The context.
     * @param uri
     * The Uri to query.
     * @param selection
     * (Optional) Filter used in the query.
     * @param selectionArgs
     * (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(context: Context, uri: Uri, selection: String?,
                              selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs,
                    null)
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
            }
        } finally {
            cursor?.close()
        }
        return null
    }
}