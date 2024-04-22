package me.shetj.base.ktx

import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import me.shetj.base.coroutine.DispatcherProvider
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T, O> T.withIO(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(DispatcherProvider.io()) {
        return@withContext action(this@withIO)
    }

suspend fun <T, O> T.withMain(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(DispatcherProvider.main()) {
        return@withContext action(this@withMain)
    }

suspend fun <T, O> T.withDef(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(DispatcherProvider.default()) {
        return@withContext action(this@withDef)
    }

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    action: suspend CoroutineScope.() -> Unit
) = viewModelScope.launch(context, block = action)

fun ComponentActivity.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    action: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(context, block = action)

fun Fragment.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    action: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(context, block = action)


/**
 * 重试机制
 */
suspend fun <T> retryDo(
    times: Int = Int.MAX_VALUE,
    initialDelay: Long = 100, // 0.1 second
    maxDelay: Long = 1000, // 1 second
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block() // last attempt
}

fun <T> Flow<T>.flowWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = callbackFlow {
    lifecycle.repeatOnLifecycle(minActiveState) {
        this@flowWithLifecycle.collect {
            send(it)
        }
    }
    close()
}

private suspend fun <T> runTimeout(
    timeout: Long,
    block: suspend () -> T
): Result<T> {
    return runCatching {
        if (timeout <= 0L) {
            block()
        } else {
            withTimeout(timeout) {
                block()
            }
        }
    }
}
