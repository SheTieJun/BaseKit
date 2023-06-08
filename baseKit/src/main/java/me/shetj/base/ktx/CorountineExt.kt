package me.shetj.base.ktx

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

suspend fun <T, O> T.withIO(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(Dispatchers.IO) {
        return@withContext this.action(this@withIO)
    }

suspend fun <T, O> T.withMain(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(Dispatchers.Main) {
        return@withContext action(this@withMain)
    }

suspend fun <T, O> T.withDef(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(Dispatchers.Default) {
        return@withContext action(this@withDef)
    }

suspend fun <T, O> T.withUnconfined(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(Dispatchers.Unconfined) {
        return@withContext action(this@withUnconfined)
    }


fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    action: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launch(context) {
        action()
    }
}

fun FragmentActivity.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    action: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(context) {
        action()
    }
}

fun Fragment.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    action: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launch(context) {
        action()
    }
}

fun Fragment.runOnCreated(
    action: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launchWhenCreated {
        action()
    }
}

fun Fragment.runOnResumed(
    action: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launchWhenResumed {
        action()
    }
}

fun Fragment.runOnStarted(
    action: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launchWhenStarted {
        action()
    }
}


fun <T> Fragment.liveData(
    context: CoroutineContext = lifecycleScope.coroutineContext,
    timeoutInMs: Long = 5000L,
    block: suspend LiveDataScope<T>.() -> Unit
): LiveData<T> {
    return androidx.lifecycle.liveData(context, timeoutInMs, block)
}

fun <T> FragmentActivity.liveData(
    context: CoroutineContext = lifecycleScope.coroutineContext,
    timeoutInMs: Long = 5000L,
    block: suspend LiveDataScope<T>.() -> Unit
): LiveData<T> {
    return androidx.lifecycle.liveData(context, timeoutInMs, block)
}

fun <T> ViewModel.liveData(
    context: CoroutineContext = viewModelScope.coroutineContext,
    timeoutInMs: Long = 5000L,
    block: suspend LiveDataScope<T>.() -> Unit
): LiveData<T> {
    return androidx.lifecycle.liveData(context, timeoutInMs, block)
}

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
