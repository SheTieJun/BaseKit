package me.shetj.base.network.kt

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun String.toRequestBody(): RequestBody {
    return toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
}


fun ByteArray.toRequestBody(): RequestBody {
    return toRequestBody("application/octet-stream".toMediaTypeOrNull())
}

fun String?.createJson(): RequestBody {
    checkNotNull(this) { "json not null!" }
    return toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

fun String?.createFile(): RequestBody {
    checkNotNull(this) { "name not null!" }
    return toRequestBody("multipart/form-data; charset=utf-8".toMediaTypeOrNull())
}

fun File?.createFile(): RequestBody {
    checkNotNull(this) { "file not null!" }
    return this.asRequestBody("multipart/form-data; charset=utf-8".toMediaTypeOrNull())
}

fun File?.createImage(): RequestBody {
    checkNotNull(this) { "file not null!" }
    return this.asRequestBody("image/jpg; charset=utf-8".toMediaTypeOrNull())
}

fun File?.createPart(name: String): Part {
    checkNotNull(this) { "file not null!" }
    return Part.createFormData(name, this.name, createFile())
}
