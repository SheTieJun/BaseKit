package me.shetj.base.network.sse

import kotlinx.coroutines.flow.MutableSharedFlow
import me.shetj.base.ktx.logI
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources


object SseClient {


    val shareFlow = MutableSharedFlow<String>()

    private var source: EventSource? = null

    fun start(url: String) {
        "start SseClient".logI("SseClient")
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url(url)
            .build()

        val listener: EventSourceListener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                "建立连接".logI("SseClient")
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                "type:$type\ndata:$data".logI("SseClient")
                if (type == "message") {
                    shareFlow.tryEmit(data)
                }
            }

            override fun onClosed(eventSource: EventSource) {
                "关闭连接".logI("SseClient")
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                "onFailure".logI("SseClient")
            }
        }
        val factory: EventSource.Factory = EventSources.createFactory(client)
        source = factory.newEventSource(request, listener)
    }

    fun close() {
        source?.cancel()
    }
}