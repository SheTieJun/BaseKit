## 构建带生命周期的协程作用域


### 自定义的生命周期+ 协程作用域
1. 通过继承`AbLifecycleWithCopeComponent`
3. 实现`LifecycleWithCopeComponent`

### 得到一个协程的作用域
1. 通过 `defLifeOwnerScope` 获取可以绑定生命周期的作用域
2. 通过`defScope`,可以选择是否绑定生命周期

```
 
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
```

```
interface KTScopeComponent {
    val ktScope: DefCoroutineScope
}
```

```
abstract class DefCoroutineScope : CoroutineScope {
    abstract fun register(lifecycle: Lifecycle)
}
```

```
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
```

```
/**
 * 可以通过[DefCoroutineScope.register] 进行绑定生命收起，也可以不判定
 */
fun defScope() = lazy {
    CoroutineScopeImpl(SupervisorJob() + Dispatchers.Main.immediate + S.handler)
}
```

```
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


```

```
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
```


### ProcessLifecycle
```kotlin
        // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
 implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")
```
全局的生命周期
