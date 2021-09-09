package me.shetj.base.base

import androidx.annotation.MainThread
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 *  自己控制的生命周期和协程 类似actvity的基类
 *  生命周期 + 协程作业用域
 *
 * 自身[LifecycleOwner] + 自身[CoroutineScope]
 */
abstract class AbLifecycleWithCopeComponent : LifecycleWithCopeComponent {

    override val ktScope: DefCoroutineScope by ktScopeWithLife(lifecycle)

    private val lifecycleRegistry : LifecycleRegistry by lazy { LifecycleRegistry(getOwner()) }

    init {
        initLifecycle()
    }

    private fun initLifecycle() {
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                onClear()
            }
        })
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }


    private fun getOwner():LifecycleOwner{
        return this
    }

    override fun onClear() {

    }

    override fun onCreate() {
        if (lifecycle is LifecycleRegistry) {
            (lifecycle as LifecycleRegistry).apply {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)){
                    currentState = Lifecycle.State.CREATED
                }
            }
        }
    }

    override fun onStart() {
        if (lifecycle is LifecycleRegistry) {
            (lifecycle as LifecycleRegistry).apply {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)){
                    handleLifecycleEvent(Lifecycle.Event.ON_START) //更新状态，并且通知观察者
                }
            }
        }
    }

    override  fun onResume(){
        if (lifecycle is LifecycleRegistry) {
            (lifecycle as LifecycleRegistry).apply {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)){
                    handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                }
            }
        }
    }


    override fun onDeStory() {
        if (lifecycle is LifecycleRegistry) {
            (lifecycle as LifecycleRegistry).apply {
                handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            }
        }
    }


    fun launchWhenCreated(block: suspend CoroutineScope.() -> Unit): Job = ktScope.launch {
        lifecycle.whenCreated(block)
    }


    fun launchWhenStarted(block: suspend CoroutineScope.() -> Unit): Job = ktScope.launch {
        lifecycle.whenStarted(block)
    }

}


interface LifecycleWithCopeComponent : KtScopeComponent, LifecycleOwner {

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