package me.shetj.base.saver

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SaverDao {

    @Query("SELECT * FROM saver order by id")
    fun getAll(): Flow<List<Saver>>

    @Query("SELECT * FROM saver WHERE isDel = 'false'")
    fun getAllNoDEL(): Flow<List<Saver>>

    @Query("SELECT * FROM saver WHERE isDel = 'true'")
    fun getAllDEL(): Flow<List<Saver>>

    @Query("SELECT * FROM saver WHERE groupName = :groupN AND isDel = :isDel order by updateTime ")
    fun getAll(groupN: String, isDel: Boolean): Flow<List<Saver>>

    @Delete
    suspend fun deleteSaver(vararg saver: Saver)

    @Query("DELETE FROM saver")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(saver: Saver): Long

    @Insert
    suspend fun insertAll(saver: List<Saver>)

    @Query("SELECT * FROM saver WHERE groupName = :groupN AND keyName = :key LIMIT 1")
    fun findSaver(groupN: String, key: String): Flow<Saver>

    @Update
    suspend fun updateSaver(vararg saver: Saver)
}
