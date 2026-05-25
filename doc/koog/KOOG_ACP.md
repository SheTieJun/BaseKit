# Koog Agent Client Protocol（ACP）用法总结

本文档沉淀 ACP（Agent Client Protocol）在 Koog 中的用法：如何把 Koog agent 接到 ACP 生态（例如 IDE），并通过双向事件流对外输出 tool call、thought、完成状态等。

## 1. ACP 是什么

ACP（Agent Client Protocol）是一个开源标准协议，让 client 应用（例如 IDE）以一致、双向的方式与 AI agent 通信。Koog 通过 ACP Kotlin SDK 集成 ACP，并提供额外的 API 扩展以适配 Koog 的事件系统与多模态消息。

注意：ACP Kotlin SDK 是 JVM 专用，因此 Koog 的 ACP 集成目前仅 JVM 可用。

## 2. 依赖

ACP 支持是可选 feature，需要显式引入：

```
implementation("ai.koog:agents-features-acp:$koogVersion")
```

## 3. 启用 ACP（核心：安装 AcpAgent feature）

Koog 通过安装 `AcpAgent` feature，将 Koog agent 的内部事件桥接成 ACP session event 并发送给 client：

```kotlin
val agent = AIAgent(
    promptExecutor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
    llmModel = OpenAIModels.Chat.GPT4o
) {
    install(AcpAgent) {
        this.sessionId = sessionId
        this.protocol = protocol
        this.eventsProducer = eventsProducer
        this.setDefaultNotifications = true
    }
}
```

关键配置项：

- `sessionId`：会话唯一标识
- `protocol`：ACP 底层通信协议对象
- `eventsProducer`：用于发送 `Event` 的 `ProducerScope`（典型是 `channelFlow {}`）
- `setDefaultNotifications`：是否使用默认事件映射（tool call 生命周期、LLM 返回、失败、完成等）

## 4. 实现一个 ACP-enabled agent（AgentSupport / AgentSession）

ACP Kotlin SDK 侧需要实现两个接口：

- `AgentSupport`：agent 的身份、能力与 session 生命周期（create/load session）
- `AgentSession`：单次会话执行，负责处理 prompt 与取消

典型结构（要点版）：

- `AgentSession.prompt(...)` 用 `channelFlow` 返回事件流
- 在 `channelFlow` 内创建 Koog agent 并 `install(AcpAgent)`，把 `eventsProducer` 指向 `channelFlow`
- 用 `Mutex` 确保同一 session 不会并发触发多个 agent 运行（ACP 不应让新的执行覆盖旧执行）

## 5. Event streaming 与同步

### 5.1 Event streaming

`AgentSession.prompt()` 返回 `Flow<Event>`，通过 `channelFlow` 可在多个协程里安全发事件。

### 5.2 Execution synchronization

常见做法：

- `agentMutex.withLock { create agent -> run -> await }`
- `agentJob = async { agent.run(...) }` 并在 cancel 时取消 job

## 6. 输入输出转换（Koog Message <-> ACP ContentBlock）

ACP client 输入是 `List<ContentBlock>`，Koog 侧通过扩展函数转换为 Koog 的消息：

- `List<ContentBlock>.toKoogMessage()`
- `ContentBlock.toKoogContentPart()`

Koog 输出到 ACP：

- `Message.Response.toAcpEvents()`
- `ContentPart.toAcpContentBlock()`

## 7. 自定义事件

如果 `setDefaultNotifications = false`，你可以自行订阅/映射 Koog 事件并发送 ACP events；或在 `withAcpAgent { sendEvent(...) }` 里发送自定义进度、计划、状态等。

## 8. 与 BaseKit 的关系（建议）

- 如果你希望把“写作助手”接入 JetBrains IDE 的自定义 agent（类似外部进程 agent），ACP 是最贴近 IDE 的标准协议。
- 若你只做 App 内聊天，对 ACP 的需求不强；优先把 tools/structured output/history compression 做稳，再考虑对外接入。

