package shetj.me.base.websocket

import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * WebSocket配置类
 */
data class WebSocketConfig(
    /** WebSocket服务器URL */
    val url: String,
    
    /** 连接超时时间（毫秒） */
    val connectTimeout: Long = 10_000L,
    
    /** 读取超时时间（毫秒） */
    val readTimeout: Long = 30_000L,
    
    /** 写入超时时间（毫秒） */
    val writeTimeout: Long = 30_000L,
    
    /** 心跳间隔时间（毫秒） */
    val heartbeatInterval: Long = 30_000L,
    
    /** 心跳消息内容 */
    val heartbeatMessage: String = "ping",
    
    /** 是否启用自动重连 */
    val enableAutoReconnect: Boolean = true,
    
    /** 重连间隔时间（毫秒） */
    val reconnectInterval: Long = 5_000L,
    
    /** 最大重连次数，-1表示无限重连 */
    val maxReconnectAttempts: Int = -1,
    
    /** 重连延迟策略：固定延迟或指数退避 */
    val reconnectDelayStrategy: ReconnectDelayStrategy = ReconnectDelayStrategy.FIXED,
    
    /** 指数退避的最大延迟时间（毫秒） */
    val maxReconnectDelay: Long = 60_000L,
    
    /** 请求头 */
    val headers: Map<String, String> = emptyMap(),
    
    /** SSL Socket Factory（用于wss连接） */
    val sslSocketFactory: SSLSocketFactory? = null,
    
    /** X509 Trust Manager（用于wss连接） */
    val trustManager: X509TrustManager? = null,
    
    /** 是否忽略SSL证书验证（仅用于测试环境） */
    val ignoreSSLErrors: Boolean = false,
    
    /** 消息队列最大大小 */
    val messageQueueSize: Int = 1000,
    
    /** 是否启用消息压缩 */
    val enableCompression: Boolean = false
) {
    
    /**
     * 重连延迟策略
     */
    enum class ReconnectDelayStrategy {
        /** 固定延迟 */
        FIXED,
        /** 指数退避 */
        EXPONENTIAL_BACKOFF
    }
    
    /**
     * 计算重连延迟时间
     * @param attemptCount 重连尝试次数（从1开始）
     * @return 延迟时间（毫秒）
     */
    fun calculateReconnectDelay(attemptCount: Int): Long {
        return when (reconnectDelayStrategy) {
            ReconnectDelayStrategy.FIXED -> reconnectInterval
            ReconnectDelayStrategy.EXPONENTIAL_BACKOFF -> {
                val delay = reconnectInterval * (1L shl (attemptCount - 1))
                minOf(delay, maxReconnectDelay)
            }
        }
    }
    
    /**
     * 验证配置的有效性
     * @throws IllegalArgumentException 如果配置无效
     */
    fun validate() {
        require(url.isNotBlank()) { "WebSocket URL不能为空" }
        require(url.startsWith("ws://") || url.startsWith("wss://")) { 
            "WebSocket URL必须以ws://或wss://开头" 
        }
        require(connectTimeout > 0) { "连接超时时间必须大于0" }
        require(readTimeout > 0) { "读取超时时间必须大于0" }
        require(writeTimeout > 0) { "写入超时时间必须大于0" }
        require(heartbeatInterval > 0) { "心跳间隔时间必须大于0" }
        require(reconnectInterval > 0) { "重连间隔时间必须大于0" }
        require(maxReconnectDelay > 0) { "最大重连延迟时间必须大于0" }
        require(messageQueueSize > 0) { "消息队列大小必须大于0" }
    }
    
    companion object {
        /**
         * 创建默认配置
         */
        fun default(url: String): WebSocketConfig {
            return WebSocketConfig(url = url)
        }
        
        /**
         * 创建用于生产环境的配置
         */
        fun forProduction(url: String): WebSocketConfig {
            return WebSocketConfig(
                url = url,
                connectTimeout = 15_000L,
                readTimeout = 60_000L,
                writeTimeout = 60_000L,
                heartbeatInterval = 45_000L,
                reconnectInterval = 10_000L,
                maxReconnectAttempts = 10,
                reconnectDelayStrategy = ReconnectDelayStrategy.EXPONENTIAL_BACKOFF,
                enableCompression = true
            )
        }
        
        /**
         * 创建用于开发环境的配置
         */
        fun forDevelopment(url: String): WebSocketConfig {
            return WebSocketConfig(
                url = url,
                connectTimeout = 5_000L,
                readTimeout = 30_000L,
                writeTimeout = 30_000L,
                heartbeatInterval = 20_000L,
                reconnectInterval = 3_000L,
                maxReconnectAttempts = -1,
                ignoreSSLErrors = true
            )
        }
    }
}