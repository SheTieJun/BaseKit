package me.shetj.base.tools.app.memory.model

import kotlinx.serialization.Serializable

@Serializable
enum class MemoryType {
    Preference,
    LearningProgress,
    CommonMistake,
    PersonalInfo,
    Custom
}

@Serializable
data class MemoryMetadata(
    val type: MemoryType,
    val key: String,
    val schemaVersion: Int = 1,
    val tags: List<String> = emptyList()
)
