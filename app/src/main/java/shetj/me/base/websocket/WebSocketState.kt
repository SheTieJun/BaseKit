package shetj.me.base.websocket

/**
 * WebSocket连接状态枚举
 */
enum class WebSocketState {
    /** 未连接 */
    DISCONNECTED,
    
    /** 连接中 */
    CONNECTING,
    
    /** 已连接 */
    CONNECTED,
    
    /** 重连中 */
    RECONNECTING,
    
    /** 连接失败 */
    FAILED,
    
    /** 连接关闭 */
    CLOSED
}