# CodeGraph 使用速查

## 这是什么

CodeGraph 是一个本地运行的代码语义索引工具。它会为你的代码仓库建立索引（存放在 `.codegraph/`），然后支持用命令快速查询符号、调用链、影响范围等信息，适合在写代码/改需求/排查问题时快速定位上下文。

## 安装（macOS/Linux）

本仓库环境使用的是 npm 全局安装方式：

```bash
npm i -g @colbymchenry/codegraph
```

可选方式（仅记录，按需使用）：

```bash
# 零安装试用（需要 Node.js）
npx @colbymchenry/codegraph --help

# 官方脚本安装（不需要 Node.js）
curl -fsSL https://raw.githubusercontent.com/colbymchenry/codegraph/main/install.sh | sh
```

## 验证是否安装成功

```bash
which codegraph
codegraph --version
codegraph --help
```

如果 `codegraph --help` 能看到 `init/status/query/explore` 等子命令，说明 CLI 正常。

## 在项目里初始化（第一次必做）

在仓库根目录执行：

```bash
cd /path/to/your-project
codegraph init -i
```

- 初始化完成后会生成 `.codegraph/` 目录
- 本仓库已经在 `.gitignore` 中忽略了 `/.codegraph/`，通常不需要提交到 Git

## 日常更新索引（建议习惯）

```bash
codegraph status
codegraph sync
```

- `status`：查看索引是否存在、统计信息等
- `sync`：对最近变更做增量更新（常用，速度通常比全量重建更快）

如果你怀疑索引状态异常，可以全量重建：

```bash
codegraph index
```

## 常用查询命令

### 1）按符号/关键词查找（轻量）

```bash
codegraph query KCHttp
codegraph query retryRequest
```

适合快速“看看这个名字在哪、有哪些相关符号”。

### 2）explore：一键把相关代码+调用路径拉出来（最常用）

```bash
codegraph explore KCHttp
codegraph explore "upload file"
```

适合让工具给你一坨“能直接读懂的上下文”，包含相关符号源代码片段和调用关系。

### 3）callers / callees：查谁调用了它、它又调用了谁

```bash
codegraph callers KCHttp.upload
codegraph callees KCHttp.upload
```

### 4）impact：改动影响范围评估（改 API 前建议跑一次）

```bash
codegraph impact KCHttp.upload
```

### 5）affected：从改动文件推导可能受影响的测试

```bash
codegraph affected baseKit/src/main/java/me/shetj/base/netcoroutine/KCHttp.kt
```

### 6）files：从索引视角查看项目结构

```bash
codegraph files
```

## 与 Agent/IDE 集成（可选）

如果你在用 Claude Code / Cursor / Codex CLI / opencode / Hermes Agent 等，可以尝试：

```bash
codegraph install
```

需要移除时：

```bash
codegraph uninstall
```

在一些支持 MCP 的环境里，集成后通常会暴露类似 `codegraph_explore` / `codegraph_node` 的工具接口，它们的输出与 CLI 的 `explore/node` 基本一致。

## 常见问题排查

### 1）提示 `command not found: codegraph`

```bash
npm prefix -g
```

确认全局 npm bin 目录是否在你的 `PATH` 中；或直接用 `npx @colbymchenry/codegraph ...` 作为临时方案。

### 2）索引卡住/提示 lock

```bash
codegraph unlock
```

### 3）想彻底清理并重建

```bash
codegraph uninit
codegraph init -i
```

## 本仓库快速示例（BaseKit）

```bash
cd /Users/pc/Documents/Github/BaseKit
codegraph sync
codegraph explore KCHttp
```

