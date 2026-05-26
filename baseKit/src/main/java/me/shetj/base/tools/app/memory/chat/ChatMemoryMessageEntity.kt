package me.shetj.base.tools.app.memory.chat

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "koog_chat_memory_message",
    indices = [
        Index(value = ["conversationId", "seq"], unique = true),
        Index(value = ["conversationId"])
    ]
)
data class ChatMemoryMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val rowId: Long = 0,
    val conversationId: String,
    // 用于保持消息顺序，通常取 messages 的下标；同一 conversationId 内必须唯一
    val seq: Long,
    // 仅存储 user/assistant（与 ChatMemory 的 filterMessages 保持一致）
    val role: String,
    val content: String,
    val createdAt: Long
)
