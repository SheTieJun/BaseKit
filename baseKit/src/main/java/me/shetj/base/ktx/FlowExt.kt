package me.shetj.base.ktx

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*



fun <T> Flow<T>.throttleFirst(duration: Long = 1000L) = this.throttleFirstImpl(duration)


fun <T> Flow<T>.throttleLast(duration: Long = 1000L) = this.sample(duration)

internal fun <T> Flow<T>.throttleFirstImpl(periodMillis: Long): Flow<T> {
    return flow {
        var lastTime = 0L
        collect { value ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime >= periodMillis) {
                lastTime = currentTime
                emit(value)
            }
        }
    }
}




@ExperimentalCoroutinesApi
fun <T> Flow<T>.throttleLatest(duration: Long = 1000L) = this.throttleLatestImpl(duration)

@ExperimentalCoroutinesApi
internal fun <T> Flow<T>.throttleLatestImpl(periodMillis: Long): Flow<T> {
    return channelFlow {
        var lastValue: T?
        var timer: Timer? = null
        onCompletion { timer?.cancel() }
        collect { value ->
            lastValue = value
            if (timer == null) {
                timer = Timer()
                timer?.scheduleAtFixedRate(
                    object : TimerTask() {
                        override fun run() {
                            val valueN = lastValue
                            lastValue = null
                            if (valueN != null) {
                                launch {
                                    send(valueN as T)
                                }
                            } else {
                                timer?.cancel()
                                timer = null
                            }
                        }
                    },
                    0,
                    periodMillis
                )
            }
        }
    }
}