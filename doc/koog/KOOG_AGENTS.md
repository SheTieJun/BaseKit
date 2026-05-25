# Koog Agents 使用指南（BaseKit 版）

本文档用于快速理解 Koog（`ai.koog:koog-agents`）提供的能力边界、核心概念与常见用法分类，并结合 BaseKit 现有封装（`KoogAgentKit`）说明推荐落地方式。

> 参考：Koog 官方文档（Quickstart / Agent types / Core components）。本仓库当前依赖版本见 `gradle/libs.versions.toml` 的 `koog`。

另见：`KOOG_PROMPTS.md`（Prompt DSL / Builder / tool messages / LLM params）
另见：`KOOG_TOOLS.md`（Tools 类型、ToolRegistry 与使用路线）
另见：`KOOG_FEATURES.md`（Features：Tracing/OpenTelemetry/自定义 Feature）

## 1. Koog 的核心概念（先把“积木”认全）

Koog 中一个 Agent 并不是“把 LLM 包一层”，而是一个可组合的“工作流系统”。常见组成：

- Prompt Executor：负责把 prompt 发给指定的 LLM Provider，并拿回结果。
- LLM Model：模型与能力描述（不同 Provider 的不同模型）。
- Strategy：Agent 的执行策略（决定“怎么思考/怎么走流程”）。
- Tools：工具（让 Agent 调用外部能力、数据源、服务）。
- Features：可选增强（观测、记忆、拦截、扩展等，按需接入）。

在 BaseKit 里，目前你已经有：

- `KoogAgentKit.createAgent(...)`：负责 Provider/Model/BaseUrl 的选择与 executor/client 构建
- `KoogAgentKit.runAgent(...)`：同步执行 `agent.run(prompt)` 并返回文本结果
- App 层 `koog` 功能：目前用“拼接 prompt”方式实现 systemPrompt、多轮上下文与工具二段式

## 2. Agent 类型（Koog 给了哪些“策略骨架”）

Koog 官方把 Agent 的策略分成几类（按适用场景理解即可）：

### 2.1 Basic Agents（基础 Agent）

适用：你只需要“输入 -> 输出”的对话/写作/总结/问答，不需要自定义工作流。

特点：

- 默认策略覆盖大多数常规场景
- 你只需要准备好 executor + model，然后调用 `agent.run(prompt)`

BaseKit 当前的聊天就是这种范式：把（systemPrompt + history + userInput）拼成一个 prompt，交给 `AIAgent.run()`。

### 2.2 Functional Agents（函数式 Agent）

适用：你想快速写一点业务逻辑来“编排”对话，比如：

- 先做输入解析/分类
- 决定要不要调用工具
- 生成多段 prompt 并合并结果

特点：

- 用 Kotlin/Java 的函数（lambda）写逻辑，比 Graph 更轻量
- 适合做“规则 + LLM”的混合流程

BaseKit 当前实现的“灵感工具 -> 模型扩写（二段式）”也属于函数式编排思路（只是现在写在 ViewModel 里，后续可以下沉到更底层的 agent strategy）。

### 2.3 Graph-based Agents（图编排 Agent）

适用：你需要一个可视化、可维护的工作流（多节点、多分支、多状态），例如：

- 写作：题材收集 -> 人设 -> 世界观 -> 冲突升级 -> 黄金三章 -> 章节扩写
- 代码审查：收集 diff -> 规则检查 -> 风险分级 -> 建议合并

特点：

- 工作流就是一张有向图：节点做事，边控制走向
- 能把复杂逻辑拆分成“可复用节点”

建议：当你发现 ViewModel 或一个函数式 agent 逻辑越来越复杂时，就该考虑用 Graph 来做“流程工程化”。

### 2.4 Planner Agents（规划型 Agent）

适用：你给一个目标，让 Agent 自己拆步骤并迭代执行，直到满足条件，例如：

- “把这个大纲扩写成 10 章，每章都有结尾钩子”
- “把设定补齐到可写 50 万字连载”

特点：

- 内部会“规划 -> 执行 -> 校验 -> 再规划”的循环
- 适合多步任务，但需要控制成本与收敛条件

