/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
