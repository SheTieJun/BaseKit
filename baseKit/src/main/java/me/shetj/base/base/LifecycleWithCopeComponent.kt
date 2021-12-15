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

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlinx.coroutines.CoroutineScope


/*** 自己控制的生命周期和协程 类似actvity的基类
 *  生命周期 + 协程作业用域
 *
 * * 自身[LifecycleOwner] + 自身[CoroutineScope]
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


    private fun getOwner(): LifecycleOwner {
        return this
    }

    override fun onClear() {

    }

    override fun onCreate() {
        if (lifecycle is LifecycleRegistry) {
            (lifecycle as LifecycleRegistry).apply {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)){
                    handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
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