建议：先把“可控的图/函数式流程”跑稳，再上 Planner；否则容易不可控（发散、成本高、结果不稳定）。

## 3. Provider / Model / Executor 的关系（你到底在配什么）

在 Koog 里，模型选择并不是一串字符串那么简单，它跟 Provider 能力、接口差异相关。

BaseKit 的 `KoogAgentKit` 已经封装了大部分差异：

- 多 Provider：OpenAI / Anthropic / Google / DeepSeek / OpenRouter / Bedrock / Mistral / Ollama / Custom
- `baseUrl`：用于自定义网关、第三方 OpenAI 兼容接口、本地 Ollama 地址等
- `modelName`：用于在 UI 里自定义模型 id

你在 App 设置页配置的本质是：

- 选择 Provider（决定走哪个接口/SDK client）
- 填 API Key（部分 Provider 必填；Ollama 通常不需要）
- 选模型（或填写模型 id）
- 选 baseUrl（默认/自定义）

## 4. Tools（工具）怎么理解与怎么用

工具是“让 Agent 能调用外部能力”的机制，典型用途：

- 写作：灵感生成、起名、设定生成、敏感词检查、风格转换
- 工具形态：输入参数 -> 返回结构化/文本结果

本仓库现状：

- `tools/InspirationTool` 是一个示例工具
- 目前的接入方式是“App 侧规则触发”，而不是让 Koog 自动 tool calling
- 好处：可控、成本低、容易调试
- 代价：不够“智能自动”，需要你维护触发规则

推荐演进路径：

- 第一步（已实现）：规则触发 + 二段式（工具先出结果，再交给模型扩写）
- 第二步：把工具选择/路由下沉到 Agent 层（避免 ViewModel 越来越胖）
- 第三步：再考虑 Koog 的更标准 tool calling（如果你需要模型自动决定调用哪个工具）

## 5. 写作助手常见落地模式（从简单到复杂）

### 5.1 单段 Prompt 模式（最简单）

适用：临时 brainstorm、短文案、短剧情。

- `prompt = systemPrompt + history + userInput`
- `agent.run(prompt)`

### 5.2 二段式模式（工具 -> 模型扩写，已实现）

适用：先产“结构化素材”，再让模型扩写为可直接使用的内容。

- Step 1：工具生成“冲突切入点/人物小传/世界观卡片”
- Step 2：把工具结果作为“素材”，让模型按固定结构扩写（黄金三章、章节骨架、示例正文）

### 5.3 图编排写作流（建议下一阶段）

适用：你希望把写作流程工程化，并可维护。

示例节点：

- 收集需求（题材/爽点/禁忌）
- 生成人设卡
- 世界观卡
- 主线任务链
- 黄金三章
- 章节扩写
- 复盘（检查爽点密度/钩子强度/冲突升级）

### 5.4 Planner 写作流（最后上）

适用：你给“最终目标”，Agent 自己迭代直到满足验收。

建议一定要有：

- 明确的“验收条件”（例如：每章必须有冲突/钩子）
- 明确的“停止条件”（例如：最多迭代 N 轮）
- 成本限制（token/轮数/时间）

## 6. 对 BaseKit 的建议（把使用方式落在代码里）

当前仓库把“写作策略”放在 `KoogChatViewModel` 做字符串拼接与规则判断，短期高效；当功能继续增长，建议做一次边界整理：

- App/UI 层：只做输入输出与状态展示
- Agent 层：集中处理“写作策略（systemPrompt + history + tools + 扩写模板）”
- KoogAgentKit：只负责“创建/执行 agent”的基础设施

## 7. 快速上手（在本仓库里怎么用）

### 7.1 配一个本地 Ollama（推荐验证闭环）

1. 设置页新增 Agent：Provider 选 `OLLAMA`，不填 API Key
2. 选择“写作助手预设”
3. 聊天页输入：`给我一个赛博朋克+修仙的开篇灵感`

### 7.2 手动触发灵感工具

- `/inspiration 赛博朋克+修仙`
- 若已配置可用 Agent，会在“灵感工具”后追加“扩写方案”
