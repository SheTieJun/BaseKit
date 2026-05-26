# Koog Long-term memory（本地 Room 落地）

本文档描述如何在 BaseKit 项目中落地 Koog `LongTermMemory`（长期记忆）：本地持久化、按 `userId + agentId` 隔离、覆盖偏好/学习进度/常错点/个人信息，并支持自定义扩展。

## 目标

- 本地长期记忆：App 重启后仍可检索到“历史事实/偏好/学习画像”。
- 记忆隔离：`namespace = "${userId}_${agentId}"`，不同用户、不同智能体互不干扰。
- 可扩展：内置 4 类记忆类型，同时支持自定义类型与 metadata 扩展。
- 可控入库：默认只支持“业务显式写入”，避免把所有对话噪声写进长期记忆。

## 与 ChatMemory 的区别

- ChatMemory：对话历史（短期上下文），按 sessionId 存取 message 列表。
- LongTermMemory：可检索的“文档记忆”（长期上下文），按 query 在记忆库中搜索相关记录并注入 Prompt（RAG 思路）。

## 依赖

需要新增 Koog 长期记忆模块（Koog 0.8.0）：

```kotlin
dependencies {
    api(libs.koog.agents.features.longterm.memory)
}
```

对应包名（Koog 0.8.0）：

```kotlin
import ai.koog.agents.longtermmemory.feature.LongTermMemory
import ai.koog.agents.longtermmemory.retrieval.SimilaritySearchStrategy
import ai.koog.agents.longtermmemory.retrieval.augmentation.SystemPromptAugmenter
import ai.koog.rag.base.TextDocument
import ai.koog.rag.base.storage.search.SimilaritySearchRequest
```

## 设计：本地 Room 作为 LongTermMemory Storage

### 存储模型

长期记忆以 `TextDocument` 形式存储：

- `id`：记录唯一 ID（建议 `${type}:${key}` 或随机 UUID）
- `content`：可检索的文本内容（用于匹配）
- `metadata`：结构化字段（`Map<String, Any>`），用于类型、key、标签、版本、来源等（落盘时建议转为 JSON 字符串保存）

BaseKit 实现中使用 Koog 提供的 `MemoryRecord`（实现了 `TextDocument`）作为默认文档载体。

Room 表建议字段：

- `id`（主键）
- `namespace`（索引；userId+agentId）
- `type`（索引；preference/progress/mistake/personal/custom）
- `key`（索引；例如 tone / level / wrong:的地得）
- `content`（全文）
- `metadataJson`（JSON 字符串）
- `updatedAt`（毫秒时间戳）

### 检索策略（MVP）

Koog 的 `SimilaritySearchRequest` 需要一个“相似度检索”，但在本地 MVP 阶段可以先用：

- `LIKE %query%` 的关键词/子串匹配（上线可用，效果一般）
- `topK`：限制返回条数
- `similarityThreshold`：本地实现阶段可映射为“是否命中”的 0/1 分数

后续升级路径：

- 引入 embedding（本地或云端）+ 向量索引（HNSW/FAISS/SQLite 扩展等）
- `content` 存原文，metadata 存 embedding 版本与向量引用

## 在 Agent 创建时安装 LongTermMemory

推荐默认：

- `retrieval`：开启自动检索注入
- `ingestion`：默认关闭（只允许业务显式写入）

示例（Kotlin）：

```kotlin
install(LongTermMemory) {
    retrieval {
        storage = roomSearchStorage
        namespace = "${userId}_${agentId}"
        searchStrategy = SimilaritySearchStrategy(topK = 5, similarityThreshold = 0.0)
        promptAugmenter = SystemPromptAugmenter()
    }
}
```

## 业务侧写入建议（可控入库）

建议把“用户画像”拆成强类型 API，统一由 `LocalLongTermMemoryKit` 写入：

- 偏好：写作风格、语气、禁忌、输出格式等
- 学习进度：当前等级、最近复习、下次复习、正确率等
- 常错点：常错字/易混淆点、错因摘要、错误次数等
- 个人信息：姓名/目标/背景等（默认不自动写入，建议 UI 提示并让用户确认）

自定义扩展：

- `custom(type, key, content, metadata)` 允许业务模块自行定义记忆类型与字段

## 推荐 namespace 规则

- `namespace = "${userId}_${agentId}"`
- 同一用户同一 agent 的长期记忆持续复用；不同 agent 保持隔离，避免“写作助手”与“汉字学习助手”相互污染。

## 操作记录（本次落地要做的改动点）

- Version Catalog：新增 `ai.koog:agents-features-longterm-memory`
- BaseKit：新增 Room 存储适配层（SearchStorage/WriteStorage）
- KoogAgentKit：createAgent 支持安装 LongTermMemory（Retrieval 默认开启；Ingestion 可配置）
- 文档：更新 `doc/koog/SKILL.md` 增加索引入口
