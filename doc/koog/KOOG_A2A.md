# Koog A2A Protocol 用法总结（Overview + Koog Integration）

本文档沉淀 A2A（Agent-to-Agent）协议在 Koog 中的使用方式：协议概念、client/server 组件，以及 Koog 内如何通过 Feature 与节点把 A2A 接进 agent 策略。

## 1. A2A 是什么

A2A（Agent-to-Agent）是一套标准化协议，用于 AI agent 与其它 agent/客户端之间的互操作通信。Koog 提供 A2A v0.3.0 的 client、server 实现，并提供与 Koog agent 框架的集成层。

## 2. Koog 的 A2A 三个组件

- **A2A Server**：对外暴露 A2A endpoint，接收请求、执行任务、返回结果/状态更新。
- **A2A Client**：发起请求到 A2A server，处理返回的 task/event（可 streaming）。
- **A2A Koog Integration**：把 A2A client/server 能力封装成 Koog 的 features/nodes，方便在 strategy graph 中使用。

## 3. 依赖（重点：默认不随 koog-agents 引入）

A2A 相关依赖不包含在 `koog-agents` 的 meta 依赖中，需要按使用场景显式添加：

### 3.1 作为 A2A Server 暴露 Koog agent

```
implementation("ai.koog:agents-features-a2a-server:$koogVersion")
implementation("ai.koog:a2a-transport-server-jsonrpc-http:$koogVersion")
implementation("io.ktor:ktor-server-netty:$ktorVersion")
```

### 3.2 作为 A2A Client 去调用外部 A2A agent

```
implementation("ai.koog:agents-features-a2a-client:$koogVersion")
implementation("ai.koog:a2a-transport-client-jsonrpc-http:$koogVersion")
implementation("io.ktor:ktor-client-cio:$ktorVersion")
```

## 4. 两种典型模式（你要先选哪个）

### 4.1 暴露 Koog agent 为 A2A Server

思路：

- 在 agent 构造时 `install(A2AAgentServer)`，并提供 `RequestContext` 与 `SessionEventProcessor`
- 在策略节点里用 `withA2AAgentServer { ... }` 发 task update（Submitted/Working/Completed）
- 对 LLM 返回的 toolCall：执行 tool -> 把 tool result 写回 llm session -> 再继续请求 LLM

适合：

- 你要把“写作助手”作为服务接到 IDE/其它前端/其它 agent
- 你需要标准协议与可流式的任务事件

### 4.2 让 Koog agent 作为 A2A Client 调用外部 agent

思路：

- 建立 A2A client（HTTP JSON-RPC transport + AgentCard resolver）
- 在 Koog agent 上 `install(A2AAgentClient)` 注入 client map
- 在 graph strategy 里先检查对方是否支持 streaming，再选择 send message / send message streaming 节点

适合：

- 你要把“外部检索 agent/翻译 agent/合规 agent”接进你的写作工作流

## 5. 关键概念：AgentCard 与 capabilities

A2A server 通过 AgentCard 描述自身能力与技能，其中 `capabilities.streaming` 决定 client 是否能走 streaming 通道。

## 6. 与 BaseKit 写作助手的关系（建议）

- 如果你未来希望“写作助手”可在桌面端/IDE 内复用，A2A 是更标准的服务暴露方式。
- 如果你只是 App 内部能力，不一定需要 A2A；更优先把 Tools/Features/History compression/Structured output 做稳。

另见：

- `KOOG_ACP.md`（ACP：更偏 IDE 侧的标准协议）
- `KOOG_BACKEND_INTEGRATIONS.md`（Ktor/Spring 场景下如何做服务端集成）

