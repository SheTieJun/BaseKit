# Android 17 发布说明

> 来源：[Android Developers Blog - Android 17 is here](https://android-developers.googleblog.com/2026/06/Android-17.html)
> 发布时间：2026年6月16日
> 作者：Matthew McCullough, VP of Product Management, Android Developer

---

## 概述

Android 17 正式发布，即日起支持大多数 Pixel 设备。Android 17 标志着从操作系统向**智能系统**的转型，以自适应优先（adaptive-first）为开发标准，同时带来下一代隐私、安全、媒体、相机和性能改进。

---

## 一、智能系统（Intelligence System）

Android 正从操作系统转变为深度融合硬件、软件和 AI 的智能系统。

### AppFunctions

- 一个平台 API + Jetpack 库，允许将应用的独特能力作为可编排的「工具」贡献给 Android MCP（Model Context Protocol 的设备端实现）。
- AI 代理和助手（如 Google Gemini）可以发现并执行 AppFunctions，代表用户完成工作流，并直接访问应用的本地状态。
- Jetpack 库目前处于 alpha 阶段，添加 AppFunction 只需注解类并添加 KDoc 注释。

```kotlin
class NoteFunctions(
    private val noteRepository: NoteRepository
) {
    @AppFunction(isDescribedByKDoc = true)
    suspend fun createNote(
        appFunctionContext: AppFunctionContext,
        title: String,
        content: String
    ): Note {
        return noteRepository.createNote(title, content)
    }
}
```

### AppFunctions Agent Skill

- 自动分析应用的关键工作流，生成所需 Kotlin 代码，优化 KDoc 供 LLM 调用，并提供 ADB 调试命令。
- GitHub: [android/skills - appfunctions](http://github.com/android/skills/tree/main/on-device/appfunctions)

### Gemini 集成

- Gemini 集成目前处于 trusted tester 私有预览阶段。
- 提供了 [测试代理应用](http://github.com/android/appfunctions/releases/initial) 来发现和执行 AppFunctions、模拟 AI 代理集成。
- 可申请早期访问计划：[goo.gle/eap-af](http://goo.gle/eap-af)

---

## 二、自适应优先（Adaptive-First）

用户不再依赖单一设备形态，而是在手机、折叠屏、平板、笔记本、车载显示和 XR 环境之间切换。目前已有超过 **5.8 亿大屏设备**，加上即将推出的基于 Android 内核的 ChromeOS（Googlebooks），自适应开发成为巨大机遇。

### 大屏设备强制自适应

- 针对 targetSdk 37 的应用，**移除大屏设备（sw > 600dp）上对方向和大小调整的开发者退出选项**。
- 系统将忽略以下传统清单属性和运行时 API：
  - `screenOrientation` / `setRequestedOrientation()`
  - `resizeableActivity=false`
  - 宽高比限制（`minAspectRatio` / `maxAspectRatio`）
- **游戏**（基于 Google Play 中的应用类别）**仍可豁免**。

### 新一代多任务：App Bubbles、Bubble Bar 和桌面交互式 PiP

- **App Bubbles**：用户可以长按启动器中的应用图标将任意应用变为悬浮气泡，在手机、折叠屏和平板上均可使用。
- **Bubble Bar**：在大屏设备上，系统任务栏新增专用 Bubble Bar，用于组织、切换和停靠悬浮气泡。
- **桌面交互式 PiP**：在桌面环境中引入交互式画中画，这些固定窗口保持完全交互且始终置顶。

### Activity 重建行为更新

为避免状态丢失和卡顿，Activity 默认不再因以下配置变更重启（改为通过 `onConfigurationChanged()` 通知）：
- `CONFIG_KEYBOARD`
- `CONFIG_KEYBOARD_HIDDEN`
- `CONFIG_NAVIGATION`
- `CONFIG_TOUCHSCREEN`
- `CONFIG_COLOR_MODE`

如需完整重启以重新加载资源，需通过新增的 `android:recreateOnConfigChanges` 清单属性显式选择。

### Continue On（跨设备接续）

帮助用户在不同 Android 设备间无缝切换任务：
- 用户在平板的任务栏中可以看到手机上最近打开的应用建议，一键接续。
- 支持应用到 Web 的切换，包括在未安装应用时回退到 Web。

```kotlin
class MyHandoffActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHandoffEnabled(true, null)
    }

    override fun onHandoffActivityDataRequested(
        handoffRequestInfo: HandoffActivityDataRequestInfo
    ): HandoffActivityData {
        // 创建并返回 handoff 数据
    }
}
```

### Jetpack Compose 自适应开发技能

Google 推出了 [Jetpack Compose adaptive skill](https://github.com/android/skills/tree/main/jetpack-compose/adaptive)，帮助实现最佳自适应实践：

- **自适应导航**：使用 Material 3 Adaptive 库的 `NavigationSuiteScaffold`，在移动端底部导航栏和大屏边缘导航栏之间自动切换。
- **多窗格布局**：使用 Navigation 3 Scenes 原生实现列表-详情和支持窗格布局。
- **FlexBox & Grid API**：Compose 1.11 的动态布局组件，轻松调整行列跨度。
- **高级非触摸输入**：Compose 1.11 增强的触控板和鼠标支持，包括原生焦点环和新 API（`TrackpadInjectionScope`、`performTrackpadInput`）。
- **动态窗口状态**：在应用从全屏切换到浮动气泡或交互式桌面 PiP 窗口时无缝适配 UI。

---

## 三、Compose 优先（Compose-First）

**Android 开发现已转为 Compose 优先：**

- 所有新的 Android API、库、工具和开发者指南将**专为 Jetpack Compose 构建**。
- 传统 View 组件（`android.widget` 包）和基于 View 的 Jetpack 库（Fragment、RecyclerView、ViewPager 等）进入**维护模式**，仅接收关键 bug 修复，不再添加新功能。

**迁移支持**：使用 AI 驱动的 [XML to Compose Migration Skill](https://developer.android.com/develop/ui/compose/migrate/migrate-xml-views-to-jetpack-compose) 自动将传统 View 布局转换为 Compose 代码。

---

## 四、性能与效率

### 应用内存限制

Android 17 将基于设备总 RAM 强制执行严格的应用内存限制：

- **R8 优化器**：使用 full mode + [R8 Configuration Analyzer](https://developer.android.com/topic/performance/app-optimization/r8-configuration-analyzer) 缩减字节码内存占用。
- **LeakCanary 集成**：Android Studio Panda 中的 Profiler 原生集成了 LeakCanary。
- **ApplicationExitInfo**：被内存限制终止时，`getDescription()` 返回 `"MemoryLimiter:AnonSwap"`。
- **设备端异常检测**：通过 `ProfilingManager` 使用 `TRIGGER_TYPE_ANOMALY` 在达到内存限制时自动捕获堆转储。

```kotlin
val profilingManager = applicationContext
    .getSystemService(ProfilingManager::class.java)

val triggers = ArrayList<ProfilingTrigger>().apply {
    add(ProfilingTrigger.Builder(
        ProfilingTrigger.TRIGGER_TYPE_ANOMALY).build())
}
profilingManager.addProfilingTriggers(triggers)
```

### 分代垃圾回收

ART 的 Concurrent Mark-Compact GC 引入更频繁、轻量级的年轻代回收，将短期对象与长期对象分离，大幅降低 CPU 使用、功耗和 UI 卡顿。ART 改进通过 Google Play System 更新覆盖 Android 12+ 的超过 10 亿台设备。

### 无锁 MessageQueue

`android.os.MessageQueue` 实现无锁架构：
- 显著减少丢帧，改善应用启动时间，提升多线程场景下的队列性能。
- **注意**：可能破坏使用反射访问 MessageQueue 私有字段/方法的应用。
- 新增 `TestLooperManager.peekWhen()` 和 `TestLooperManager.poll()` API 替代反射。

### static final 字段真正不可变

针对 targetSdk 37 的应用，无法再修改 `static final` 字段：
- 通过反射修改会抛出 `IllegalAccessException`。
- 通过 JNI `SetStatic<Type>Field` 修改会**立即崩溃**。

### 自定义通知视图限制

针对 targetSdk 37 的应用，进一步收紧自定义通知视图大小限制，修复通过 URI 绕过现有限制的漏洞。

---

## 五、隐私与安全

### 隐私保护选择

Android 17 继续转向隐私保护模式，提供临时、基于会话的访问：

- **系统级联系人选择器**：使用 `ACTION_PICK_CONTACTS`，应用可请求仅访问用户选择的特定字段（如邮箱或电话），无需 `READ_CONTACTS` 权限，支持个人/工作资料分离。
- **可自定义的 Photo Picker 宽高比**：使用 `PhotoPickerUiCustomizationParams` 自定义系统照片选择器，以人像模式显示缩略图。
- **系统渲染的位置按钮**：嵌入式系统渲染位置按钮，仅为当前会话授予精确位置访问权限。

---

## 关键要点总结

| 领域 | 关键变化 | 影响 |
|------|----------|------|
| **智能系统** | AppFunctions + MCP | 应用功能可被 AI 代理发现和调用 |
| **自适应** | 大屏强制自由缩放 | 移除方向/大小调整限制，必须适配任意窗口 |
| **Compose** | Compose 优先 | View 体系进入维护模式 |
| **性能** | 内存限制 + 无锁队列 + 分代 GC | 更严格的内存管理，更好的性能 |
| **隐私** | 临时/会话级权限 | 更少请求永久权限 |

---

*本文档整理自 Google Android Developers 官方博客，完整原文请参阅 [Android 17 is here](https://android-developers.googleblog.com/2026/06/Android-17.html)。*
