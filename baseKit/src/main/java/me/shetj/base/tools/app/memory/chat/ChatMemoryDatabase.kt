package me.shetj.base.tools.app.memory.chat

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChatMemoryMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChatMemoryDatabase : RoomDatabase() {

    abstract fun chatMemoryDao(): ChatMemoryDao

    companion object {

        @Volatile
        private var INSTANCE: ChatMemoryDatabase? = null

        fun getInstance(context: Context): ChatMemoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ChatMemoryDatabase::class.java,
                    "koog_chat_memory"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

