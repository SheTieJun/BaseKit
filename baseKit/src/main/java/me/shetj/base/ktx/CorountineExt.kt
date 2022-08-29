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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

suspend fun <T> doOnIO(action: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.IO) {
        return@withContext this.action()
    }

suspend fun <T> doOnMain(action: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Main) {
        return@withContext action()
    }

suspend fun <T> doOnDef(action: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Default) {
        return@withContext action()
    }

suspend fun <T> doOnUnconfined(action: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Unconfined) {
        return@withContext action()
    }


suspend fun <T, O> T.doOnIO(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(Dispatchers.IO) {
        return@withContext this.action(this@doOnIO)
    }

suspend fun <T, O> T.doOnMain(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(Dispatchers.Main) {
        return@withContext action(this@doOnMain)
    }

suspend fun <T, O> T.doOnDef(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(Dispatchers.Default) {
        return@withContext action(this@doOnDef)
    }

suspend fun <T, O> T.doOnUnconfined(action: suspend CoroutineScope.(t: T) -> O) =
    withContext(Dispatchers.Unconfined) {
        return@withContext action(this@doOnUnconfined)
    }


fun ViewModel.launch(action: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch {
        action()
    }
}

fun AppCompatActivity.launch(action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch {
        action()
    }
}

fun Fragment.launch(action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch {
        action()
    }
}

fun Fragment.runOnCreated(action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenCreated {
        action()
    }
}

fun Fragment.runOnResumed(action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenResumed {
        action()
    }
}

fun Fragment.runOnStarted(action: suspend CoroutineScope.() -> Unit): Job {
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
