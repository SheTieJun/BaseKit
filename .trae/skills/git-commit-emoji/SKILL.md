---
name: git-commit-emoji
description: 生成 git commit 信息时在开头添加语义匹配的 emoji 前缀。用户要求提交代码、生成 commit message、整理提交说明或提到 emoji 提交时调用。
---

# Git 提交 Emoji 前缀

生成 git commit 信息时，根据本次变更的**主要意图**挑选匹配的 emoji 放在最前，后接中文描述。

## 流程

1. 收集变更：优先 `git diff --cached`；无暂存内容时看 `git diff`。命令前加 `GIT_EDITOR=true`，避免卡在交互式编辑器。
2. 判断意图：对照下表选出 emoji。按「为什么改」判断，而不是「改了哪些文件」。
3. 生成信息：`` `<emoji> <中文描述>` ``，描述用简洁祈使句，说清改动目的，不堆砌文件名。
4. 提交：仅在用户明确要求时才执行 `git commit`，不要主动提交。

## Emoji 对照表

| Emoji | 代码 | 适用场景 |
|---|---|---|
| 🎨 | `:art:` | 改进代码结构/代码格式 |
| ⚡ / 🐎 | `:zap:` / `:racehorse:` | 提升性能 |
| 🔥 | `:fire:` | 移除代码或文件 |
| 🐞 | `:bug:` | 修复 bug |
| 🚑 | `:ambulance:` | 重要补丁 |
| ✨ | `:sparkles:` | 引入新功能 |
| 📝 | `:memo:` | 撰写文档 |
| 🚀 | `:rocket:` | 部署功能 |
| 💄 | `:lipstick:` | 更新 UI 和样式文件 |
| 🎉 | `:tada:` | 初次提交 |
| ✅ | `:white_check_mark:` | 增加测试 |
| 🔒 | `:lock:` | 修复安全问题 |
| 🍏 | `:apple:` | 修复 macOS 下的问题 |
| 🐧 | `:penguin:` | 修复 Linux 下的问题 |
| 🏁 | `:checkered_flag:` | 修复 Windows 下的问题 |
| 📔 | `:bookmark:` | 发行/版本标签 |
| 🚨 | `:rotating_light:` | 移除 linter 警告 |
| 🚧 | `:construction:` | 工作进行中 |
| 💚 | `:green_heart:` | 修复 CI 构建问题 |
| ⬇️ | `:arrow_down:` | 降级依赖 |
| ⬆️ | `:arrow_up:` | 升级依赖 |
| 👷🏻 | `:construction_worker:` | 添加 CI 构建系统 |
| 💬 | `:chart_with_upwards_trend:` | 添加分析或跟踪代码 |
| 🔨 | `:hammer:` | 重大重构 |
| ➖ | `:heavy_minus_sign:` | 减少一个依赖 |
| 🐳 | `:whale:` | Docker 相关工作 |
| ➕ | `:heavy_plus_sign:` | 增加一个依赖 |
| 🔧 | `:wrench:` | 修改配置文件 |
| 🌎 | `:globe_with_meridians:` | 国际化与本地化 |
| ✏️ | `:pencil2:` | 修复 typo markdown |

## 选择规则

- 一次提交只用一个 emoji 作为开头，紧跟一个空格
- 混合变更取占比最大或最核心的意图；确实需要时最多并列两个，如 `✨🔒`
- 安全修复 🔒 优先于普通 bug 修复 🐞
- 结构调整或格式变动用 🎨，修 typo 用 ✏️，改配置用 🔧
- 依赖版本变化：升级 ⬆️ / 降级 ⬇️ / 新增 ➕ / 移除 ➖
- 只有问题确实属于平台专属时才用 🍏 🐧 🏁
- 没有合适 emoji 时不要硬套，直接写中文描述

## 示例

| 变更内容 | 提交信息 |
|---|---|
| 新增登录页 | `✨ 新增手机号登录页` |
| 修复列表滑动卡顿 | `⚡ 优化列表复用减少滑动卡顿` |
| 修复空数据崩溃 | `🐞 修复详情页空数据崩溃` |
| 补充构建说明 | `📝 补充构建与运行说明` |
| 升级 Retrofit 到 2.11 | `⬆️ 升级 Retrofit 至 2.11` |
| 抽取重复的网络请求逻辑 | `🔨 抽取重复的网络请求逻辑` |
