# Changelog

My awesome project that provides a lot of useful features, like:

- Feature 1
- Feature 2
- and Feature 3

## [2026-02-10]

### Added
- 新增 `DebugSettingsActivity` 调试设置界面，支持 HTTP 日志开关和 BaseUrl 动态修改。
- 新增 `LogViewerActivity` 日志查看界面，支持关键字搜索、级别过滤和文件切换。
- 新增 `LogManager` 日志管理系统，支持异步缓冲写入、文件分割和 Crash 同步保护。
- 新增 `LogConfig` 和 `LogRecord`，提供灵活的日志配置。

### Changed
- 重构 `BaseUncaughtExceptionHandler`，对接 `LogManager` 实现 Crash 日志的可靠保存。
- 重构 `DebugFunc`，将其作为 `LogManager` 的兼容性门面，底层逻辑全面升级。

### Deprecated

### Removed

### Fixed

### Security

## [2026-04-16]

### Added
- 新增 `TimeCostKit`，提供 `timedAsync` / `timedAsyncIO` 用于统计协程代码块耗时并输出日志。

### Changed
- 增强 `TimeCostKit`：新增 `timedAsyncCost` / `timedAsyncCostIO` 支持返回耗时（毫秒）。

## [2026-04-30]

### Added
- 接入 Koog（JetBrains AI Agent Framework）：新增 `ai.koog:koog-agents` 依赖，供 app 模块使用。
- 新增 `KoogAgentKit` 工具类，封装 AI Agent 的创建和执行，支持多种 LLM 提供商：OpenAI, Anthropic, Google, DeepSeek, OpenRouter, Bedrock, Mistral, Ollama。

## [2026-05-22]

### Added
- 新增 Koog 智能体模块构建文档 `doc/koog/SKILL.md`，记录核心架构、依赖接入与快速复刻步骤。
- 新增 `doc/koog/KOOG_AGENTS.md`：Koog-agents 的核心概念、Agent 类型与常见用法说明（结合 BaseKit 现状）。
- 新增 `doc/koog/KOOG_PROMPTS.md`：Koog Prompt 的创建方式、消息类型、工具消息与参数说明。
- 新增 `doc/koog/KOOG_TOOLS.md`：Koog Tools 的类型、ToolRegistry 注册与使用路线总结。
- 新增 `doc/koog/KOOG_FEATURES.md`：Koog Features 的用途、Tracing/OpenTelemetry 与自定义 Feature 骨架总结。
- 新增 `doc/koog/KOOG_LLM_PARAMETERS.md`：Koog LLM 参数（temperature/toolChoice/maxTokens 等）总结与写作场景建议。
- 新增 `doc/koog/KOOG_MODEL_CAPABILITIES.md`：Koog Model capabilities（LLMCapability/LLModel）总结与选型建议。
- 新增 `doc/koog/KOOG_CONTENT_MODERATION.md`：Koog Content moderation（内容审核）用法与落地建议。
- 新增 `doc/koog/KOOG_HISTORY_COMPRESSION.md`：Koog History compression（历史压缩）用法、策略与保留 memory 说明。
- 新增 `doc/koog/KOOG_MCP.md`：Koog 集成 MCP（Model Context Protocol）用法总结。
- 新增 `doc/koog/KOOG_A2A.md`：Koog A2A Protocol 用法总结（client/server/integration）。
- 新增 `doc/koog/KOOG_ACP.md`：Koog ACP（Agent Client Protocol）对接 IDE/客户端用法总结。
- 新增 `doc/koog/KOOG_BACKEND_INTEGRATIONS.md`：Koog 服务端框架集成（Spring Boot/Ktor）总结。
- 新增 `doc/koog/KOOG_ADVANCED_USAGE.md`：Koog 进阶能力索引与写作助手落地建议。
- 新增 `doc/koog/MODEL_LANDSCAPE.md`：主流大模型“知识图谱”式总结（按家族/能力维度）。
- Koog 设置页新增“写作助手预设”，支持一键填充 systemPrompt。
- Koog 聊天链路接入 InspirationTool，支持手动指令与自动触发的灵感生成。
- Koog 灵感生成支持“工具先产出 -> 模型再扩写”的二段式输出（已配置 Agent 时生效）。

### Changed
- Koog 创建 Agent 时注入当前 Agent 的 systemPrompt（Koog systemPrompt 能力），避免每次发送拼接 systemPrompt 字符串。
- Koog 聊天发送增加多轮对话上下文拼接（截断历史消息），提升写作助手对前文的连续性。
- Koog 灵感工具调用轨迹改为通过 Prompt 的 toolCall/toolResult 消息写入，让模型更可控利用工具结果。
- 更新 `doc/koog/KOOG_FEATURES.md`：补充内置 Features 清单并增加写作助手案例。
- 更新 `doc/koog/MODEL_LANDSCAPE.md`：补充能力矩阵、官方入口清单与任务选型配方。

## [2026-05-26]

### Added
- 新增 Koog Long-term memory 落地文档 `doc/koog/KOOG_LONG_TERM_MEMORY.md`（本地 Room、namespace=userId+agentId、支持自定义扩展）。
- 新增 Koog ChatMemory Room 持久化文档 `doc/koog/KOOG_CHAT_MEMORY_ROOM.md`，并在 `doc/koog/SKILL.md` 增加索引入口。
- 新增实现计划 `doc/koog/plans/2026-05-26-long-term-memory-room.md` 并在 `doc/koog/SKILL.md` 增加索引入口。
- baseKit 新增长期记忆 Room 存储层：`LongTermMemoryDatabase` / `MemoryRecordEntity` / `MemoryRecordDao`。
- baseKit 新增 `RoomTextDocumentStorage`（适配 Koog RAG 的 SearchStorage/WriteStorage）与 `LocalLongTermMemoryKit`（强类型写入 + 自定义扩展）。
 - baseKit 新增 ChatMemory Room 存储层：`ChatMemoryDatabase` / `ChatMemoryDao` / `ChatMemoryMessageEntity`，并提供 `RoomChatHistoryProvider` 实现 `ChatHistoryProvider`。

### Changed
- baseKit 的 `KoogAgentKit.createAgent` 支持安装 `LongTermMemory`，按 userId+agentId 生成 namespace（默认不启用自动入库）。
- app 的 `KoogChatViewModel` 创建 Agent 时自动注入本地 Room 长期记忆存储（Search/Write），使对话可直接检索历史偏好与画像。
- app 的 `KoogSettingsScreen` 新增“记忆管理”入口：写入偏好、查看最近记录、支持删除。
- app 的 `ChatHistoryManager` 由 DataStore 切换为复用 `ChatMemoryDatabase`（Room），用于 UI 聊天消息持久化并与 ChatMemory 共享同一份会话数据。
- app 的 UI 不再主动写入聊天历史，避免与 Koog `ChatMemory` 的自动 store 重复写入同一会话数据。
