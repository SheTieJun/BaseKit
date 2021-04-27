package me.shetj.base.base

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * 生命周期 + 协程作业用域
 * [LifecycleOwner] + [CoroutineScope]
 */
abstract class AbLifecycleWithCopeComponent : LifecycleWithCopeComponent {

    override val ktScope: DefCoroutineScope by this.defLifeOwnerScope()

    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        initLifecycle()
    }

    private fun initLifecycle() {
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                onClear()
            }
        })
        if (lifecycle is LifecycleRegistry) {
            (lifecycle as LifecycleRegistry).apply {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)){
                    currentState = Lifecycle.State.CREATED
                }
            }
        }
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override fun onClear() {

    }

    override fun onStart() {
        if (lifecycle is LifecycleRegistry) {
            (lifecycle as LifecycleRegistry).apply {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)){
                    currentState = Lifecycle.State.STARTED
                }
            }
        }
    }

    override  fun onResume(){
        if (lifecycle is LifecycleRegistry) {
            (lifecycle as LifecycleRegistry).apply {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)){
                    currentState = Lifecycle.State.RESUMED
                }
            }
        }
    }


    override fun onDeStory() {
        if (lifecycle is LifecycleRegistry) {
            (lifecycle as LifecycleRegistry).apply {
                currentState = Lifecycle.State.DESTROYED
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


interface LifecycleWithCopeComponent : KTScopeComponent, LifecycleOwner {

    /**
     * when LifecycleEvent == [Lifecycle.Event.ON_DESTROY]
     */
    fun onClear()

    fun onStart()

    fun onResume()

    fun onDeStory()

}