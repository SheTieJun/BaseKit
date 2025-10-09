package shetj.me.base.websocket

import okhttp3.Response
import okio.ByteString

/**
 * WebSocket事件监听器接口
 */
interface WebSocketStateListener {
    
    /**
     * 连接状态变化回调
     * @param state 新的连接状态
     * @param reason 状态变化原因（可选）
     */
    fun onStateChanged(state: WebSocketState, reason: String? = null)
    
    /**
     * 连接成功回调
     * @param response 响应信息
     */
    fun onConnected(response: Response)
    
    /**
     * 接收到文本消息
     * @param text 文本消息内容
     */
    fun onTextMessage(text: String)
    
    /**
     * 接收到二进制消息
     * @param bytes 二进制消息内容
     */
    fun onBinaryMessage(bytes: ByteString)
    
    /**
     * 连接关闭回调
     * @param code 关闭代码
     * @param reason 关闭原因
     */
    fun onClosed(code: Int, reason: String)
    
    /**
     * 连接失败回调
     * @param throwable 异常信息
     * @param response 响应信息（可能为null）
     */
    fun onFailure(throwable: Throwable, response: Response?)
    
    /**
     * 消息发送失败回调
     * @param message 发送失败的消息
     * @param throwable 异常信息
     */
    fun onSendMessageFailed(message: String, throwable: Throwable)
}

/**
 * WebSocket监听器的简单实现，提供默认空实现
 */
open class SimpleWebSocketListener : WebSocketStateListener {
    override fun onStateChanged(state: WebSocketState, reason: String?) {}
    override fun onConnected(response: Response) {}
    override fun onTextMessage(text: String) {}
    override fun onBinaryMessage(bytes: ByteString) {}
    override fun onClosed(code: Int, reason: String) {}
    override fun onFailure(throwable: Throwable, response: Response?) {}
    override fun onSendMessageFailed(message: String, throwable: Throwable) {}
}