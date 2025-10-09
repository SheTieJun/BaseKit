package shetj.me.base.websocket

import android.content.Context
import android.widget.Toast
import okhttp3.Response
import okio.ByteString

/**
 * WebSocket使用示例
 * 展示如何正确使用WebSocket模块的各种功能
 */
class WebSocketExample(private val context: Context) {
    
    private var webSocketManager: WebSocketManager? = null
    
    /**
     * 基础使用示例
     */
    fun basicUsageExample() {
        // 1. 创建配置
        val config = WebSocketConfig.default("wss://echo.websocket.org")
        
        // 2. 创建WebSocket管理器
        webSocketManager = WebSocketManager(config)
        
        // 3. 添加监听器
        webSocketManager?.addListener("example", object : SimpleWebSocketListener() {
            override fun onConnected(response: Response) {
                showToast("WebSocket连接成功")
                // 连接成功后发送消息
                webSocketManager?.sendMessage("Hello WebSocket!")
            }
            
            override fun onTextMessage(text: String) {
                showToast("收到消息: $text")
            }
            
            override fun onClosed(code: Int, reason: String) {
                showToast("连接已关闭: $reason")
            }
            
            override fun onFailure(throwable: Throwable, response: Response?) {
                showToast("连接失败: ${throwable.message}")
            }
        })
        
        // 4. 开始连接
        webSocketManager?.connect()
    }
    
    /**
     * 生产环境使用示例
     */
    fun productionUsageExample() {
        // 创建生产环境配置
        val config = WebSocketConfig.forProduction("wss://api.example.com/websocket")
            .copy(
                headers = mapOf<String, String>(
                    "Authorization" to "Bearer your-token",
                    "User-Agent" to "YourApp/1.0"
                ),
                heartbeatMessage = """{"type":"ping","timestamp":${System.currentTimeMillis()}}""",
                maxReconnectAttempts = 5
            )
        
        webSocketManager = WebSocketManager(config)
        
        // 添加完整的监听器
        webSocketManager?.addListener("production", object : WebSocketStateListener {
            override fun onStateChanged(state: WebSocketState, reason: String?) {
                WebSocketLogger.logStateChange(WebSocketState.DISCONNECTED, state, reason)
                when (state) {
                    WebSocketState.CONNECTING -> showToast("正在连接...")
                    WebSocketState.CONNECTED -> showToast("连接成功")
                    WebSocketState.RECONNECTING -> showToast("正在重连...")
                    WebSocketState.FAILED -> showToast("连接失败")
                    WebSocketState.CLOSED -> showToast("连接已关闭")
                    WebSocketState.DISCONNECTED -> showToast("已断开连接")
                }
            }
            
            override fun onConnected(response: Response) {
                WebSocketLogger.i(message = "WebSocket连接成功，协议: ${response.protocol}")
                
                // 发送认证消息
                val authMessage = """
                    {
                        "type": "auth",
                        "token": "your-auth-token",
                        "timestamp": ${System.currentTimeMillis()}
                    }
                """.trimIndent()
                webSocketManager?.sendMessage(authMessage)
            }
            
            override fun onTextMessage(text: String) {
                WebSocketLogger.logMessageReceived(text)
                handleMessage(text)
            }
            
            override fun onBinaryMessage(bytes: ByteString) {
                WebSocketLogger.d(message = "收到二进制消息，大小: ${bytes.size}")
                handleBinaryMessage(bytes)
            }
            
            override fun onClosed(code: Int, reason: String) {
                WebSocketLogger.w(message = "WebSocket连接关闭: $code - $reason")
            }
            
            override fun onFailure(throwable: Throwable, response: Response?) {
                val exception = WebSocketException.fromThrowable(throwable)
                WebSocketLogger.logException(exception, "WebSocket连接")
                handleConnectionFailure(exception)
            }
            
            override fun onSendMessageFailed(message: String, throwable: Throwable) {
                WebSocketLogger.e(message = "消息发送失败: $message", throwable = throwable)
                // 可以选择重新发送或者记录到本地队列
            }
        })
        
        webSocketManager?.connect()
    }
    
    /**
     * 开发环境使用示例
     */
    fun developmentUsageExample() {
        // 配置开发环境日志
        WebSocketLogger.configure(
            enabled = true,
            level = WebSocketLogger.LogLevel.DEBUG
        )
        
        // 创建开发环境配置
        val config = WebSocketConfig.forDevelopment("ws://localhost:8080/websocket")
        
        webSocketManager = WebSocketManager(config)
        
        // 添加调试监听器
        webSocketManager?.addListener("debug", object : SimpleWebSocketListener() {
            override fun onStateChanged(state: WebSocketState, reason: String?) {
                WebSocketLogger.d(message = "状态变化: $state, 原因: $reason")
            }
            
            override fun onConnected(response: Response) {
                WebSocketLogger.i(message = "开发环境连接成功")
                
                // 发送测试消息
                repeat(5) { index ->
                    webSocketManager?.sendMessage("测试消息 #$index")
                }
            }
            
            override fun onTextMessage(text: String) {
                WebSocketLogger.d(message = "收到测试消息: $text")
            }
        })
        
        webSocketManager?.connect()
    }
    
