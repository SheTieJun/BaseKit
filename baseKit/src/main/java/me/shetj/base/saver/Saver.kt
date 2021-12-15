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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.shetj.base.ktx.toBean

@Entity(tableName = "saver")
data class Saver(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") var id: Int = 0,
        @ColumnInfo(name = "createTime") var createTime: Long? = null,
        @ColumnInfo(name = "updateTime") var updateTime: Long? = null,
        @ColumnInfo(name = "jsonInfo") var jsonInfo: String? = null,
        @ColumnInfo(name = "isDel") var isDel: Boolean = false,
        @ColumnInfo(name = "groupName") val groupName: String?,
        @ColumnInfo(name = "keyName") val keyName: String?,
        @ColumnInfo(name = "value") val value: String?
) {

     inline fun <reified T> getInfoByJson() = jsonInfo.toBean<T>()
}