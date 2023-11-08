package me.shetj.base.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.shetj.base.BaseKit
import kotlin.coroutines.CoroutineContext

/**
 * class: KtScopeComponent{
 *      override val ktScope: DefCoroutineScope by defScope()
 * }
 */
interface KtScopeComponent {
    val ktScope: CoroutineScope

    fun onScopeDestroy() {
        ktScope.cancel()
    }
}

abstract class ABKtScopeComponent : KtScopeComponent {
    override val ktScope: DefCoroutineScope by defScope()
}

/**
 * 可以通过[DefCoroutineScope.register] 进行绑定生命收起，也可以不判定
 */
fun defScope() = lazy { CoroutineScopeImpl(coroutineContext()) }

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

abstract class DefCoroutineScope : CoroutineScope {
    abstract fun register(lifecycle: Lifecycle)
}

private fun coroutineContext() = SupervisorJob() + Dispatchers.Main.immediate + BaseKit.handler
