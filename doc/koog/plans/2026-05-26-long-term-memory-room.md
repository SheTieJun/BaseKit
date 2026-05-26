# Long-term Memory（Room 本地持久化）Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 BaseKit 中接入 Koog `LongTermMemory`，使用 Room 本地存储，按 `userId + agentId` 作为 namespace 隔离，支持偏好/学习进度/常错点/个人信息与自定义扩展写入，并可在对话前自动检索注入 Prompt。

**Architecture:** `KoogAgentKit.createAgent()` 安装 `LongTermMemory`（Retrieval 默认开启；Ingestion 默认关闭）。本地提供 `RoomTextDocumentStorage` 同时实现 `SearchStorage<TextDocument, SimilaritySearchRequest>` 与 `WriteStorage<TextDocument>`。业务侧提供 `LocalLongTermMemoryKit` 封装强类型写入，并保留 `customUpsert()` 扩展口。

**Tech Stack:** Koog 0.8.0 + `ai.koog:agents-features-longterm-memory` + Room + KSP + org.json（metadata JSON）

---

## 文件结构

**Modify**
- `gradle/libs.versions.toml`
- `baseKit/build.gradle.kts`
- `baseKit/src/main/java/me/shetj/base/tools/app/KoogAgentKit.kt`
- `doc/koog/SKILL.md`

**Create**
- `baseKit/src/main/java/me/shetj/base/tools/app/memory/model/MemoryTypes.kt`
- `baseKit/src/main/java/me/shetj/base/tools/app/memory/storage/MemoryRecordEntity.kt`
- `baseKit/src/main/java/me/shetj/base/tools/app/memory/storage/MemoryRecordDao.kt`
- `baseKit/src/main/java/me/shetj/base/tools/app/memory/storage/LongTermMemoryDatabase.kt`
- `baseKit/src/main/java/me/shetj/base/tools/app/memory/storage/RoomTextDocumentStorage.kt`
- `baseKit/src/main/java/me/shetj/base/tools/app/memory/LocalLongTermMemoryKit.kt`
- `doc/koog/KOOG_LONG_TERM_MEMORY.md`（已创建，用于说明）

---

### Task 1: 依赖接入（Version Catalog + baseKit api）

**Files**
- Modify: `gradle/libs.versions.toml`
- Modify: `baseKit/build.gradle.kts`

- [ ] **Step 1: 在 Version Catalog 新增 library alias**

在 `gradle/libs.versions.toml` 的 `[libraries]` 增加：

```toml
koog-agents-features-longterm-memory = { module = "ai.koog:agents-features-longterm-memory", version.ref = "koog" }
```

- [ ] **Step 2: baseKit 增加 api 依赖**

在 `baseKit/build.gradle.kts` 的 `addOther()` 中追加：

```kotlin
api(libs.koog.agents.features.longterm.memory)
```

- [ ] **Step 3: 编译验证**

运行：

```bash
./gradlew :baseKit:compileDebugKotlin --no-daemon
```

期望：编译通过，并能在代码中引用：

```kotlin
import ai.koog.agents.longtermmemory.feature.LongTermMemory
```

---

### Task 2: 定义 Memory Types（可扩展 schema）

**Files**
- Create: `baseKit/src/main/java/me/shetj/base/tools/app/memory/model/MemoryTypes.kt`

- [ ] **Step 1: 创建 MemoryTypes.kt**

```kotlin
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
```

- [ ] **Step 2: 编译验证**

运行：

```bash
./gradlew :baseKit:compileDebugKotlin --no-daemon
```

---

### Task 3: Room 持久化（Entity/Dao/Database）

**Files**
- Create: `baseKit/src/main/java/me/shetj/base/tools/app/memory/storage/MemoryRecordEntity.kt`
- Create: `baseKit/src/main/java/me/shetj/base/tools/app/memory/storage/MemoryRecordDao.kt`
- Create: `baseKit/src/main/java/me/shetj/base/tools/app/memory/storage/LongTermMemoryDatabase.kt`

- [ ] **Step 1: 创建 MemoryRecordEntity.kt**

```kotlin
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
```

- [ ] **Step 2: 创建 MemoryRecordDao.kt**

```kotlin
package me.shetj.base.tools.app.memory.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MemoryRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MemoryRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<MemoryRecordEntity>)

    @Query(
        """
        SELECT * FROM koog_long_term_memory
        WHERE namespace = :namespace
          AND content LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
        LIMIT :limit
        """
    )
    suspend fun searchByContent(namespace: String, query: String, limit: Int): List<MemoryRecordEntity>

    @Query(
        """
        SELECT * FROM koog_long_term_memory
        WHERE namespace = :namespace
          AND type = :type
          AND key = :key
        LIMIT 1
        """
    )
    suspend fun findByTypeAndKey(namespace: String, type: String, key: String): MemoryRecordEntity?

    @Query(
        """
        DELETE FROM koog_long_term_memory
        WHERE namespace = :namespace
          AND id IN (:ids)
        """
    )
    suspend fun deleteByIds(namespace: String, ids: List<String>): Int

    @Query(
        """
        SELECT * FROM koog_long_term_memory
        WHERE namespace = :namespace
          AND id IN (:ids)
        """
    )
    suspend fun getByIds(namespace: String, ids: List<String>): List<MemoryRecordEntity>
}
```

