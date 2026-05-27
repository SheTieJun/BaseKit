package shetj.me.base.func.koog

import android.content.Context
import me.shetj.base.tools.app.memory.chat.ChatMemoryDatabase
import me.shetj.base.tools.app.memory.chat.ChatMemoryMessageEntity
import timber.log.Timber

/**
 * 聊天记录持久化管理器
 */
class ChatHistoryManager private constructor(context: Context) {
    private val dao = ChatMemoryDatabase.getInstance(context.applicationContext).chatMemoryDao()

    companion object {
        @Volatile private var instance: ChatHistoryManager? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: ChatHistoryManager(context).also { instance = it }
        }
    }

    /**
     * 加载指定 Agent 的聊天历史
     */
    suspend fun loadMessages(agentId: String): List<ChatMessage> {
        return try {
            dao.getByConversationId(agentId).map { entity ->
                ChatMessage(
                    id = entity.rowId.toString(),
                    content = entity.content,
                    isUser = entity.role == "user",
                    timestamp = entity.createdAt
                )
            }
        } catch (e: Exception) {
            Timber.tag("ChatHistory").e(e, "加载聊天记录失败: ${e.message}")
            emptyList()
        }
    }

    /**
     * 保存聊天消息列表
     */
    suspend fun saveMessages(agentId: String, messages: List<ChatMessage>) {
        try {
            val entities = messages.mapIndexed { index, msg ->
                ChatMemoryMessageEntity(
                    conversationId = agentId,
                    seq = index.toLong(),
                    role = if (msg.isUser) "user" else "assistant",
                    content = msg.content,
                    createdAt = msg.timestamp
                )
            }
            dao.replaceConversation(agentId, entities)
        } catch (e: Exception) {
            Timber.tag("ChatHistory").e(e, "保存聊天记录失败: ${e.message}")
        }
    }

    /**
     * 清空指定 Agent 的聊天历史
     */
    suspend fun clearMessages(agentId: String) {
        try {
            dao.deleteByConversationId(agentId)
        } catch (e: Exception) {
            Timber.tag("ChatHistory").e(e, "清空聊天记录失败: ${e.message}")
        }
    }
}
