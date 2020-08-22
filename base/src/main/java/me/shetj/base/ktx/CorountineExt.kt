package me.shetj.base.ktx

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext



suspend inline fun <T> doOnContext(context: CoroutineContext = EmptyCoroutineContext, crossinline action: () -> T) = withContext(context) {
    return@withContext action()
}

suspend inline fun <T> runOnIO(crossinline action:suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO) {
    return@withContext this.action()
}

suspend inline fun <T> runOnMain(crossinline action:suspend CoroutineScope.() -> T) = withContext(Dispatchers.Main) {
    return@withContext action()
}


suspend inline fun <T> runOnDef(crossinline action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Default) {
    return@withContext action()
}


suspend inline fun <T> runOnUn(crossinline action:suspend CoroutineScope.() -> T) = withContext(Dispatchers.Unconfined) {
    return@withContext action()
}

suspend inline fun <T> runOnContext(context: CoroutineContext = EmptyCoroutineContext, crossinline action: suspend CoroutineScope.() -> T) = withContext(context) {
    return@withContext action()
}


suspend inline fun <T, R> T.onContext(context: CoroutineContext = EmptyCoroutineContext, crossinline action: T.() -> R) = withContext(context) {
    return@withContext action()
}


suspend inline fun <T> CoroutineScope.io(crossinline action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO) {
    return@withContext action()
}


suspend inline fun <T> CoroutineScope.main(crossinline action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Main) {
    return@withContext action()
}


suspend inline fun <T> CoroutineScope.default(crossinline action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Default) {
    return@withContext action()
}


suspend inline fun <T> CoroutineScope.unconfined(crossinline action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Unconfined) {
    return@withContext action()
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

inline fun AppCompatActivity.runOnResumed (crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenResumed {
        action()
    }
}

inline fun AppCompatActivity.runOnStarted (crossinline action: suspend CoroutineScope.() -> Unit): Job {
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

inline fun Fragment.runOnResumed (crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenResumed {
        action()
    }
}

inline fun Fragment.runOnStarted (crossinline action: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launchWhenStarted {
        action()
    }
}