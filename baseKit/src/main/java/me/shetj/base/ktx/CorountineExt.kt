package me.shetj.base.ktx

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


suspend inline fun <T> doOnIO(crossinline action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO) {
    return@withContext this.action()
}

suspend inline fun <T> doOnMain(crossinline action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Main) {
    return@withContext action()
}


suspend inline fun <T> doOnDef(crossinline action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Default) {
    return@withContext action()
}


suspend inline fun <T> doOnUnconfined(crossinline action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Unconfined) {
    return@withContext action()
}

suspend inline fun <T> doOnContext(context: CoroutineContext = EmptyCoroutineContext, crossinline action: suspend CoroutineScope.() -> T) = withContext(context) {
    return@withContext action()
}

inline fun ViewModel.launch(crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch {
        action()
    }
}

inline fun AppCompatActivity.launch(crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch {
        action()
    }
}

inline fun AppCompatActivity.runOnCreated(crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenCreated {
        action()
    }
}

inline fun AppCompatActivity.runOnResumed(crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenResumed {
        action()
    }
}

inline fun AppCompatActivity.runOnStarted(crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenStarted {
        action()
    }
}


inline fun Fragment.launch(crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch {
        action()
    }
}

inline fun Fragment.runOnCreated(crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenCreated {
        action()
    }
}

inline fun Fragment.runOnResumed(crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenResumed {
        action()
    }
}

inline fun Fragment.runOnStarted(crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenStarted {
        action()
    }
}

/**
 * 重试机制
 */
suspend fun <T> retryDo(
        times: Int = Int.MAX_VALUE,
        initialDelay: Long = 100, // 0.1 second
        maxDelay: Long = 1000,    // 1 second
        factor: Double = 2.0,
        block: suspend () -> T): T
{
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
    return block()  // last attempt
}