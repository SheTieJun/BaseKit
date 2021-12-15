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
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface SaverDao {

    @Query("SELECT * FROM saver order by id")
    fun getAll(): Flowable<List<Saver>>

    @Query("SELECT * FROM saver WHERE isDel = 'false'")
    fun getAllNoDEL(): Flowable<List<Saver>>

    @Query("SELECT * FROM saver WHERE isDel = 'true'")
    fun getAllDEL(): Flowable<List<Saver>>

    @Query("SELECT * FROM saver WHERE groupName = :groupN AND isDel = :isDel order by updateTime ")
    fun getAll(groupN: String, isDel: Boolean): Flowable<List<Saver>>

    @Delete
    fun deleteSaver(vararg saver: Saver): Completable

    @Query("DELETE FROM saver")
    fun deleteAll(): Completable

    @Insert
    fun insert(vararg saver: Saver): Completable

    @Insert
    fun insertAll(saver: List<Saver>): Completable

    @Query("SELECT * FROM saver WHERE groupName = :groupN AND keyName = :key LIMIT 1")
    fun findSaver(groupN: String, key: String): Flowable<Saver>

    @Update
    fun updateSaver(vararg saver: Saver): Completable
}