- [ ] **Step 3: 创建 LongTermMemoryDatabase.kt**

```kotlin
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
```

- [ ] **Step 4: 编译验证**

运行：

```bash
./gradlew :baseKit:compileDebugKotlin --no-daemon
```

---

### Task 4: RoomTextDocumentStorage（实现 Koog RAG Storage 接口）

**Files**
- Create: `baseKit/src/main/java/me/shetj/base/tools/app/memory/storage/RoomTextDocumentStorage.kt`

- [ ] **Step 1: 创建 RoomTextDocumentStorage.kt**

```kotlin
package me.shetj.base.tools.app.memory.storage

import ai.koog.rag.base.TextDocument
import ai.koog.rag.base.storage.DeletionStorage
import ai.koog.rag.base.storage.LookupStorage
import ai.koog.rag.base.storage.SearchStorage
import ai.koog.rag.base.storage.WriteStorage
import ai.koog.rag.base.storage.search.Score
import ai.koog.rag.base.storage.search.ScoreMetric
import ai.koog.rag.base.storage.search.SearchResult
import ai.koog.rag.base.storage.search.SimilaritySearchRequest
import org.json.JSONArray
import org.json.JSONObject

class RoomTextDocumentStorage(
    private val dao: MemoryRecordDao
) : SearchStorage<TextDocument, SimilaritySearchRequest>,
    WriteStorage<TextDocument>,
    LookupStorage<TextDocument>,
    DeletionStorage {

    override suspend fun add(documents: List<TextDocument>, namespace: String?): List<String> {
        val ns = namespace ?: "default"
        val entities = documents.map { doc ->
            val id = doc.id ?: error("TextDocument.id 不能为空")
            val metadataJson = JSONObject(doc.metadata).toString()
            MemoryRecordEntity(
                id = id,
                namespace = ns,
                type = (doc.metadata?.get("type") as? String).orEmpty(),
                key = (doc.metadata?.get("key") as? String).orEmpty(),
                content = doc.content,
                metadataJson = metadataJson,
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.upsertAll(entities)
        return entities.map { it.id }
    }

    override suspend fun update(documents: Map<String, TextDocument>, namespace: String?): List<String> {
        val ns = namespace ?: "default"
        val entities = documents.map { (id, doc) ->
            val metadataJson = JSONObject(doc.metadata).toString()
            MemoryRecordEntity(
                id = id,
                namespace = ns,
                type = (doc.metadata?.get("type") as? String).orEmpty(),
                key = (doc.metadata?.get("key") as? String).orEmpty(),
                content = doc.content,
                metadataJson = metadataJson,
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.upsertAll(entities)
        return entities.map { it.id }
    }

    override suspend fun search(
        request: SimilaritySearchRequest,
        namespace: String?
    ): List<SearchResult<TextDocument>> {
        val ns = namespace ?: "default"
        val records = dao.searchByContent(ns, request.queryText, request.limit)
        return records.map { entity ->
            SearchResult(
                document = TextDocument(
                    content = entity.content,
                    id = entity.id,
                    metadata = entity.metadataJson.toAnyMap()
                ),
                score = Score(1.0, ScoreMetric.COSINE_SIMILARITY)
            )
        }
    }

    override suspend fun delete(ids: List<String>, namespace: String?): List<String> {
        val ns = namespace ?: "default"
        dao.deleteByIds(ns, ids)
        return ids
    }

    override suspend fun get(ids: List<String>, namespace: String?): List<TextDocument> {
        val ns = namespace ?: "default"
        return dao.getByIds(ns, ids).map { entity ->
            TextDocument(content = entity.content, id = entity.id, metadata = entity.metadataJson.toAnyMap())
        }
    }

    private fun String.toAnyMap(): Map<String, Any> {
        if (isBlank()) return emptyMap()
        val obj = JSONObject(this)
        return buildMap {
            obj.keys().forEach { key ->
                val v = obj.opt(key)
                when (v) {
                    is JSONArray -> put(key, (0 until v.length()).map { idx -> v.opt(idx) })
                    JSONObject.NULL -> Unit
                    null -> Unit
                    else -> put(key, v)
                }
            }
        }
    }
}
```

- [ ] **Step 2: 编译验证**

运行：

```bash
./gradlew :baseKit:compileDebugKotlin --no-daemon
```

---

### Task 5: LocalLongTermMemoryKit（业务强类型写入 + 自定义扩展）

**Files**
- Create: `baseKit/src/main/java/me/shetj/base/tools/app/memory/LocalLongTermMemoryKit.kt`

- [ ] **Step 1: 创建 LocalLongTermMemoryKit.kt**

