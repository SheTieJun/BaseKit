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
