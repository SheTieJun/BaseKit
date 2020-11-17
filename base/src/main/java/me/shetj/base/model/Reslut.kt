package me.shetj.base.model


/**
 * 学习源码！
 */
@Suppress("UNCHECKED_CAST")
@SuppressWarnings("Unchecked")
class Result<out T> constructor(val value: Any?) {
    // discovery

    val isLoading: Boolean get() = value ==null

    val isSuccess: Boolean get() = value !is Failure


    val isFailure: Boolean get() = value is Failure


    inline fun getOrNull(): T? =
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
                is Failure -> value.toString() // "Failure($exception)"
                else -> "Success($value)"
            }

    // companion with constructors


    companion object {
        fun <T> success(value: T): Result<T> =
                Result(value)

        fun <T> failure(exception: Throwable): Result<T> =
                Result(createFailure(exception))

        fun loading() = Result<Any>(null)
    }

    data class Failure(
            @JvmField
            val exception: Throwable
    )  {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }
}

fun createFailure(exception: Throwable): Result.Failure =
        Result.Failure(exception)


fun Result<*>.throwOnFailure() {
    if (value is Result.Failure) throw value.exception
}


inline fun <R> runCatching(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

 
inline fun <T, R> T.runCatching(block: T.() -> R):Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

// -- extensions ---


fun <T> Result<T>.getOrThrow(): T {
    throwOnFailure()
    return value as T
}

 
inline fun <R, T : R>  Result<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    return when (val exception = exceptionOrNull()) {
        null -> value as T
        else -> onFailure(exception)
    }
}

 
inline fun <R, T : R>  Result<T>.getOrDefault(defaultValue: R): R {
    if (isFailure) return defaultValue
    return value as T
}

 
inline fun <R, T>  Result<T>.fold(
        onSuccess: (value: T) -> R,
        onFailure: (exception: Throwable) -> R
): R {
    return when (val exception = exceptionOrNull()) {
        null -> onSuccess(value as T)
        else -> onFailure(exception)
    }
}

// transformation
 
inline fun <R, T>  Result<T>.map(transform: (value: T) -> R):  Result<R> {
   
    return when {
        isSuccess -> Result.success(transform(value as T))
        else -> Result(value)
    }
}

 
inline fun <R, T>  Result<T>.mapCatching(transform: (value: T) -> R):  Result<R> {
    return when {
        isSuccess -> runCatching { transform(value as T) }
        else -> Result(value)
    }
}

 
inline fun <R, T : R> Result<T>.recover(transform: (exception: Throwable) -> R):  Result<R> {
   
    return when (val exception = exceptionOrNull()) {
        null -> this
        else ->  Result.success(transform(exception))
    }
}
 
inline fun <R, T : R>  Result<T>.recoverCatching(transform: (exception: Throwable) -> R):  Result<R> {
    val value = value // workaround for inline classes BE bug
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> runCatching { transform(exception) }
    }
}


inline fun <T>  Result<T>.onFailure(action: (exception: Throwable) -> Unit):  Result<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}


inline fun <T>  Result<T>.onSuccess(action: (value: T) -> Unit):  Result<T> {
    if (isSuccess) action(value as T)
    return this
}
