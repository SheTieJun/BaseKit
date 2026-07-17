---
title: "Koog Reference (Skill-local)"
version: "v1"
source: "Derived from Koog-related docs in this repository; copied/condensed to keep koog-dev skill self-contained."
---

# Koog Reference（SKILL 内置版）

本文件用于让 `.trae/skills/koog-dev` 目录完全自洽：SKILL 不再依赖仓库其它路径的文档链接。

## 1) 基础概念与最小闭环

### Agent

- Agent = LLM + Tools + Strategy + Features
- 必设 `maxIterations`（或 `maxAgentIterations`）防止循环成本失控

### Prompt

- Prompt 是“消息集合 + 可选参数”，而不是一段字符串
- 消息类型：`system / user / assistant / toolCall / toolResult`

## 2) Tools（Function Call）

### 工具类型

- `SimpleTool<Args>`：最常用，工具返回 `String`，便于模型继续推理
- `Tool<Args, Result>`：需要非字符串结果或更强控制时使用

### 入参约束（强烈建议当成硬规范）

- Args 必须是 **object schema**：`@Serializable data class Args(...)`
- 给每个字段加 `@LLMDescription`，让模型更稳定地产生参数 JSON

### 注册

- 工具必须注册进 `ToolRegistry` 才能被 Agent 调用
- 典型写法：
  - `ToolRegistry { tools(tool1, tool2) }`

### 运行时闭环

- 模型触发 tool call
- 执行工具
- 将 tool result 写回上下文
- 继续迭代，直到没有 tool call 或达到 `maxIterations`

## 3) Prompt 中显式记录工具轨迹（toolCall/toolResult）

用途：把“调用了什么工具/入参/输出”写进 Prompt，让模型基于工具输出继续推理（尤其适合工程强控工具调用后的二段式扩写）。

- `toolCall(id, tool, argsJson)`
- `toolResult(id, tool, outputText)`
- `id` 必须一一对应（同一次调用用同一个 id 串起来）

## 4) LLM 参数（LLMParams）

### 常用参数

- `temperature`：越低越稳定，越高越发散
- `maxTokens`：控制输出长度；输出截断时优先提高它并减少无关上下文
- `toolChoice`：控制工具调用策略（Auto/None/Force… 以 SDK 版本为准）
- `numberOfChoices`：一次返回多个候选（成本显著增加）
- `schema`：约束结构化输出（JSON）
- `speculation`：轻量格式提示（仅部分模型支持）

### 推荐实践

- 写作/多任务场景：优先在 Prompt 级配置参数（同一 Agent 内不同请求更灵活）
- 输出不稳定：先降温度，再强化格式，最后才考虑上 schema

## 5) Model Capabilities（能力检测与降级）

典型能力分类（以 Koog 体系为参考）：

- Core：`Temperature`、`Tools`、`ToolChoice`、`MultipleChoices`、`Speculation`
- Schema：`Schema.JSON.Basic`、`Schema.JSON.Standard`
- Media/Text：`Vision.Image`、`Document`、`Embed`、`Completion`、`PromptCaching`、`Moderation`

用法：运行前检测 `LLModel.supports(capability)`，不支持则降级（例如不走 Tools、降为纯文本输出、关 schema 等）。

## 6) MCP（Model Context Protocol）

Koog 通过 MCP 把“外部工具集合”桥接进 `ToolRegistry`。

### 连接方式

- stdio：MCP server 作为独立进程，通过 stdin/stdout 通信
- SSE：MCP server 作为 HTTP 服务，通过 Server-Sent Events 通信

### 关键点

- 明确 transport（stdio/SSE）
- 明确 serverInfo（url、command 等）
- 将 MCP tool 转换并注册到 ToolRegistry 供 Agent 使用

## 7) ChatMemory / LongTermMemory / HistoryCompression

### ChatMemory（短期对话历史）

- 以 `sessionId` 为主键加载/保存消息
- 常见持久化：Room 实现 `ChatHistoryProvider`
- 注意：如果你的实现只保存 `user/assistant`，那么 toolCall/toolResult 不会被持久化（要额外设计）

### LongTermMemory（长期可检索记忆）

- 存“可检索文档记忆”，适合偏好/长期信息
- 建议 namespace 以 `userId_agentId` 隔离
- 建议默认只允许业务显式写入（避免把噪声对话灌进长期记忆）

### HistoryCompression（历史压缩）

- 用于控制 token、防止长对话越聊越慢/越聊越不准
- 常见策略：WholeHistory / FromLastNMessages / Chunked / FactRetrieval...
- 如果使用了 memory 相关 feature，压缩要注意 `preserveMemory`（避免删掉关键 memory 消息）

## 8) AskUser（多轮追问）

规则化多轮收集信息的推荐方式：

- 信息不足时必须调用 AskUser（不要猜）
- AskUser 必须作为 Tool 注册，否则模型“想问也问不了”
- Args 结构建议：
  - `question: String`
  - `options: List<String> = emptyList()`（为空表示自由输入）

## 9) 内容审核（Moderation）

推荐默认开启“三段式”：

- 输入前审：user input
- 输出前审：assistant output
- 工具边界审：tool args / tool result
