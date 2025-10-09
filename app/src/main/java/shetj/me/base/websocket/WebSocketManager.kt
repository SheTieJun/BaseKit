package shetj.me.base.websocket

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.*
import okio.ByteString
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * WebSocket管理器
 * 提供完整的WebSocket连接、消息发送接收、自动重连等功能
 */
class WebSocketManager(private val config: WebSocketConfig) {
    
    companion object {
        private const val TAG = "WebSocketManager"
        private const val NORMAL_CLOSURE_STATUS = 1000
        private const val ABNORMAL_CLOSURE_STATUS = 1006
    }
    
    // 当前连接状态
    private val currentState = AtomicReference(WebSocketState.DISCONNECTED)
    
    // WebSocket连接实例
    private val webSocket = AtomicReference<WebSocket?>()
    
    // OkHttp客户端
    private lateinit var okHttpClient: OkHttpClient
    
    // 事件监听器列表
    private val listeners = ConcurrentHashMap<String, WebSocketStateListener>()
    
    // 消息队列
    private val messageQueue = LinkedBlockingQueue<String>(config.messageQueueSize)
    
    // 心跳相关
    private val heartbeatExecutor = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "WebSocket-Heartbeat").apply { isDaemon = true }
    }
    private var heartbeatFuture: ScheduledFuture<*>? = null
    
    // 重连相关
    private val reconnectExecutor = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "WebSocket-Reconnect").apply { isDaemon = true }
    }
    private var reconnectFuture: ScheduledFuture<*>? = null
    private val reconnectAttempts = AtomicInteger(0)
    private val isReconnecting = AtomicBoolean(false)
    
    // 主线程Handler
    private val mainHandler = Handler(Looper.getMainLooper())
    
    // 是否手动关闭
    private val isManualClose = AtomicBoolean(false)
    
    init {
        config.validate()
        initOkHttpClient()
    }
    
    /**
     * 初始化OkHttp客户端
     */
    private fun initOkHttpClient() {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(config.writeTimeout, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
        
        // 配置SSL
        if (config.url.startsWith("wss://")) {
            if (config.ignoreSSLErrors) {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })
                
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }
            } else if (config.sslSocketFactory != null && config.trustManager != null) {
                builder.sslSocketFactory(config.sslSocketFactory, config.trustManager)
            }
        }
        
        okHttpClient = builder.build()
    }
    
    /**
     * 连接WebSocket
     */
    fun connect() {
        if (currentState.get() == WebSocketState.CONNECTING || 
            currentState.get() == WebSocketState.CONNECTED) {
            Log.w(TAG, "WebSocket已连接或正在连接中")
            return
        }
        
        isManualClose.set(false)
        updateState(WebSocketState.CONNECTING)
        
        try {
            val requestBuilder = Request.Builder().url(config.url)
            
            // 添加请求头
            config.headers.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }
            
            val request = requestBuilder.build()
            val ws = okHttpClient.newWebSocket(request, createWebSocketListener())
            webSocket.set(ws)
            
            Log.i(TAG, "开始连接WebSocket: ${config.url}")
            
        } catch (e: Exception) {
            Log.e(TAG, "连接WebSocket失败", e)
            updateState(WebSocketState.FAILED)
            notifyFailure(e, null)
            scheduleReconnect()
        }
    }
    
    /**
     * 断开WebSocket连接
     * @param code 关闭代码
     * @param reason 关闭原因
     */
    fun disconnect(code: Int = NORMAL_CLOSURE_STATUS, reason: String = "主动断开") {
        isManualClose.set(true)
        stopHeartbeat()
        stopReconnect()
        
        webSocket.get()?.let { ws ->
            ws.close(code, reason)
            Log.i(TAG, "主动断开WebSocket连接: $reason")
        }
        
        updateState(WebSocketState.DISCONNECTED)
    }
    
    /**
     * 发送文本消息
     * @param message 文本消息
     * @return 是否发送成功
     */
    fun sendMessage(message: String): Boolean {
        val ws = webSocket.get()
        if (ws == null || currentState.get() != WebSocketState.CONNECTED) {
            Log.w(TAG, "WebSocket未连接，消息加入队列: $message")
            
            // 如果队列已满，移除最旧的消息
            if (!messageQueue.offer(message)) {
                messageQueue.poll()
                messageQueue.offer(message)
            }
            return false
        }
        
        return try {
            val success = ws.send(message)
            if (success) {
                Log.d(TAG, "发送消息成功: $message")
            } else {
                Log.w(TAG, "发送消息失败: $message")
                notifySendMessageFailed(message, RuntimeException("发送失败"))
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "发送消息异常: $message", e)
            notifySendMessageFailed(message, e)
            false
        }
    }
    
    /**
     * 发送二进制消息
     * @param bytes 二进制数据
     * @return 是否发送成功
     */
    fun sendMessage(bytes: ByteString): Boolean {
        val ws = webSocket.get()
        if (ws == null || currentState.get() != WebSocketState.CONNECTED) {
            Log.w(TAG, "WebSocket未连接，无法发送二进制消息")
            return false
        }
        
        return try {
            val success = ws.send(bytes)
            if (success) {
                Log.d(TAG, "发送二进制消息成功，大小: ${bytes.size}")
            } else {
                Log.w(TAG, "发送二进制消息失败")
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "发送二进制消息异常", e)
            false
        }
    }
    
    /**
     * 获取当前连接状态
     */
    fun getState(): WebSocketState = currentState.get()
    
    /**
     * 是否已连接
     */
    fun isConnected(): Boolean = currentState.get() == WebSocketState.CONNECTED
    
    /**
     * 添加事件监听器
     * @param key 监听器标识
     * @param listener 监听器实例
     */
    fun addListener(key: String, listener: WebSocketStateListener) {
        listeners[key] = listener
    }
    
    /**
     * 移除事件监听器
     * @param key 监听器标识
     */
    fun removeListener(key: String) {
        listeners.remove(key)
    }
    
    /**
     * 清除所有监听器
     */
    fun clearListeners() {
        listeners.clear()
    }
    
    /**
     * 销毁WebSocket管理器
     */
    fun destroy() {
        disconnect()
        clearListeners()
        messageQueue.clear()
        
        heartbeatExecutor.shutdown()
        reconnectExecutor.shutdown()
        
        try {
            if (!heartbeatExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                heartbeatExecutor.shutdownNow()
            }
            if (!reconnectExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                reconnectExecutor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            heartbeatExecutor.shutdownNow()
            reconnectExecutor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
    
    /**
     * 创建WebSocket监听器
     */
    private fun createWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.i(TAG, "WebSocket连接成功")
                reconnectAttempts.set(0)
                isReconnecting.set(false)
                updateState(WebSocketState.CONNECTED)
                
                // 发送队列中的消息
                sendQueuedMessages()
                
                // 启动心跳
                startHeartbeat()
                
                // 通知监听器
                notifyConnected(response)
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "收到文本消息: $text")
                notifyTextMessage(text)
            }
            
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "收到二进制消息，大小: ${bytes.size}")
                notifyBinaryMessage(bytes)
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.i(TAG, "WebSocket正在关闭: $code - $reason")
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.i(TAG, "WebSocket已关闭: $code - $reason")
                stopHeartbeat()
                updateState(WebSocketState.CLOSED)
                notifyClosed(code, reason)
                
                if (!isManualClose.get() && config.enableAutoReconnect) {
                    scheduleReconnect()
                }
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket连接失败", t)
                stopHeartbeat()
                updateState(WebSocketState.FAILED)
                notifyFailure(t, response)
                
                if (!isManualClose.get() && config.enableAutoReconnect) {
                    scheduleReconnect()
                }
            }
        }
    }
    
    /**
     * 发送队列中的消息
     */
    private fun sendQueuedMessages() {
        while (messageQueue.isNotEmpty() && isConnected()) {
            val message = messageQueue.poll()
            if (message != null) {
                sendMessage(message)
            }
        }
    }
    
    /**
     * 启动心跳
     */
    private fun startHeartbeat() {
        stopHeartbeat()
        
        heartbeatFuture = heartbeatExecutor.scheduleWithFixedDelay({
            if (isConnected()) {
                sendMessage(config.heartbeatMessage)
            }
        }, config.heartbeatInterval, config.heartbeatInterval, TimeUnit.MILLISECONDS)
        
        Log.d(TAG, "心跳已启动，间隔: ${config.heartbeatInterval}ms")
    }
    
    /**
     * 停止心跳
     */
    private fun stopHeartbeat() {
        heartbeatFuture?.cancel(false)
        heartbeatFuture = null
        Log.d(TAG, "心跳已停止")
    }
    
    /**
     * 安排重连
     */
    private fun scheduleReconnect() {
        if (!config.enableAutoReconnect || isManualClose.get() || isReconnecting.get()) {
            return
        }
        
        val attempts = reconnectAttempts.incrementAndGet()
        if (config.maxReconnectAttempts > 0 && attempts > config.maxReconnectAttempts) {
            Log.w(TAG, "达到最大重连次数: $attempts")
            return
        }
        
        isReconnecting.set(true)
        updateState(WebSocketState.RECONNECTING)
        
        val delay = config.calculateReconnectDelay(attempts)
        Log.i(TAG, "安排第${attempts}次重连，延迟: ${delay}ms")
        
        reconnectFuture = reconnectExecutor.schedule({
            if (!isManualClose.get() && config.enableAutoReconnect) {
                Log.i(TAG, "开始第${attempts}次重连")
                connect()
            }
        }, delay, TimeUnit.MILLISECONDS)
    }
    
    /**
     * 停止重连
     */
    private fun stopReconnect() {
        isReconnecting.set(false)
        reconnectFuture?.cancel(false)
        reconnectFuture = null
        Log.d(TAG, "重连已停止")
    }
    
    /**
     * 更新连接状态
     */
    private fun updateState(newState: WebSocketState, reason: String? = null) {
        val oldState = currentState.getAndSet(newState)
        if (oldState != newState) {
            Log.d(TAG, "状态变化: $oldState -> $newState")
            notifyStateChanged(newState, reason)
        }
    }
    
    // 通知方法
    private fun notifyStateChanged(state: WebSocketState, reason: String?) {
        mainHandler.post {
            listeners.values.forEach { listener ->
                try {
                    listener.onStateChanged(state, reason)
                } catch (e: Exception) {
                    Log.e(TAG, "通知状态变化异常", e)
                }
            }
        }
    }
    
    private fun notifyConnected(response: Response) {
        mainHandler.post {
            listeners.values.forEach { listener ->
                try {
                    listener.onConnected(response)
                } catch (e: Exception) {
                    Log.e(TAG, "通知连接成功异常", e)
                }
            }
        }
    }
    
    private fun notifyTextMessage(text: String) {
        mainHandler.post {
            listeners.values.forEach { listener ->
                try {
                    listener.onTextMessage(text)
                } catch (e: Exception) {
                    Log.e(TAG, "通知文本消息异常", e)
                }
            }
        }
    }
    
    private fun notifyBinaryMessage(bytes: ByteString) {
        mainHandler.post {
            listeners.values.forEach { listener ->
                try {
                    listener.onBinaryMessage(bytes)
                } catch (e: Exception) {
                    Log.e(TAG, "通知二进制消息异常", e)
                }
            }
        }
    }
    
    private fun notifyClosed(code: Int, reason: String) {
        mainHandler.post {
            listeners.values.forEach { listener ->
                try {
                    listener.onClosed(code, reason)
                } catch (e: Exception) {
                    Log.e(TAG, "通知连接关闭异常", e)
                }
            }
        }
    }
    
    private fun notifyFailure(throwable: Throwable, response: Response?) {
        mainHandler.post {
            listeners.values.forEach { listener ->
                try {
                    listener.onFailure(throwable, response)
                } catch (e: Exception) {
                    Log.e(TAG, "通知连接失败异常", e)
                }
            }
        }
    }
    
    private fun notifySendMessageFailed(message: String, throwable: Throwable) {
        mainHandler.post {
            listeners.values.forEach { listener ->
                try {
                    listener.onSendMessageFailed(message, throwable)
                } catch (e: Exception) {
                    Log.e(TAG, "通知发送失败异常", e)
                }
            }
        }
    }
}