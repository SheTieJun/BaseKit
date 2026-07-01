package me.shetj.base.network.lan.model

import java.util.UUID

/**
 * 局域网通信消息
 *
 * @param id 消息唯一 ID
 * @param type 消息类型
 * @param payload 消息内容
 * @param senderId 发送方设备标识
 */
data class LanMessage(
    val id: String = UUID.randomUUID().toString(),
    val type: String = "text",
    val payload: String,
    val senderId: String = "",
) {
    companion object {
        const val TYPE_HANDSHAKE = "handshake" // 握手消息，用于密钥校验
        const val TYPE_TEXT = "text"
        const val TYPE_JSON = "json"
        const val TYPE_BYTES = "bytes"
    }
}