```kotlin
package me.shetj.base.tools.app.memory

import ai.koog.rag.base.TextDocument
import ai.koog.rag.base.storage.WriteStorage
import me.shetj.base.tools.app.memory.model.MemoryType

class LocalLongTermMemoryKit(
    private val storage: WriteStorage<TextDocument>,
    private val namespaceProvider: () -> String
) {
    suspend fun upsertPreference(key: String, value: String) {
        upsert(type = MemoryType.Preference, key = key, content = value)
    }

    suspend fun upsertLearningProgress(key: String, content: String) {
        upsert(type = MemoryType.LearningProgress, key = key, content = content)
    }

    suspend fun recordCommonMistake(key: String, content: String) {
        upsert(type = MemoryType.CommonMistake, key = key, content = content)
    }

    suspend fun storePersonalInfo(key: String, content: String) {
        upsert(type = MemoryType.PersonalInfo, key = key, content = content)
    }

    suspend fun upsertCustom(type: String, key: String, content: String, metadata: Map<String, Any> = emptyMap()) {
        val ns = namespaceProvider()
        val id = "${type}:${key}"
        val merged: Map<String, Any> = buildMap {
            put("type", type)
            put("key", key)
            putAll(metadata)
        }
        storage.add(
            documents = listOf(TextDocument(content = content, id = id, metadata = merged)),
            namespace = ns
        )
    }

    private suspend fun upsert(type: MemoryType, key: String, content: String) {
        val ns = namespaceProvider()
        val id = "${type.name}:${key}"
        val metadata: Map<String, Any> = mapOf(
            "type" to type.name,
            "key" to key,
            "schemaVersion" to 1
        )
        storage.add(
            documents = listOf(TextDocument(content = content, id = id, metadata = metadata)),
            namespace = ns
        )
    }
}
```

- [ ] **Step 2: 编译验证**

运行：

```bash
./gradlew :baseKit:compileDebugKotlin --no-daemon
```

---

### Task 6: KoogAgentKit 安装 LongTermMemory（namespace=userId+agentId）

**Files**
- Modify: `baseKit/src/main/java/me/shetj/base/tools/app/KoogAgentKit.kt`

- [ ] **Step 1: 增加 import**

```kotlin
import ai.koog.agents.longtermmemory.feature.LongTermMemory
import ai.koog.agents.longtermmemory.retrieval.SimilaritySearchStrategy
import ai.koog.agents.longtermmemory.retrieval.augmentation.SystemPromptAugmenter
import ai.koog.rag.base.TextDocument
import ai.koog.rag.base.storage.SearchStorage
import ai.koog.rag.base.storage.WriteStorage
import ai.koog.rag.base.storage.search.SimilaritySearchRequest
```

- [ ] **Step 2: createAgent(...) 增加参数并安装 Feature**

新增参数（建议）：

```kotlin
longTermSearchStorage: SearchStorage<TextDocument, SimilaritySearchRequest>? = null,
longTermWriteStorage: WriteStorage<TextDocument>? = null,
longTermNamespace: String? = null,
enableLongTermIngestion: Boolean = false,
```

安装逻辑（Retrieval 默认启用）：

```kotlin
private fun AIAgent.Builder<String, String>.installLongTermMemory(
    searchStorage: SearchStorage<TextDocument, SimilaritySearchRequest>?,
    writeStorage: WriteStorage<TextDocument>?,
    namespace: String?,
    enableIngestion: Boolean
) {
    if (searchStorage == null || namespace.isNullOrBlank()) return
    install(LongTermMemory) {
        retrieval {
            storage = searchStorage
            this.namespace = namespace
            searchStrategy = SimilaritySearchStrategy(topK = 5, similarityThreshold = 0.0)
            promptAugmenter = SystemPromptAugmenter()
        }
        if (enableIngestion && writeStorage != null) {
            ingestion {
                storage = writeStorage
                this.namespace = namespace
            }
        }
    }
}
```

- [ ] **Step 3: 编译验证**

运行：

```bash
./gradlew :baseKit:compileDebugKotlin --no-daemon
```

---

### Task 7: 文档与索引更新

**Files**
- Modify: `doc/koog/SKILL.md`
- Ensure: `doc/koog/KOOG_LONG_TERM_MEMORY.md`

- [ ] **Step 1: SKILL.md 增加入口链接**

在“另见”区域增加：

```md
- 另见：[KOOG_LONG_TERM_MEMORY.md](KOOG_LONG_TERM_MEMORY.md)（Long-term memory：本地 Room 落地与可扩展 schema）
- 另见：[plans/2026-05-26-long-term-memory-room.md](plans/2026-05-26-long-term-memory-room.md)（本地 Room 长期记忆实现计划）
```

---

## 自检清单

- 依赖已接入：`agents-features-longterm-memory`
- namespace 已确认：`userId + agentId`
- 本地存储：Room（无加密）
- 业务写入：强类型 + custom 扩展口
- Retrieval 可工作：对话前注入检索结果
- Ingestion 默认关闭：避免噪声入库
