# Koog ChatMemory（Room 持久化）

本文档记录在 BaseKit 中如何用 Room 实现 Koog `ChatHistoryProvider`，用于 `ChatMemory` 的“对话历史持久化”（短期上下文），使多轮对话在 App 重启后仍可继续。

## 背景

- ChatMemory 负责“按 sessionId 自动加载/保存历史消息”，用于多轮对话上下文。
- 默认实现 `InMemoryChatHistoryProvider` 只存在内存中，进程重启后会丢失。
- BaseKit 通过 Room 实现 `ChatHistoryProvider`，实现持久化存储。

## 接入点

- `KoogAgentKit.createAgent(...)` 已支持 `chatHistoryProvider` 参数，并在内部 `install(ChatMemory)` 安装 Feature。
- app 侧在 `KoogChatViewModel` 创建 Agent 时注入 `RoomChatHistoryProvider`。
- app 侧的 `ChatHistoryManager`（用于 UI 消息列表持久化）也已切换为复用同一套 `ChatMemoryDatabase`，避免 DataStore 与 Room 两套历史并存。

## 去重写入

为避免同一会话被“双写”（UI 自己保存一次 + ChatMemory 自动保存一次），当前策略是：

- UI 不再主动调用 `ChatHistoryManager.saveMessages(...)`。
- 历史落盘由 Koog `ChatMemory` 在每次 `agent.run(..., sessionId)` 后自动触发 `ChatHistoryProvider.store(...)` 完成。
- UI 在 Agent 切换或重启时，通过 `ChatHistoryManager.loadMessages(...)` 从 `ChatMemoryDatabase` 读取并恢复列表。

## Room 表结构

表：`koog_chat_memory_message`

- `conversationId`：会话 ID（即 `agent.run(text, sessionId)` 的 sessionId）
- `seq`：消息顺序（同一会话内从 0 开始递增）
- `role`：`user` / `assistant`
- `content`：消息内容
- `createdAt`：写入时间

说明：

- 由于 BaseKit 的 `ChatMemory` 配置中会 `filterMessages { it is Message.User || it is Message.Assistant }`，因此 Room 只持久化 user/assistant 两类消息。
- 若后续需要持久化 toolCall/toolResult，需要扩展 role 与 content 的结构化存储策略。

## 代码位置

- Room：`me.shetj.base.tools.app.memory.chat`
  - `ChatMemoryMessageEntity`
  - `ChatMemoryDao`
  - `ChatMemoryDatabase`
- Provider：`RoomChatHistoryProvider`（实现 `ai.koog.agents.chatMemory.feature.ChatHistoryProvider`）

## 使用示例

```kotlin
val provider = RoomChatHistoryProvider(
    ChatMemoryDatabase.getInstance(context).chatMemoryDao()
)

val agent = KoogAgentKit.createAgent(
    provider = KoogAgentKit.Provider.OPENAI,
    apiKey = "...",
    modelName = "gpt-4o-mini",
    chatHistoryProvider = provider
)

agent.run("你好", "session-1")
agent.run("继续", "session-1")
```

## 约定与建议

- sessionId 建议具备隔离性：例如 `userId + agentId` 或 `userId + agentId + 场景`。
- ChatMemory 适合存“短期上下文”；长期偏好/学习画像建议使用 `LongTermMemory`（RAG）方案。
