

package shetj.me.base.contentprovider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteDatabaseOpenHelper(context: Context?) :
    SQLiteOpenHelper(context, "testcontentprovider.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table person(personid integer primary key autoincrement," +
                " name varchar(20),phone varchar(12),salary Integer(12))"
        )
    }

    // 当数据库版本号发生变化时调用该方法
    override fun onUpgrade(db: SQLiteDatabase, arg1: Int, arg2: Int) {
    }
}
