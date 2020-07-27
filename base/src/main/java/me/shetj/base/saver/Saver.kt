package me.shetj.base.saver

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.shetj.base.ktx.toBean

@Entity(tableName = "saver")
data class Saver(
        @ColumnInfo(name = "groupName") val groupName: String?,
        @ColumnInfo(name = "keyName") val keyName: String?,
        @ColumnInfo(name = "value") val value: String?
        ) {

    @PrimaryKey(autoGenerate = true) var id = 0
    @ColumnInfo(name = "createTime")
    var createTime: Long? = null
    @ColumnInfo(name = "updateTime")
    var updateTime: Long? = null
    @ColumnInfo(name = "jsonInfo")
    var jsonInfo: String? = null
    @ColumnInfo(name = "isDel")
    var isDel: Boolean = false

    inline fun <reified T> getInfoByJson() = jsonInfo.toBean<T>()
}