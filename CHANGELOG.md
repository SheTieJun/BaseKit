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
