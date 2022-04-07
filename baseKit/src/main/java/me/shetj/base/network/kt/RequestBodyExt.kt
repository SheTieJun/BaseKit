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
package me.shetj.base.network.kt

import java.io.File
import okhttp3.MediaType
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody

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
