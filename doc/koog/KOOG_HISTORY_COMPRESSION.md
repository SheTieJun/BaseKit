# Koog History compression（历史记录压缩）用法总结

本文档总结 Koog 的历史记录压缩（History compression）：何时压缩、如何在 graph strategy / custom node 中压缩、内置压缩策略与自定义策略，以及压缩时如何保留 memory 相关消息。

## 1. 为什么需要历史压缩

Agent 的 message history 会随着对话增长（user/assistant/toolCall/toolResult 都会堆进来）。长对话会带来：

- token 占用过高，触发上下文上限或请求失败
- 性能下降，响应变慢
- 成本上升
- 历史噪声干扰模型，准确率下降

历史压缩的目标：把一长串 messages 总结成少量 TLDR 消息，仅保留后续推理所需的关键信息。

## 2. 什么时候压缩

Koog 文档建议的压缩时机：

- **逻辑阶段之间（subgraph 之间）**：例如“收集信息”结束后压缩，再进入“做决策/写作输出”阶段。
- **上下文过长时**：例如 message 数超过阈值（如 100 条）再压缩。

## 3. 怎么实现（两条路线）

### 3.1 在 strategy graph 中压缩（推荐）

使用预置节点：

- Kotlin：`nodeLLMCompressHistory`
- Java：`AIAgentNode.llmCompressHistory()`

典型做法：在 edge condition 中判断 history 是否过长，满足则插入 compress 节点。

要点：压缩节点可以“携带”某些中间态输入（例如 `ReceivedToolResults`）传给下一个节点，避免丢失关键执行结果。

### 3.2 在 custom node 中压缩（更灵活）

在自定义节点里直接调用：

```kotlin
llm.writeSession {
    replaceHistoryWithTLDR()
}
```

适合：你需要在任意时点、按更复杂条件触发压缩。

## 4. 内置压缩策略（HistoryCompressionStrategy）

Koog 提供多种内置策略，可通过参数指定：

- Kotlin：`nodeLLMCompressHistory(strategy=...)` 或 `replaceHistoryWithTLDR(strategy=...)`
- Java：`.compressionStrategy(...)`

### 4.1 WholeHistory（默认）

压缩整个 history 成一个 TLDR，保留“到目前为止完成了什么”的整体状态。

适合：通用对话/通用助手。

### 4.2 FromLastNMessages(n)

只压缩最后 n 条消息，并丢弃更早内容。

适合：只有最新上下文有效的任务（例如只关心最近的改稿指令）。

### 4.3 Chunked(chunkSize)

把 history 按固定 size 分块，每块分别压缩成 TLDR，最终保留多个 TLDR（能保留阶段性进度）。

适合：长任务多阶段，需要“早期信息仍可能重要”，但又不能无限膨胀。

### 4.4 FactRetrievalHistoryCompressionStrategy（按概念提取事实）

给定一组 Concept（关键词 + 描述 + FactType SINGLE/MULTIPLE），让 LLM 从历史中检索与这些概念相关的事实，并用这些事实替换 history。

适合：你明确知道后续需要哪些事实（例如“用户偏好/世界观设定/已解决问题”），希望压缩后只保留这些“可用事实”。

## 5. 自定义压缩策略（仅 Kotlin）

Koog 文档说明：自定义压缩策略只在 Kotlin 可用。

思路：

- 继承 `HistoryCompressionStrategy` 抽象类
- 实现 `compress` 方法
- 示例是过滤包含特定关键词（如 “important”）的消息，只保留它们

## 6. 压缩时保留 memory 相关消息（preserveMemory）

Koog 的历史压缩支持“保留 memory 相关 messages”：

- Kotlin：使用 `preserveMemory` 参数
- Java：使用 `.preserveMemory()` builder 方法

这用于避免把“记忆类消息”在压缩时误删，导致长期记忆/会话记忆失效。

## 7. 写作助手建议（BaseKit 视角）

- 你当前在 App 侧做了“截断最近 N 条消息”，这是最轻量的压缩手段。
- 若未来迁移到 Koog graph/planner（或引入 Chat memory Feature），建议：
  - 阈值触发压缩：messages > 100（或按 token 估算）
  - 写作任务更偏“阶段推进”：可优先考虑 `Chunked` 或“阶段间 WholeHistory”
  - 世界观/人物卡等关键信息：可用 FactRetrieval 按概念提取，压缩后只保留“设定事实”

