package me.shetj.base.lifecycle

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
open class AbLifecycleCopeComponent : LifecycleCopeComponent {

    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(getOwner())

    init {
        initLifecycle()
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val ktScope: LifecycleCoroutineScope = lifecycleRegistry.coroutineScope

    private fun initLifecycle() {
        lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    onClear()
                }
            }
        )
    }

    private fun getOwner(): LifecycleOwner {
        return this
    }

    @CallSuper
    override fun onClear() {
    }

    @CallSuper
    override fun onCreate() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    @CallSuper
    override fun onStart() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    @CallSuper
    override fun onPause() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    @CallSuper
    override fun onStop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    @CallSuper
    override fun onResume() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    @CallSuper
    override fun onDeStory() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
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
    fun onPause()

    @MainThread
    fun onStop()

    @MainThread
    fun onDeStory()
}
