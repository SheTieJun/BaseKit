# Koog Advanced usage（进阶用法）总结

本文档把 Koog 文档里的“Advanced usage”方向做工程化归纳，重点回答：什么时候需要用、在项目里怎么组织、以及与 BaseKit 写作助手的组合方式。

## 1. History compression（历史压缩）

用途：长对话/长任务降低 token 与噪声。

怎么用：

- graph strategy：`nodeLLMCompressHistory`
- custom node：`replaceHistoryWithTLDR()`
- 内置策略：WholeHistory / FromLastNMessages / Chunked / FactRetrieval
- memory 保留：`preserveMemory`

另见：`KOOG_HISTORY_COMPRESSION.md`

## 2. Structured output（结构化输出）

用途：把 LLM 输出稳定落盘成结构体（人物卡/大纲/分章计划），避免靠解析不稳定的自由文本。

怎么用（Koog 提供三层 API）：

- PromptExecutor 层：`executeStructured<T>(prompt, model, examples, fixingParser)`
- Agent session 层：`requestLLMStructured<T>(...)`
- Node 层：`nodeLLMRequestStructured<T>(...)` 形成可复用节点

关键点：

- 需要配合 `@Serializable`、`@LLMDescription` 描述字段含义
- Koog 会基于 model capabilities 选择“原生结构化/提示词注入”，并可用 `StructureFixingParser` 自动修复不合法 JSON

官方页：`structured-output`

## 3. Streaming API（流式输出 + 并行 tool calls）

用途：长文本实时渲染；工具调用可以边流边触发；更好的交互体验。

怎么用：

- `requestLLMStreaming()` 返回 `Flow<StreamFrame>`
- 帧类型：TextDelta/ReasoningDelta/ToolCallDelta + TextComplete/ToolCallComplete + End
- 可在 event handlers 里监听 `onLLMStreamingFrameReceived`

官方页：`streaming-api`

## 4. Content moderation（内容审核）

用途：输入/输出/工具内容三段式安全控制。

怎么用：

- `LLMClient.moderate(prompt, model)`
- `PromptExecutor.moderate(prompt, model)`

另见：`KOOG_CONTENT_MODERATION.md`

## 5. Knowledge retrieval / Embeddings / RAG（知识检索）

用途：长周期知识注入（资料库/设定集/世界观档案），用 embedding 做相似度检索，再把命中片段注入 prompt。

工程建议：

- 写作助手更常见的“知识”其实是：人物卡/世界观/设定事实；可以先用 Long-term memory / Fact retrieval 压缩做轻量版本
- 真要上 RAG，再引入向量库与 embeddings（避免一开始复杂度过高）

## 6. Agent persistence（断点续跑）

用途：复杂工作流（Planner/Graph）中保存/恢复 agent 状态。

适用：章节生成、改稿多轮循环、计划执行类任务。

## 7. Tracing / OpenTelemetry（可观测）

用途：调试、复盘、性能与成本分析。

怎么用：

- Tracing：本地/灰度记录一次 run 的轨迹
- OpenTelemetry：导出 spans/traces 到 Datadog/Langfuse 等；默认会 mask prompt/response 内容，必要时 `setVerbose(true)`

另见：`KOOG_FEATURES.md`

## 8. LLM switching / fallback（多模型切换与降级）

用途：失败自动切换、成本与质量平衡、不同阶段用不同模型（脑暴 vs 结构化输出 vs 改写）。

落地方式：

- 使用 `MultiLLMPromptExecutor` 做 provider fallback
- 或在业务层按能力与成本选择模型（依赖 `KOOG_MODEL_CAPABILITIES.md`）

## 9. 与 BaseKit 写作助手的组合建议（最短路径）

建议优先顺序：

1. **Structured output**：人物卡/大纲/分章落盘（最提升稳定性的能力）
2. **History compression**：长对话不爆上下文
3. **Moderation**：输入/输出安全底线
4. **Streaming**：体验优化
5. **RAG / Persistence**：当你真的进入“长任务/长记忆”阶段再接

