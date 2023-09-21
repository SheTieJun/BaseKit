package shetj.me.base.contentprovider

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull

data class MediaItem(
    val id: Long,
    val contentUri: Uri,
    val data: String?,
    val mimeType: String?,
    val duration: Long?
)

fun fetchGalleryImages(
    context: Context,
    orderBy: String,
    orderAscending: Boolean,
    limit: Int = 20,
    offset: Int = 0
): List<MediaItem> {
    val galleryImageUrls = mutableListOf<MediaItem>()
    val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.TITLE,
        MediaStore.Video.Media.DURATION
    )
    val whereCondition = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )
    createCursor(
        contentResolver = context.contentResolver,
        collection = collection,
        projection = projection,
        whereCondition = whereCondition,
        selectionArgs = selectionArgs,
        orderBy = orderBy,
        orderAscending = orderAscending,
        limit = limit,
        offset = offset
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            if (idIndex < 0) continue
            val id = cursor.getLong(idIndex)
            galleryImageUrls.add(
                MediaItem(
                    id = id,
                    contentUri = ContentUris.withAppendedId(collection, id),
                    data = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)),
                    mimeType = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)),
                    duration = cursor.getLongOrNull(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                )
            )
        }
    }
    return galleryImageUrls
}

private fun createCursor(
    contentResolver: ContentResolver,
    collection: Uri,
    projection: Array<String>,
    whereCondition: String,
    selectionArgs: Array<String>,
    orderBy: String,
    orderAscending: Boolean,
    limit: Int = 20,
    offset: Int = 0
): Cursor? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
        val selection = createSelectionBundle(whereCondition, selectionArgs, orderBy, orderAscending, limit, offset)
        contentResolver.query(collection, projection, selection, null)
    }
    else -> {
        val orderDirection = if (orderAscending) "ASC" else "DESC"
        var order = when (orderBy) {
            "ALPHABET" -> "${MediaStore.Audio.Media.TITLE}, ${MediaStore.Audio.Media.ARTIST} $orderDirection"
            else -> "${MediaStore.Audio.Media.DATE_ADDED} $orderDirection"
        }
        order += " LIMIT $limit OFFSET $offset"
        contentResolver.query(collection, projection, whereCondition, selectionArgs, order)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun createSelectionBundle(
    whereCondition: String,
    selectionArgs: Array<String>,
    orderBy: String,
    orderAscending: Boolean,
    limit: Int = 20,
    offset: Int = 0
): Bundle = Bundle().apply {
    // Limit & Offset
    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
    // Sort function
    when (orderBy) {
        "ALPHABET" -> putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.Files.FileColumns.TITLE))
        else -> putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.Files.FileColumns.DATE_ADDED))
    }
    // Sorting direction
    val orderDirection =
        if (orderAscending) ContentResolver.QUERY_SORT_DIRECTION_ASCENDING else ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
    putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, orderDirection)
    // Selection
    putString(ContentResolver.QUERY_ARG_SQL_SELECTION, whereCondition)
    putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
}