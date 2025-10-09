package shetj.me.base.websocket

/**
 * WebSocket异常基类
 */
sealed class WebSocketException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * 连接异常
     */
    class ConnectionException(
        message: String,
        cause: Throwable? = null
    ) : WebSocketException("连接异常: $message", cause)
    
    /**
     * 认证异常
     */
    class AuthenticationException(
        message: String,
        cause: Throwable? = null
    ) : WebSocketException("认证异常: $message", cause)
    
    /**
     * 网络异常
     */
    class NetworkException(
        message: String,
        cause: Throwable? = null
    ) : WebSocketException("网络异常: $message", cause)
    
    /**
     * SSL证书异常
     */
    class SSLException(
        message: String,
        cause: Throwable? = null
    ) : WebSocketException("SSL证书异常: $message", cause)
    
    /**
     * 超时异常
     */
    class TimeoutException(
        message: String,
        cause: Throwable? = null
    ) : WebSocketException("超时异常: $message", cause)
    
    /**
     * 消息发送异常
     */
    class MessageSendException(
        message: String,
        val originalMessage: String,
        cause: Throwable? = null
    ) : WebSocketException("消息发送异常: $message", cause)
    
    /**
     * 协议异常
     */
    class ProtocolException(
        message: String,
        cause: Throwable? = null
    ) : WebSocketException("协议异常: $message", cause)
    
    /**
     * 服务器异常
     */
    class ServerException(
        message: String,
        val statusCode: Int,
        cause: Throwable? = null
    ) : WebSocketException("服务器异常 ($statusCode): $message", cause)
    
    /**
     * 配置异常
     */
    class ConfigurationException(
        message: String,
        cause: Throwable? = null
    ) : WebSocketException("配置异常: $message", cause)
    
    companion object {
        /**
         * 根据异常类型创建对应的WebSocket异常
         */
        fun fromThrowable(throwable: Throwable): WebSocketException {
            return when {
                throwable is WebSocketException -> throwable
                
                throwable is java.net.SocketTimeoutException ||
                throwable is java.util.concurrent.TimeoutException -> {
                    TimeoutException("连接或操作超时", throwable)
                }
                
                throwable is java.net.UnknownHostException ||
                throwable is java.net.ConnectException ||
                throwable is java.net.NoRouteToHostException -> {
                    NetworkException("网络连接失败", throwable)
                }
                
                throwable is javax.net.ssl.SSLException ||
                throwable is java.security.cert.CertificateException -> {
                    SSLException("SSL证书验证失败", throwable)
                }
                
                throwable is java.io.IOException -> {
                    ConnectionException("IO异常", throwable)
                }
                
                throwable is IllegalArgumentException ||
                throwable is IllegalStateException -> {
                    ConfigurationException("配置错误", throwable)
                }
                
                else -> {
                    ConnectionException("未知异常: ${throwable.message}", throwable)
                }
            }
        }
        
        /**
         * 根据HTTP状态码创建服务器异常
         */
        fun fromHttpStatusCode(statusCode: Int, message: String? = null): ServerException {
            val errorMessage = message ?: when (statusCode) {
                400 -> "请求格式错误"
                401 -> "未授权访问"
                403 -> "访问被禁止"
                404 -> "服务未找到"
                500 -> "服务器内部错误"
                502 -> "网关错误"
                503 -> "服务不可用"
                504 -> "网关超时"
                else -> "HTTP错误"
            }
            return ServerException(errorMessage, statusCode)
        }
    }
}