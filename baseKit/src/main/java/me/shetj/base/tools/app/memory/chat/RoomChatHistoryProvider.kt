package me.shetj.base.tools.app.memory.chat

import ai.koog.agents.chatMemory.feature.ChatHistoryProvider
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.RequestMetaInfo
import ai.koog.prompt.message.ResponseMetaInfo

class RoomChatHistoryProvider(
    private val dao: ChatMemoryDao
) : ChatHistoryProvider {

    override suspend fun store(conversationId: String, messages: List<Message>) {
        // 约定：只持久化 user/assistant（与 ChatMemory 的 filterMessages 预处理保持一致）
        val now = System.currentTimeMillis()
        val entities = messages.mapIndexedNotNull { index, msg ->
            when (msg) {
                is Message.User -> ChatMemoryMessageEntity(
                    conversationId = conversationId,
                    seq = index.toLong(),
                    role = "user",
                    content = msg.content,
                    createdAt = now
                )

                is Message.Assistant -> ChatMemoryMessageEntity(
                    conversationId = conversationId,
                    seq = index.toLong(),
                    role = "assistant",
                    content = msg.content,
                    createdAt = now
                )

                else -> null
            }
        }
        dao.replaceConversation(conversationId, entities)
    }

    override suspend fun load(conversationId: String): List<Message> {
        // 约定：从 Room 中按 seq 恢复消息顺序
        val entities = dao.getByConversationId(conversationId)
        return entities.mapNotNull { e ->
            when (e.role) {
                "user" -> Message.User(e.content, RequestMetaInfo.Empty)
                "assistant" -> Message.Assistant(e.content, ResponseMetaInfo.Empty)
                else -> null
            }
        }
    }
}
