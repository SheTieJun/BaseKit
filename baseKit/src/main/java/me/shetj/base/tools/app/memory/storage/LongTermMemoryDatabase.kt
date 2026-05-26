package me.shetj.base.tools.app.memory.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MemoryRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LongTermMemoryDatabase : RoomDatabase() {

    abstract fun memoryRecordDao(): MemoryRecordDao

    companion object {

        @Volatile
        private var INSTANCE: LongTermMemoryDatabase? = null

        fun getInstance(context: Context): LongTermMemoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LongTermMemoryDatabase::class.java,
                    "koog_long_term_memory"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

