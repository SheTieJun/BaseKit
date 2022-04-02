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
@file:Suppress("UNCHECKED_CAST")

package me.shetj.base.network_coroutine

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import java.io.File
import me.shetj.base.network.exception.ApiException

//region 下载状态相关
typealias OnError = (ApiException) -> Unit
typealias download_error = suspend (Throwable) -> Unit
typealias download_process = suspend (downloadedSize: Long, length: Long, progress: Float) -> Unit
typealias download_success = suspend (uri: File) -> Unit

/**
 * 学习源码！
 */
@Suppress("UNCHECKED_CAST")
@SuppressWarnings("Unchecked")
class HttpResult<out T> constructor(val value: Any?) {

    val isSuccess: Boolean get() = value !is Failure && value !is Progress

    val isFailure: Boolean get() = value is Failure

    val isLoading: Boolean get() = value is Progress

    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    override fun toString(): String =
        when (value) {
            is Failure -> "Failure(${value.exception.message})"
            else -> "Success($value)"
        }

    // companion with constructors

    companion object {
        fun <T> success(value: T): HttpResult<T> =
            HttpResult(value)

        fun <T> failure(exception: Throwable): HttpResult<T> =
            HttpResult(createFailure(exception))

        fun <T> progress(currentLength: Long, length: Long, process: Float): HttpResult<T> =
            HttpResult(createLoading(currentLength, length, process))
    }

    data class Failure(
        val exception: Throwable
    ) {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }

    data class Progress(val currentLength: Long, val length: Long, val process: Float)
}

private fun createFailure(exception: Throwable): HttpResult.Failure =
    HttpResult.Failure(exception)

private fun createLoading(currentLength: Long, length: Long, process: Float) =
    HttpResult.Progress(currentLength, length, process)

fun HttpResult<*>.throwOnFailure() {
    if (value is HttpResult.Failure) throw value.exception
}

inline fun <R> runCatching(block: () -> R): HttpResult<R> {
    return try {
        HttpResult.success(block())
    } catch (e: Throwable) {
        HttpResult.failure(ApiException.handleException(e))
    }
}

inline fun <T, R> T.runCatching(block: T.() -> R): HttpResult<R> {
    return try {
        HttpResult.success(block())
    } catch (e: Throwable) {
        HttpResult.failure(ApiException.handleException(e))
    }
}

// -- extensions ---

fun <T> HttpResult<T>.getOrThrow(): T {
    throwOnFailure()
    return value as T
}

inline fun <R, T : R> HttpResult<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    return when (val exception = exceptionOrNull()) {
        null -> value as T
        else -> onFailure(exception)
    }
}

fun <R, T : R> HttpResult<T>.getOrDefault(defaultValue: R): R {
    if (isFailure) return defaultValue
    return value as T
}

inline fun <R, T> HttpResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R
): R {
    return when (val exception = exceptionOrNull()) {
        null -> onSuccess(value as T)
        else -> onFailure(exception)
    }
}

inline fun <R, T> HttpResult<T>.fold(
    onSuccess: (value: T) -> R,
    onLoading: (loading: HttpResult.Progress) -> R,
    onFailure: (exception: Throwable?) -> R
): R {
    return when {
        isFailure -> {
            onFailure(exceptionOrNull())
        }
        isLoading -> {
            onLoading(value as HttpResult.Progress)
        }
        else -> {
            onSuccess(value as T)
        }
    }
}

// transformation

inline fun <R, T> HttpResult<T>.map(transform: (value: T) -> R): HttpResult<R> {

    return when {
        isSuccess -> HttpResult.success(transform(value as T))
        else -> HttpResult(value)
    }
}

inline fun <R, T> HttpResult<T>.mapCatching(transform: (value: T) -> R): HttpResult<R> {
    return when {
        isSuccess -> runCatching { transform(value as T) }
        else -> HttpResult(value)
    }
}

inline fun <R, T : R> HttpResult<T>.recover(
    transform: (exception: Throwable) -> R
): HttpResult<R?> {

    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> HttpResult.success(transform(exception))
    }
}

/**
 * 把异常转成可以用的数据
 */
inline fun <R, T : R> HttpResult<T>.recoverCatching(
    transform: (exception: Throwable) -> R
): HttpResult<R> {
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> runCatching { transform(exception) }
    }
}

inline fun <T> HttpResult<T>.onFailure(action: (exception: Throwable) -> Unit): HttpResult<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}

inline fun <T> HttpResult<T>.onSuccess(action: OnSuccess<T>): HttpResult<T> {
    if (isSuccess) action(value as T)
    return this
}

typealias OnLoading = HttpResult.Progress.() -> Unit

typealias OnSuccess<T> = T.() -> Unit

typealias OnFailure = Throwable?.() -> Unit

class HttpResultCallBack<T> {
    var onLoading: OnLoading? = null
    var onSuccess: OnSuccess<T?>? = null
    var onFailure: OnFailure? = null
}

fun <T> LiveData<HttpResult<T>>.observeChange(
    owner: LifecycleOwner,
    block: HttpResultCallBack<T>.() -> Unit
) {
    HttpResultCallBack<T>().apply(block).let { callBack ->
        this.observe(owner) {
            when {
                it.isSuccess -> {
                    callBack.onSuccess?.invoke(it.getOrNull())
                }
                it.isLoading -> {
                    callBack.onLoading?.invoke(value as HttpResult.Progress)
                }
                else -> {
                    callBack.onFailure?.invoke(it.exceptionOrNull())
                }
            }
        }
    }
}
