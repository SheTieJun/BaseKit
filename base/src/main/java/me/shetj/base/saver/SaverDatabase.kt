package me.shetj.base.saver

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Saver::class], version = 1, exportSchema = false)
abstract class SaverDatabase : RoomDatabase() {

    abstract fun saverDao(): SaverDao

    companion object {

        @Volatile
        private var INSTANCE: SaverDatabase? = null

        fun getInstance(context: Context): SaverDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDataBase(context).also {
                    INSTANCE = it
                }
            }
        }

        private fun buildDataBase(context: Context) =
            Room.databaseBuilder(context.applicationContext, SaverDatabase::class.java, "saver")
                .build()
    }

}