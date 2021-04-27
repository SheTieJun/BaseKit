package me.shetj.base.base

import androidx.lifecycle.*
import kotlinx.coroutines.*
import me.shetj.base.S
import kotlin.coroutines.CoroutineContext

/**
 *
 */
interface KTScopeComponent {
    val ktScope: DefCoroutineScope
}


abstract class DefCoroutineScope : CoroutineScope {
    abstract fun register(lifecycle: Lifecycle)
}

/**
 * who use this need implements [LifecycleOwner]
 */
fun LifecycleOwner.defLifeOwnerScope() = lazy {
    LifecycleCoroutineScopeImpl(lifecycle,
            SupervisorJob() + Dispatchers.Main.immediate + S.handler)
            .also {
                it.register(lifecycle)
            }
}


/**
 * 可以通过[DefCoroutineScope.register] 进行绑定生命收起，也可以不判定
 */
fun defScope() = lazy {
    CoroutineScopeImpl(SupervisorJob() + Dispatchers.Main.immediate + S.handler)
}


class CoroutineScopeImpl(
        override val coroutineContext: CoroutineContext
) : DefCoroutineScope(), LifecycleEventObserver {

    override fun register(lifecycle: Lifecycle) {
        launch(Dispatchers.Main.immediate) {
            if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
                coroutineContext.cancel()
            }
            if (lifecycle.currentState >= Lifecycle.State.INITIALIZED) {
                lifecycle.addObserver(this@CoroutineScopeImpl)
            } else {
                coroutineContext.cancel()
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (source.lifecycle.currentState <= Lifecycle.State.DESTROYED) {
            source.lifecycle.removeObserver(this)
            coroutineContext.cancel()
        }
    }
}


class LifecycleCoroutineScopeImpl(
        val lifecycle: Lifecycle,
        override val coroutineContext: CoroutineContext
) : DefCoroutineScope(), LifecycleEventObserver {

    init {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            coroutineContext.cancel()
        }
    }

    override fun register(lifecycle: Lifecycle) {
        launch(Dispatchers.Main.immediate) {
            if (lifecycle.currentState >= Lifecycle.State.INITIALIZED) {
                lifecycle.addObserver(this@LifecycleCoroutineScopeImpl)
            } else {
                coroutineContext.cancel()
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (lifecycle.currentState <= Lifecycle.State.DESTROYED) {
            lifecycle.removeObserver(this)
            coroutineContext.cancel()
        }
    }
}