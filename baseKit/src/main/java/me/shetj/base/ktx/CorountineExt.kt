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
package me.shetj.base.ktx

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

suspend inline fun <T> doOnIO(crossinline action: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.IO) {
        return@withContext this.action()
    }

suspend inline fun <T> doOnMain(crossinline action: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Main) {
        return@withContext action()
    }

suspend inline fun <T> doOnDef(crossinline action: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Default) {
        return@withContext action()
    }

suspend inline fun <T> doOnUnconfined(crossinline action: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Unconfined) {
        return@withContext action()
    }

suspend inline fun <T> doOnContext(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline action: suspend CoroutineScope.() -> T
) = withContext(context) {
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


private suspend fun  <T> runTimeout(
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