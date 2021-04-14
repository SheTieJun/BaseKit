package me.shetj.base.saver

import androidx.paging.PagingSource
import androidx.room.*
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

    @Query(value = "SELECT * FROM saver order by id ")
    fun searchSaver(): PagingSource<Int, Saver>

}