package me.shetj.base.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import me.shetj.base.S.handler
import kotlin.coroutines.CoroutineContext

class DefScope(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate + handler) : CoroutineScope