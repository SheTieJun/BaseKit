package me.shetj.base.base

import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope

/*** 自己控制的生命周期和协程 类似activity的基类
 *  生命周期 + 协程作业用域
 *
 * * 自身[LifecycleOwner] + 自身[CoroutineScope]
 */
abstract class AbLifecycleCopeComponent : LifecycleCopeComponent {
    init {
        initLifecycle()
    }

    private val lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(getOwner()) }

    override val ktScope: LifecycleCoroutineScope  = lifecycle.coroutineScope

    private fun initLifecycle() {
        lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    onClear()
                }
            }
        )
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private fun getOwner(): LifecycleOwner {
        return this
    }

    @CallSuper
    override fun onClear() {
    }

    @CallSuper
    override fun onCreate() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    @CallSuper
    override fun onStart() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    @CallSuper
    override fun onResume() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    @CallSuper
    override fun onDeStory() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }
}

interface LifecycleCopeComponent : KtScopeComponent, LifecycleOwner {

    @MainThread
    fun onCreate()

    /**
     * when LifecycleEvent == [Lifecycle.Event.ON_DESTROY]
     */
    @MainThread
    fun onClear()

    @MainThread
    fun onStart()

    @MainThread
    fun onResume()

    @MainThread
    fun onDeStory()
}
