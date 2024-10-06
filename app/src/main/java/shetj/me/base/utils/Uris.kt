package shetj.me.base.utils

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import java.util.Locale

object Uris {
    const val SCHEME_HTTP: String = "http"
    const val SCHEME_HTTPS: String = "https"

    /** Returns true if the Uri is an 'http:' one.  */
    fun isHttp(uri: Uri): Boolean {
        return SCHEME_HTTP == uri.scheme
    }

    /** Returns true if the Uri is an 'https:' one.  */
    fun isHttps(uri: Uri): Boolean {
        return SCHEME_HTTPS == uri.scheme
    }

    /** Returns true if the Uri is a remote (http/s) one.  */
    fun isRemote(uri: Uri): Boolean {
        val scheme = uri.scheme
        return SCHEME_HTTP == scheme || SCHEME_HTTPS == scheme
    }

    /** Returns true if the Uri is a local (on-device) one.  */
    fun isLocal(uri: Uri): Boolean {
        val scheme = uri.scheme
        return ContentResolver.SCHEME_FILE == scheme || ContentResolver.SCHEME_CONTENT == scheme
    }

    /** Returns true if the Uri is a 'content:' one.  */
    fun isContentUri(uri: Uri): Boolean {
        return ContentResolver.SCHEME_CONTENT == uri.scheme
    }

    /** Returns true if the Uri is a 'file:' one.  */
    fun isFileUri(uri: Uri): Boolean {
        return ContentResolver.SCHEME_FILE == uri.scheme
    }

    /**
     * Extract a content-type from the given [Uri] by mapping its file extension to a known
     * mime-type. This is based on the Uri only, it doesn't open any connection.
     */
    fun extractContentType(uri: Uri): String? {
        // Note: MimeTypeMap.getFileExtensionFromUrl(path); fails on unusual characters in path.
        val name = uri.lastPathSegment
        if (name != null) {
            val dot = name.lastIndexOf('.')
            if (dot >= 0) {
                val extension = name.substring(dot + 1).lowercase(Locale.getDefault())
                return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
        }
        return null
    }

    /**
     * Extracts a file name from the given [Uri] - either its last segment of the whole Uri.
     * This is based on the Uri only, it doesn't open any connection.
     */
    fun extractFileName(uri: Uri): String {
        var name = uri.lastPathSegment
        if (name == null) {
            name = uri.toString()
        }
        return name
    }

}