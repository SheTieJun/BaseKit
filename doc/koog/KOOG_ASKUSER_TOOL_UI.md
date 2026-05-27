# AskUser Tool 与 UI 交互（选项 + 自由输入）

本文档说明在 Android（Compose）里如何让 Koog Tool 和 UI 进行“可挂起的交互”，以实现：

- 模型调用 `AskUser` 工具发起追问（tool call）
- UI 弹窗展示问题 + 选项
- 用户可选择选项或自由输入文本
- UI 把结果回传给工具，工具再把结果作为 tool result 返回给模型继续推理

---

## 1. 为什么需要 UI 网关（Gateway）

Koog 的 Tool 形态是 `suspend fun execute(args): String`，天然适合“等待一个结果再返回”。

但 Android UI 是事件驱动的：用户什么时候点按钮、输入什么内容，是异步回调。

因此需要一个“桥”：把 UI 的回调包装为一个可挂起的 `ask()`，让 Tool 可以 `await()` 用户答案。

---

## 2. 核心组件

**2.1 AskUserGateway（桥接接口）**

- 产出：`SharedFlow<AskUserRequest>` 给 UI 订阅（用于弹窗）
- 能力：`ask(...)` 挂起等待；`answer/cancel` 用于 UI 回填

实现文件：
- [AskUserGateway.kt](file:///Users/pc/Documents/Github/BaseKit/app/src/main/java/shetj/me/base/func/koog/askuser/AskUserGateway.kt)

**2.2 AskUserTool（Koog Tool）**

- Tool 名称：`AskUser`（模型侧看到的工具名）
- 入参：`question` + `options`
- 执行：调用 gateway 的 `ask()`，等待 UI 回填并返回结果

实现文件：
- [AskUserTool.kt](file:///Users/pc/Documents/Github/BaseKit/app/src/main/java/shetj/me/base/func/koog/tools/AskUserTool.kt)

**2.3 KoogChatViewModel（注册工具并暴露事件流）**

- 创建 agent 时统一注册：`tools = listOf(InspirationTool, AskUserTool(gateway))`
- 暴露 `askUserRequests` 给 UI 收听
- 提供 `answerAskUser/cancelAskUser` 供 UI 回填

实现文件：
- [KoogChatViewModel.kt](file:///Users/pc/Documents/Github/BaseKit/app/src/main/java/shetj/me/base/func/koog/KoogChatViewModel.kt#L1-L130)

**2.4 KoogChatScreen（Compose 弹窗）**

- 订阅 `viewModel.askUserRequests`
- 出现请求时弹出 `AlertDialog`
- 支持：点击选项会把文本写入输入框；也允许自由编辑输入框
- 确认/取消回调给 ViewModel

实现文件：
- [KoogChatScreen.kt](file:///Users/pc/Documents/Github/BaseKit/app/src/main/java/shetj/me/base/func/koog/KoogChatScreen.kt#L1-L220)

---

## 3. 数据流（从模型到 UI 再回到模型）

1) 模型触发 tool call：`AskUser(question, options)`
2) Koog 执行 `AskUserTool.execute(args)`：
   - 调用 `gateway.ask(...)`
   - `ask()` 发出 `AskUserRequest` 到 `SharedFlow`
   - `ask()` 挂起等待答案
3) UI 订阅 `SharedFlow`，弹窗展示 question/options，并允许用户输入
4) UI 点击确定：
   - 调用 `viewModel.answerAskUser(requestId, value)`
   - gateway 完成 pending 的 `Deferred`，`ask()` 返回
5) Tool 将返回值作为 tool result 回传给模型，模型继续推理并给出最终回复

---

## 4. 注意事项

- 并发：同一时间多个 AskUser 请求，UI 需要排队展示（当前实现用简单队列依次弹出）。
- 取消：用户取消时返回空字符串；建议在 systemPrompt 里要求模型遇到空答案时继续追问或提示用户重新输入。
- 生命周期：如果页面被销毁但 tool 正在等待，建议在 UI 层统一 cancel（当前实现由对话框 dismiss 触发 cancel）。

