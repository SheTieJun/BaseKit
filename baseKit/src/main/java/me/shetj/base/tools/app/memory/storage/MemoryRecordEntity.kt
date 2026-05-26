package me.shetj.base.tools.app.memory.storage

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "koog_long_term_memory",
    indices = [
        Index(value = ["namespace"]),
        Index(value = ["namespace", "type"]),
        Index(value = ["namespace", "type", "key"])
    ]
)
data class MemoryRecordEntity(
    @PrimaryKey
    val id: String,
    val namespace: String,
    val type: String,
    val key: String,
    val content: String,
    val metadataJson: String,
    val updatedAt: Long
)

