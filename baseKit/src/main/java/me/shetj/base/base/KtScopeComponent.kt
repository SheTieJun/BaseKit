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
package me.shetj.base.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.shetj.base.BaseKit

/**
 * class: KtScopeComponent{
 *      override val ktScope: DefCoroutineScope by defScope()
 * }
 */
interface KtScopeComponent {
    val ktScope: CoroutineScope

    fun onScopeDestroy(){
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
