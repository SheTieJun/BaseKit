---
name: "rework-commits"
description: "将当前分支重组为一组小而清晰、适合代码评审的语义化提交。用户要求拆分提交、整理 commit 历史、重做提交顺序时调用。"
---

# 重组提交历史（rework-commits）

把当前分支整理为一组更小、更清晰、具备语义的提交，方便代码评审。

## 何时使用

- 用户希望把一大坨修改拆成多个可审查的小提交
- 用户希望整理当前分支的 commit 历史
- 用户希望在提交前重构 commit 粒度、顺序或说明
- 用户明确提到“拆分提交”“整理 commit”“rework commits”

## 重要说明

- 你执行的所有 git 命令前都要加上 `GIT_EDITOR=true`，尤其是查看 diff 一类命令，避免命令进入交互式编辑器后卡住

## 操作步骤

1. **检查是否有未提交变更**：如果存在未提交内容，立即中止。
2. **检查 rebase 状态**：确认当前分支已经 rebase 到 `main` 之上；如果没有，立即中止。
3. **保存恢复点**：把当前提交哈希告诉用户，便于后续需要时可以通过 `git reset --hard` 回退到这里。
4. **保存原始 diff**：在开始修改前，把完整的 git diff 保存到 `/tmp/original-diff.patch`。
5. **重置到 main**：执行 `git reset main`，把所有变更变成未暂存状态。
6. **规划提交拆分**：仔细阅读**全部**变更内容，先识别当前项目类型与模块边界（不确定就问用户），再规划出一组小而清晰、按顺序推进的语义化提交。把每个提交的 TODO 写到 `/tmp/split-todos.md`。
   - 项目类型判断参考：
     - Android：存在 `AndroidManifest.xml`、`app/` 模块、`com.android.application`/`com.android.library`
     - KMP：存在 `kotlin-multiplatform`、`commonMain/commonTest`、`androidMain/iosMain` 等 sourceSet
     - Backend：存在 `build.gradle(.kts)` 中的服务端框架（如 Spring/Ktor）与服务端目录结构
   - 拆分顺序建议（按项目类型调整）：
     - 通用：底层依赖/构建逻辑 → 公共 API/基础模块 → 业务模块 → UI → 测试与文档
     - Android：Gradle/版本管理/Manifest → 基础库/网络/数据层 → 业务模块 → UI/资源 → 测试
     - KMP：Gradle/KMP 配置 → `commonMain` 公共逻辑（优先）→ 各平台 `androidMain/iosMain/...` 适配 → 示例/测试
     - Backend + Web：数据库/Schema 相关优先，其次后端接口与实现，最后前端与联调调整
7. **逐个创建提交**：按照 TODO 顺序依次处理，并为人工评审者撰写高质量的提交说明。
8. **校验结果**：将当前 diff 与 `/tmp/original-diff.patch` 对比，确认没有任何变更被丢失或意外改动。
9. **清理临时文件**：校验通过后，删除临时文件。

## 补充说明

- 如果校验失败，要明确告知用户，并提供最初记录的提交哈希作为恢复点
- 每个提交都应当是自包含的，代表一个独立且合理的工作单元
- 提交信息应说明“为什么这样改”，而不仅仅是“改了什么”
