@file:Suppress("InjectDispatcher")

package me.shetj.base.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface CoroutineDispatcherProvider {
    fun io(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
    fun main(): CoroutineDispatcher
}

typealias DispatcherProvider = CoroutineDispatcherProviderImpl

object CoroutineDispatcherProviderImpl : CoroutineDispatcherProvider {
    override fun io(): CoroutineDispatcher = Dispatchers.IO
    override fun default(): CoroutineDispatcher = Dispatchers.Default
    override fun main(): CoroutineDispatcher = Dispatchers.Main
}
