# WebSocket 模块使用指南

这是一个基于 OkHttp 实现的完整 WebSocket 功能模块，提供了稳定的连接管理、自动重连、心跳检测等功能。

## 功能特性

- ✅ 支持 ws:// 和 wss:// 协议
- ✅ 自动重连机制（支持固定延迟和指数退避策略）
- ✅ 心跳检测和保活
- ✅ 消息队列管理
- ✅ 完善的异常处理
- ✅ 线程安全设计
- ✅ 详细的日志记录
- ✅ 灵活的配置选项
- ✅ SSL/TLS 支持

## 快速开始

### 1. 基础使用

```kotlin
// 创建配置
val config = WebSocketConfig.default("wss://echo.websocket.org")

// 创建管理器
val webSocketManager = WebSocketManager(config)

// 添加监听器
webSocketManager.addListener("main", object : SimpleWebSocketListener() {
    override fun onConnected(response: Response) {
        // 连接成功
        webSocketManager.sendMessage("Hello WebSocket!")
    }
    
    override fun onTextMessage(text: String) {
        // 收到文本消息
        println("收到消息: $text")
    }
    
    override fun onFailure(throwable: Throwable, response: Response?) {
        // 连接失败
        println("连接失败: ${throwable.message}")
    }
})

// 开始连接
webSocketManager.connect()
```

### 2. 生产环境配置

```kotlin
val config = WebSocketConfig.forProduction("wss://api.example.com/websocket")
    .copy(
        headers = mapOf(
            "Authorization" to "Bearer your-token",
            "User-Agent" to "YourApp/1.0"
        ),
        heartbeatInterval = 45_000L,
        maxReconnectAttempts = 10,
        reconnectDelayStrategy = WebSocketConfig.ReconnectDelayStrategy.EXPONENTIAL_BACKOFF
    )
```

### 3. 开发环境配置

```kotlin
val config = WebSocketConfig.forDevelopment("ws://localhost:8080/websocket")
    .copy(
        ignoreSSLErrors = true,
        heartbeatInterval = 20_000L
    )
```

## 配置选项

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| url | String | - | WebSocket 服务器地址 |
| connectTimeout | Long | 10000 | 连接超时时间（毫秒） |
| readTimeout | Long | 30000 | 读取超时时间（毫秒） |
| writeTimeout | Long | 30000 | 写入超时时间（毫秒） |
| heartbeatInterval | Long | 30000 | 心跳间隔时间（毫秒） |
| heartbeatMessage | String | "ping" | 心跳消息内容 |
| enableAutoReconnect | Boolean | true | 是否启用自动重连 |
| reconnectInterval | Long | 5000 | 重连间隔时间（毫秒） |
| maxReconnectAttempts | Int | -1 | 最大重连次数（-1表示无限） |
| reconnectDelayStrategy | ReconnectDelayStrategy | FIXED | 重连延迟策略 |
| maxReconnectDelay | Long | 60000 | 最大重连延迟时间（毫秒） |
| headers | Map<String, String> | {} | 请求头 |
| ignoreSSLErrors | Boolean | false | 是否忽略SSL错误（仅测试环境） |
| messageQueueSize | Int | 1000 | 消息队列最大大小 |
| enableCompression | Boolean | false | 是否启用消息压缩 |

## 连接状态

- `DISCONNECTED`: 未连接
- `CONNECTING`: 连接中
- `CONNECTED`: 已连接
- `RECONNECTING`: 重连中
- `FAILED`: 连接失败
- `CLOSED`: 连接关闭

## 监听器接口

```kotlin
interface WebSocketListener {
    fun onStateChanged(state: WebSocketState, reason: String?)
    fun onConnected(response: Response)
    fun onTextMessage(text: String)
    fun onBinaryMessage(bytes: ByteString)
    fun onClosed(code: Int, reason: String)
    fun onFailure(throwable: Throwable, response: Response?)
    fun onSendMessageFailed(message: String, throwable: Throwable)
}
```

## 异常处理

模块提供了详细的异常分类：

- `ConnectionException`: 连接异常
- `NetworkException`: 网络异常
- `TimeoutException`: 超时异常
- `SSLException`: SSL证书异常
- `AuthenticationException`: 认证异常
- `ServerException`: 服务器异常
- `MessageSendException`: 消息发送异常
- `ProtocolException`: 协议异常
- `ConfigurationException`: 配置异常

## 日志配置

```kotlin
// 配置日志级别
WebSocketLogger.configure(
    enabled = true,
    level = WebSocketLogger.LogLevel.DEBUG,
    customLogger = { level, tag, message, throwable ->
        // 自定义日志处理
    }
)
```

## 最佳实践

### 1. 连接管理

```kotlin
class MyWebSocketManager {
    private var webSocketManager: WebSocketManager? = null
    
    fun connect() {
        if (webSocketManager?.isConnected() == true) {
            return
        }
        
        val config = WebSocketConfig.forProduction(url)
        webSocketManager = WebSocketManager(config)
        webSocketManager?.addListener("main", createListener())
        webSocketManager?.connect()
    }
    
    fun disconnect() {
        webSocketManager?.disconnect()
    }
    
    fun destroy() {
        webSocketManager?.destroy()
        webSocketManager = null
    }
}
```

### 2. 消息处理

```kotlin
override fun onTextMessage(text: String) {
    try {
        val json = JSONObject(text)
        when (json.getString("type")) {
            "ping" -> handlePing()
            "notification" -> handleNotification(json)
            "data" -> handleData(json)
            else -> handleUnknownMessage(text)
        }
    } catch (e: Exception) {
        WebSocketLogger.e(message = "消息处理异常", throwable = e)
    }
}
```

### 3. 生命周期管理

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var webSocketManager: WebSocketManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWebSocket()
    }
    
    override fun onResume() {
        super.onResume()
        webSocketManager.connect()
    }
    
    override fun onPause() {
        super.onPause()
        // 根据需要决定是否断开连接
    }
    
    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.destroy()
    }
}
```

## 注意事项

1. **SSL 配置**: 生产环境中不要设置 `ignoreSSLErrors = true`
2. **内存管理**: 及时调用 `destroy()` 方法释放资源
3. **线程安全**: 所有回调都在主线程执行，长时间操作请切换到后台线程
4. **消息队列**: 连接断开时消息会暂存在队列中，重连后自动发送
5. **重连策略**: 根据实际需求选择合适的重连策略和参数

## 故障排除

### 连接失败
- 检查网络连接
- 验证 WebSocket URL 格式
- 检查防火墙和代理设置
- 查看服务器日志

### SSL 错误
- 确保证书有效
- 检查域名匹配
- 验证证书链完整性

### 频繁重连
- 调整心跳间隔
- 检查网络稳定性
- 优化重连策略参数

### 内存泄漏
- 确保调用 `destroy()` 方法
- 及时移除不需要的监听器
- 避免在回调中持有 Activity 引用