    /**
     * SSL配置示例
     */
    fun sslConfigurationExample() {
        // 注意：在生产环境中不要忽略SSL错误
        val config = WebSocketConfig(
            url = "wss://secure.example.com/websocket",
            ignoreSSLErrors = false, // 生产环境设为false
            connectTimeout = 15_000L,
            headers = mapOf("Origin" to "https://yourapp.com")
        )
        
        webSocketManager = WebSocketManager(config)
        
        webSocketManager?.addListener("ssl", object : SimpleWebSocketListener() {
            override fun onFailure(throwable: Throwable, response: Response?) {
                when (val exception = WebSocketException.fromThrowable(throwable)) {
                    is WebSocketException.SSLException -> {
                        showToast("SSL证书验证失败，请检查证书配置")
                        WebSocketLogger.e(message = "SSL错误", throwable = exception)
                    }
                    else -> {
                        showToast("连接失败: ${exception.message}")
                    }
                }
            }
        })
        
        webSocketManager?.connect()
    }
    
    /**
     * 处理接收到的消息
     */
    private fun handleMessage(message: String) {
        try {
            // 这里可以解析JSON消息并处理不同类型的消息
            when {
                message.contains("\"type\":\"pong\"") -> {
                    // 处理心跳响应
                    WebSocketLogger.d(message = "收到心跳响应")
                }
                message.contains("\"type\":\"notification\"") -> {
                    // 处理通知消息
                    showToast("收到通知")
                }
                message.contains("\"type\":\"data\"") -> {
                    // 处理数据消息
                    WebSocketLogger.i(message = "收到数据消息")
                }
                else -> {
                    WebSocketLogger.d(message = "收到未知类型消息: $message")
                }
            }
        } catch (e: Exception) {
            WebSocketLogger.e(message = "处理消息异常", throwable = e)
        }
    }
    
    /**
     * 处理二进制消息
     */
    private fun handleBinaryMessage(bytes: ByteString) {
        // 处理二进制数据，例如图片、文件等
        WebSocketLogger.d(message = "处理二进制消息，大小: ${bytes.size}")
    }
    
    /**
     * 处理连接失败
     */
    private fun handleConnectionFailure(exception: WebSocketException) {
        when (exception) {
            is WebSocketException.NetworkException -> {
                showToast("网络连接失败，请检查网络设置")
            }
            is WebSocketException.TimeoutException -> {
                showToast("连接超时，请稍后重试")
            }
            is WebSocketException.AuthenticationException -> {
                showToast("认证失败，请重新登录")
            }
            is WebSocketException.ServerException -> {
                showToast("服务器错误: ${exception.statusCode}")
            }
            else -> {
                showToast("连接失败: ${exception.message}")
            }
        }
    }
    
    /**
     * 发送不同类型的消息示例
     */
    fun sendMessageExamples() {
        webSocketManager?.let { manager ->
            // 发送文本消息
            manager.sendMessage("Hello World!")
            
            // 发送JSON消息
            val jsonMessage = """
                {
                    "type": "chat",
                    "content": "Hello from Android!",
                    "timestamp": ${System.currentTimeMillis()}
                }
            """.trimIndent()
            manager.sendMessage(jsonMessage)
            
            // 发送二进制消息
            val binaryData = "Hello Binary".toByteArray()
            manager.sendMessage(ByteString.of(*binaryData))
        }
    }
    
    /**
     * 连接管理示例
     */
    fun connectionManagementExample() {
        webSocketManager?.let { manager ->
            // 检查连接状态
            when (manager.getState()) {
                WebSocketState.CONNECTED -> {
                    showToast("当前已连接")
                }
                WebSocketState.CONNECTING -> {
                    showToast("正在连接中...")
                }
                WebSocketState.DISCONNECTED -> {
                    showToast("未连接，开始连接")
                    manager.connect()
                }
                else -> {
                    showToast("连接状态: ${manager.getState()}")
                }
            }
            
            // 手动断开连接
            // manager.disconnect(1000, "用户主动断开")
        }
    }
    
    /**
     * 清理资源
     */
    fun cleanup() {
        webSocketManager?.let { manager ->
            manager.removeListener("example")
            manager.removeListener("production")
            manager.removeListener("debug")
            manager.removeListener("ssl")
            manager.destroy()
        }
        webSocketManager = null
    }
    
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}