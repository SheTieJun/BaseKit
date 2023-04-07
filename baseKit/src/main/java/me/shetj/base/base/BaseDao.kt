package me.shetj.base.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    @Insert
    fun insert(vararg obj: T)

    @Update
    fun update(obj: T)

    @Delete
    fun delete(obj: T)
}