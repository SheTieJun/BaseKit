@file:Suppress("InjectDispatcher")

package me.shetj.base.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

typealias DispatcherProvider = CoroutineDispatcherProviderImpl

interface CoroutineDispatcherProvider {
    fun io(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
    fun main(): CoroutineDispatcher
}

object CoroutineDispatcherProviderImpl : CoroutineDispatcherProvider {
    override fun io(): CoroutineDispatcher = Dispatchers.IO
    override fun default(): CoroutineDispatcher = Dispatchers.Default
    override fun main(): CoroutineDispatcher = Dispatchers.Main
}
