# Koog Model Context Protocol（MCP）用法总结

本文档总结 Koog 如何集成 MCP（Model Context Protocol）：连接 MCP Server、把 MCP 工具转成 Koog Tools、注册到 ToolRegistry，并让 Agent/LLM 调用这些工具。

## 1. MCP 是什么

MCP（Model Context Protocol）是一套标准协议，让 AI Agent 以统一方式调用外部工具/服务。MCP 将工具与 prompts 以 API 端点形式暴露，并用 JSON Schema 描述输入输出。

Koog 通过 `agent-mcp` 模块集成 MCP，提供从“连接 -> 拉取工具 -> 转换 -> 注册 -> 调用”的完整链路。

## 2. MCP Server 与传输方式

MCP Server 可来自 MCP Marketplace / DockerHub，也可自建。Koog 支持两种连接方式：

- **stdio**：MCP server 作为独立进程运行（例如 Docker、CLI），通过 stdin/stdout 通信。
- **SSE**：MCP server 作为 HTTP 服务，通过 Server-Sent Events 通信。

## 3. Koog 集成 MCP 的关键组件（理解用）

- `McpTool`：把 MCP 工具桥接到 Koog Tool 接口。
- `McpToolDescriptorParser`：把 MCP 的 tool 定义解析成 Koog 的 tool descriptor。
- `McpToolRegistryProvider`：基于 stdio/SSE/client 创建 ToolRegistry。

## 4. 快速上手（标准流程）

### 4.1 建立连接

stdio（进程）示意：

```kotlin
val process = ProcessBuilder("path/to/mcp/server").start()
val transport = McpToolRegistryProvider.defaultStdioTransport(process)
```

SSE（HTTP）示意：

```kotlin
val transport = McpToolRegistryProvider.defaultSseTransport("http://localhost:8931")
```

### 4.2 创建 ToolRegistry

方式一：用 transport + serverInfo

```kotlin
val toolRegistry = McpToolRegistryProvider.fromTransport(
    transport = transport,
    serverInfo = McpServerInfo(url = "http://localhost:8931", command = "path/to/mcp/server"),
    name = "my-client",
    version = "1.0.0"
)
```

方式二：已有 MCP client

```kotlin
val toolRegistry = McpToolRegistryProvider.fromClient(
    mcpClient = existingMcpClient,
    serverInfo = McpServerInfo(url = "http://localhost:8931")
)
```

### 4.3 传入 Agent 并运行

```kotlin
val agent = AIAgent(
    promptExecutor = executor,
    llmModel = OpenAIModels.Chat.GPT4o,
    toolRegistry = toolRegistry
)

agent.run("Use the MCP tool to perform a task")
```

## 5. 示例（官方思路复述）

- **Google Maps MCP**：作为 Docker 进程（stdio）运行，Koog 通过 `fromProcess`/stdio 拉取工具并调用。
- **Playwright MCP**：作为 SSE 服务运行（`npx @playwright/mcp`），Koog 通过 `fromSseUrl` 拉取工具并调用。

## 6. 结合 BaseKit 的落地建议

- 如果你希望“LLM 自主决定调用 MCP 工具”，推荐走 **ToolRegistry + tool calling 循环**（Graph/Functional/Planner 里都能做）。
- 如果你希望“工程强控”，可以像当前 InspirationTool 一样先在 App 侧触发 MCP 调用，再把 toolCall/toolResult 写入 Prompt，让模型只负责扩写/整理。

另见：

- `KOOG_TOOLS.md`（ToolRegistry 与工具调用整体路线）
- `KOOG_PROMPTS.md`（toolCall/toolResult 消息写入方式）

