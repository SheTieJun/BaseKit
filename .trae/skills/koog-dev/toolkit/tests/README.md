# Koog Dev Toolkit Tests

## Smoke Test

- 文件：`toolkit/tests/KoogDevSkillSmokeTest.kt`
- 目的：在不依赖真实 LLM 网络调用的前提下，验证 Koog 核心 API（Prompt DSL、ToolRegistry、SimpleTool）可正常编译与执行（示例可复制到你的工程 test 源集后运行）。

运行：

```bash
./gradlew test
```
