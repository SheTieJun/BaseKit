# Koog 中的 Function Call / SKILL / MCP 对齐说明

本文档用于把常见术语（function call、skill、MCP）与 Koog 的能力边界对齐，方便在 BaseKit 内落地一致的工程实现方式。

## 术语对齐结论

### Function Call（函数调用）

在 Koog 中对应 **Tools**：

- 定义：`Tool` / `SimpleTool<TArgs>`
- 注册：`ToolRegistry`
- 执行：Agent 在运行中触发工具调用，框架负责“模型 -> 工具 -> 工具结果 -> 模型”的闭环

工程含义：

- function call 更像“模型可调用的能力接口”，本质是可结构化输入输出的函数。
- 在 BaseKit 中，建议把 function call 能力落地为一个或多个 `SimpleTool`，并由 `KoogAgentKit.createAgent` 统一注册。

### SKILL（技能/能力包）

Koog 没有强制的“SKILL”对象，但工程上可以用两种方式等价实现：

1) **Tool 集合 + 编排策略**
   - 一组 Tool + 触发规则（显式命令 / 意图识别 / 业务流程）
   - 适合：把“查词/纠错/生成练习题”等组合成一个“汉字学习助手能力包”
2) **Agent 级封装（推荐作为最终形态）**
   - `systemPrompt + tools + features(ChatMemory/LongTermMemory/Tracing...)`
   - 对外暴露一个“专用 agent”，整体就是一个 Skill

工程含义：

- SKILL 更像“可复用的业务能力单元”，它往往包含：
  - 角色与边界（systemPrompt）
  - 可调用能力（Tools）
  - 记忆与可观测（Features）

### MCP（Model Context Protocol）

在 Koog 的落地通常对应 **Tool 的外部调用适配**：

- 你实现一个 `Tool`，在 `execute()` 内部作为 MCP Client 去访问 MCP Server（HTTP/WebSocket/SDK）
- Tool 把 MCP 的结果转换为“模型可消费的内容”（字符串/结构化片段），再回填给模型继续推理

工程含义：

- MCP 解决的是“外部工具/资源如何标准化提供给模型使用”
- Koog 更偏向“用 Tool 做协议适配层”，而不是把 MCP 固化成唯一内置对象

## 一个统一的工程视图（建议）

```mermaid
graph TD
  A[用户输入] --> B[Agent(systemPrompt)]
  B --> C{需要外部能力?}
  C -->|是| D[Tool(Function Call)]
  D --> E[MCP Client/HTTP/SDK]
  E --> F[外部服务/MCP Server]
  F --> G[工具结果]
  G --> B
  C -->|否| H[直接生成回复]
  B --> I[输出]
  B --> J[ChatMemory:短期历史]
  B --> K[LongTermMemory:RAG长期记忆]
```

## BaseKit 当前项目的对应关系（示例）

- function call：`InspirationTool`（`SimpleTool<Args>`）
- skill（agent 形态）：写作助手（systemPrompt + InspirationTool + ChatMemory/LongTermMemory）
- MCP：当前未直接接入 MCP Server，但建议后续用“Tool 作为 MCP client”的方式扩展

## 落地建议（避免踩坑）

- Tool 入参必须是 object schema（使用 `@Serializable data class Args(...)`），避免 string schema 初始化报错。
- ChatMemory 用于短期对话连续性；长期画像/偏好请写入 LongTermMemory（并控制入库策略）。
- MCP 接入建议“一个协议适配 Tool 对应一个外部资源域”，避免在单个 Tool 内塞入过多职责。

