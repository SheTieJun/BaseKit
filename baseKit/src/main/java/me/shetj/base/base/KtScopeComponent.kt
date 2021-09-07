package me.shetj.base.base

import androidx.lifecycle.*
import kotlinx.coroutines.*
import me.shetj.base.S
import kotlin.coroutines.CoroutineContext

/**
 * class: KtScopeComponent{
 *      override val ktScope: DefCoroutineScope by defScope()
 * }
 */
interface KtScopeComponent {
    val ktScope: DefCoroutineScope
}

abstract class ABKtScopeComponent :KtScopeComponent{
    override val ktScope: DefCoroutineScope by defScope()
}


fun ktScopeWithLife(lifecycle: Lifecycle) = lazy {
    LifecycleCoroutineScopeImpl(lifecycle,
            SupervisorJob() + Dispatchers.Main.immediate + S.handler)
            .also {
                it.register(lifecycle)
            }
}

fun Lifecycle.ktLifeScope() = lazy {
     ktScopeWithLife(this)
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




interface LifecycleKtScopeComponent {
    val lifeKtScope: LifecycleCoroutineScope
}


abstract class DefCoroutineScope : CoroutineScope {
    abstract fun register(lifecycle: Lifecycle)
}


/**
 * 结合外部的LifecycleOwner 使用
 */
open class LifecycleCoroutineScope(override val coroutineContext: CoroutineContext) : CoroutineScope, LifecycleEventObserver {

    open fun register(lifecycle: Lifecycle) {
        launch(Dispatchers.Main.immediate) {
            if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
                coroutineContext.cancel()
            }
            if (lifecycle.currentState >= Lifecycle.State.INITIALIZED) {
                lifecycle.addObserver(this@LifecycleCoroutineScope)
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

/**
 * who use this need implements [LifecycleOwner]
 */
fun LifecycleOwner.defLifeOwnerScope() = lazy {
    LifecycleCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + S.handler)
            .also {
                it.register(lifecycle)
            }
}