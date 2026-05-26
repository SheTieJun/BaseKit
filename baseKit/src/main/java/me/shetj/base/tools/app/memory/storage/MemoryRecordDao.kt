package me.shetj.base.tools.app.memory.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MemoryRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MemoryRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<MemoryRecordEntity>)

    @Query(
        """
        SELECT * FROM koog_long_term_memory
        WHERE namespace = :namespace
          AND content LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
        LIMIT :limit
        """
    )
    suspend fun searchByContent(namespace: String, query: String, limit: Int): List<MemoryRecordEntity>

    @Query(
        """
        SELECT * FROM koog_long_term_memory
        WHERE namespace = :namespace
        ORDER BY updatedAt DESC
        LIMIT :limit
        """
    )
    suspend fun listRecent(namespace: String, limit: Int): List<MemoryRecordEntity>

    @Query(
        """
        DELETE FROM koog_long_term_memory
        WHERE namespace = :namespace
          AND id IN (:ids)
        """
    )
    suspend fun deleteByIds(namespace: String, ids: List<String>): Int

    @Query(
        """
        SELECT * FROM koog_long_term_memory
        WHERE namespace = :namespace
          AND id IN (:ids)
        """
    )
    suspend fun getByIds(namespace: String, ids: List<String>): List<MemoryRecordEntity>
}
