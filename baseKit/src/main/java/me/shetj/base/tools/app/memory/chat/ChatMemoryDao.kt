package me.shetj.base.tools.app.memory.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ChatMemoryDao {

    @Query(
        """
        SELECT * FROM koog_chat_memory_message
        WHERE conversationId = :conversationId
        ORDER BY seq ASC
        """
    )
    suspend fun getByConversationId(conversationId: String): List<ChatMemoryMessageEntity>

    @Query(
        """
        SELECT * FROM koog_chat_memory_message
        WHERE conversationId = :conversationId
        ORDER BY seq DESC
        LIMIT :limit
        """
    )
    suspend fun getRecentByConversationId(conversationId: String, limit: Int): List<ChatMemoryMessageEntity>

    @Query(
        """
        SELECT COUNT(*) FROM koog_chat_memory_message
        WHERE conversationId = :conversationId
        """
    )
    suspend fun countByConversationId(conversationId: String): Int

    @Query(
        """
        SELECT MAX(seq) FROM koog_chat_memory_message
        WHERE conversationId = :conversationId
        """
    )
    suspend fun maxSeq(conversationId: String): Long?

    @Query(
        """
        DELETE FROM koog_chat_memory_message
        WHERE conversationId = :conversationId
        """
    )
    suspend fun deleteByConversationId(conversationId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ChatMemoryMessageEntity>)

    @Transaction
    suspend fun replaceConversation(conversationId: String, entities: List<ChatMemoryMessageEntity>) {
        deleteByConversationId(conversationId)
        if (entities.isNotEmpty()) insertAll(entities)
    }
}
