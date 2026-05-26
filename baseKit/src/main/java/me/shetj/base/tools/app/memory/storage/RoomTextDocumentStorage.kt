package me.shetj.base.tools.app.memory.storage

import ai.koog.agents.longtermmemory.model.MemoryRecord
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
        val ns = namespace.orEmpty().ifBlank { "default" }
        val entities = documents.map { doc ->
            val id = doc.id ?: error("TextDocument.id 不能为空")
            val metadataJson = JSONObject(doc.metadata).toString()
            MemoryRecordEntity(
                id = id,
                namespace = ns,
                type = (doc.metadata["type"] as? String).orEmpty(),
                key = (doc.metadata["key"] as? String).orEmpty(),
                content = doc.content,
                metadataJson = metadataJson,
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.upsertAll(entities)
        return entities.map { it.id }
    }

    override suspend fun update(documents: Map<String, TextDocument>, namespace: String?): List<String> {
        val ns = namespace.orEmpty().ifBlank { "default" }
        val entities = documents.map { (id, doc) ->
            val metadataJson = JSONObject(doc.metadata).toString()
            MemoryRecordEntity(
                id = id,
                namespace = ns,
                type = (doc.metadata["type"] as? String).orEmpty(),
                key = (doc.metadata["key"] as? String).orEmpty(),
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
        val ns = namespace.orEmpty().ifBlank { "default" }
        val records = dao.searchByContent(ns, request.queryText, request.limit)
        val threshold = request.minScore ?: 0.0
        return records.mapNotNull { entity ->
            val score = Score(1.0, ScoreMetric.COSINE_SIMILARITY)
            if (score.value < threshold) return@mapNotNull null
            SearchResult(
                document = MemoryRecord(entity.content, entity.id, entity.metadataJson.toAnyMap()),
                score = score,
                id = entity.id,
                namespace = ns
            )
        }
    }

    override suspend fun delete(ids: List<String>, namespace: String?): List<String> {
        val ns = namespace.orEmpty().ifBlank { "default" }
        dao.deleteByIds(ns, ids)
        return ids
    }

    override suspend fun get(ids: List<String>, namespace: String?): List<TextDocument> {
        val ns = namespace.orEmpty().ifBlank { "default" }
        return dao.getByIds(ns, ids).map { entity ->
            MemoryRecord(entity.content, entity.id, entity.metadataJson.toAnyMap())
        }
    }

    private fun String.toAnyMap(): Map<String, Any> {
        if (isBlank()) return emptyMap()
        val obj = JSONObject(this)
        return buildMap {
            val it = obj.keys()
            while (it.hasNext()) {
                val key = it.next()
                val parsed = obj.opt(key).toAnyValue()
                if (parsed != null) put(key, parsed)
            }
        }
    }

    private fun Any?.toAnyValue(): Any? {
        return when (this) {
            null -> null
            is JSONObject -> {
                buildMap {
                    val it = keys()
                    while (it.hasNext()) {
                        val key = it.next()
                        val v = opt(key)
                        val parsed = v.toAnyValue()
                        if (parsed != null) put(key, parsed)
                    }
                }
            }

            is JSONArray -> {
                buildList {
                    for (i in 0 until length()) {
                        val v = opt(i)
                        val parsed = v.toAnyValue()
                        if (parsed != null) add(parsed)
                    }
                }
            }

            JSONObject.NULL -> null
            else -> this
        }
    }
}
