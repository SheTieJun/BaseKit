package me.shetj.base.tools.app

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import me.shetj.base.BaseKit
import me.shetj.base.ktx.logD

object TimeCostKit {
    fun <T> timedAsync(
        name: String,
        tag: String = BaseKit.TAG,
        scope: CoroutineScope = BaseKit.applicationScope,
        block: suspend () -> T
    ): Deferred<T> {
        return scope.async {
            val start = SystemClock.elapsedRealtime()
            try {
                block()
            } finally {
                "$name cost=${SystemClock.elapsedRealtime() - start}ms".logD(tag)
            }
        }
    }

    fun timedAsyncCost(
        name: String,
        tag: String = BaseKit.TAG,
        scope: CoroutineScope = BaseKit.applicationScope,
        block: suspend () -> Unit
    ): Deferred<Long> {
        return scope.async {
            val start = SystemClock.elapsedRealtime()
            var cost = 0L
            try {
                block()
            } finally {
                cost = SystemClock.elapsedRealtime() - start
                "$name cost=${cost}ms".logD(tag)
            }
            cost
        }
    }

    fun <T> timedAsyncIO(
        name: String,
        tag: String = BaseKit.TAG,
        scope: CoroutineScope = BaseKit.applicationScope,
        block: suspend () -> T
    ): Deferred<T> {
        return scope.async(Dispatchers.IO) {
            val start = SystemClock.elapsedRealtime()
            try {
                block()
            } finally {
                "$name cost=${SystemClock.elapsedRealtime() - start}ms".logD(tag)
            }
        }
    }

    fun timedAsyncCostIO(
        name: String,
        tag: String = BaseKit.TAG,
        scope: CoroutineScope = BaseKit.applicationScope,
        block: suspend () -> Unit
    ): Deferred<Long> {
        return scope.async(Dispatchers.IO) {
            val start = SystemClock.elapsedRealtime()
            var cost = 0L
            try {
                block()
            } finally {
                cost = SystemClock.elapsedRealtime() - start
                "$name cost=${cost}ms".logD(tag)
            }
            cost
        }
    }
}
