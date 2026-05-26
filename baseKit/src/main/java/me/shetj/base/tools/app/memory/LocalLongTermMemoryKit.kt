package me.shetj.base.tools.app.memory

import ai.koog.agents.longtermmemory.model.MemoryRecord
import ai.koog.rag.base.TextDocument
import ai.koog.rag.base.storage.WriteStorage
import me.shetj.base.tools.app.memory.model.MemoryType

class LocalLongTermMemoryKit(
    private val storage: WriteStorage<TextDocument>,
    private val namespaceProvider: () -> String
) {

    suspend fun upsertPreference(key: String, value: String, tags: List<String> = emptyList()) {
        upsert(type = MemoryType.Preference, key = key, content = value, tags = tags)
    }

    suspend fun upsertLearningProgress(key: String, content: String, tags: List<String> = emptyList()) {
        upsert(type = MemoryType.LearningProgress, key = key, content = content, tags = tags)
    }

    suspend fun recordCommonMistake(key: String, content: String, tags: List<String> = emptyList()) {
        upsert(type = MemoryType.CommonMistake, key = key, content = content, tags = tags)
    }

    suspend fun storePersonalInfo(key: String, content: String, tags: List<String> = emptyList()) {
        upsert(type = MemoryType.PersonalInfo, key = key, content = content, tags = tags)
    }

    suspend fun upsertCustom(
        type: String,
        key: String,
        content: String,
        metadata: Map<String, Any> = emptyMap()
    ) {
        val ns = namespaceProvider()
        val merged: Map<String, Any> = buildMap {
            put("type", type)
            put("key", key)
            put("schemaVersion", 1)
            putAll(metadata)
        }
        storage.add(
            documents = listOf(MemoryRecord(content = content, id = "${type}:${key}", metadata = merged)),
            namespace = ns
        )
    }

    private suspend fun upsert(type: MemoryType, key: String, content: String, tags: List<String>) {
        val ns = namespaceProvider()
        val metadata: Map<String, Any> = buildMap {
            put("type", type.name)
            put("key", key)
            put("schemaVersion", 1)
            if (tags.isNotEmpty()) put("tags", tags)
        }
        storage.add(
            documents = listOf(MemoryRecord(content = content, id = "${type.name}:${key}", metadata = metadata)),
            namespace = ns
        )
    }
}

