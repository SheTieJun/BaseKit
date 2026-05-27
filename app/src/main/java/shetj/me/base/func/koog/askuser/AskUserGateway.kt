package shetj.me.base.func.koog.askuser

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class AskUserRequest(
    val id: String,
    val question: String,
    val options: List<String>
)

interface AskUserGateway {
    val requests: SharedFlow<AskUserRequest>

    suspend fun ask(question: String, options: List<String>): String

    fun answer(id: String, value: String)

    fun cancel(id: String)
}

class AskUserGatewayImpl : AskUserGateway {

    private val _requests = MutableSharedFlow<AskUserRequest>(extraBufferCapacity = 1)
    override val requests: SharedFlow<AskUserRequest> = _requests

    private val pending = ConcurrentHashMap<String, CompletableDeferred<String>>()

    override suspend fun ask(question: String, options: List<String>): String {
        val id = UUID.randomUUID().toString()
        val deferred = CompletableDeferred<String>()
        pending[id] = deferred
        _requests.emit(
            AskUserRequest(
                id = id,
                question = question,
                options = options
            )
        )
        return try {
            deferred.await()
        } finally {
            pending.remove(id)
        }
    }

    override fun answer(id: String, value: String) {
        pending.remove(id)?.complete(value)
    }

    override fun cancel(id: String) {
        pending.remove(id)?.complete("")
    }
}

