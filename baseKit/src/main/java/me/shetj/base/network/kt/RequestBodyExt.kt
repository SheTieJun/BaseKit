package me.shetj.base.network.kt

import okhttp3.MediaType
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody
import java.io.File

fun String.toRequestBody(): RequestBody {
    return toRequestBody("application/json;charset=utf-8")
}

fun String.toRequestBody(mediaType: String): RequestBody {
    return toRequestBody(MediaType.parse(mediaType))
}

fun String.toRequestBody(mediaType: MediaType?): RequestBody {
    return RequestBody.create(mediaType, this)
}

fun ByteArray.toRequestBody(): RequestBody {
    return RequestBody.create(MediaType.parse("application/octet-stream"), this)
}

fun String?.createJson(): RequestBody {
    checkNotNull(this) { "json not null!" }
    return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), this)
}

fun String?.createFile(): RequestBody {
    checkNotNull(this) { "name not null!" }
    return RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), this)
}

fun File?.createFile(): RequestBody {
    checkNotNull(this) { "file not null!" }
    return RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), this)
}

fun File?.createImage(): RequestBody {
    checkNotNull(this) { "file not null!" }
    return RequestBody.create(MediaType.parse("image/jpg; charset=utf-8"), this)
}

fun File?.createPart(name: String): Part {
    checkNotNull(this) { "file not null!" }
    return Part.createFormData(name, this.name, createFile())
}
