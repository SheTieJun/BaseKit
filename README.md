# BaseKit

> **减少重复代码，提高开发效率**
> 把简单的事情程序化，把复杂的事情程序化，把重复的事情程序化。

BaseKit 是一个旨在简化 Android 开发的 Kotlin 基础库，提供了一系列封装良好的工具类、扩展函数和基础组件。它集成了 MVVM 架构支持、网络请求、图片加载、依赖注入等主流技术栈，帮助开发者快速搭建高质量的 Android 应用。

## 🚀 主要功能

-   **架构支持**：提供 MVVM (ViewBinding/DataBinding + ViewModel + LiveData/Flow) 基础类，简化页面开发。
-   **Kotlin 扩展 (KTX)**：涵盖 Context, View, String, Collection, File 等常用扩展，大幅减少样板代码。
-   **网络模块**：基于 Retrofit + OkHttp + Coroutines 的网络请求封装，支持 DSL 配置、异常处理、文件下载进度监听。
-   **界面组件**：
    -   沉浸式状态栏/导航栏适配 (`WindowInsetsControllerCompat`)。
    -   ActivityResultLauncher 封装，简化权限请求和 Activity 跳转结果处理。
    -   基于 Material Design 3 的主题切换工具。
-   **数据存储**：集成 DataStore, Room, SharedPreferences 工具类。
-   **工具集**：包含日志打印 (Timber), 文件操作, 异步任务 (WorkManager), 协程封装等。

## 🛠 技术栈

-   **语言**：Kotlin (100%)
-   **最低兼容**：Android 5.0 (API 21)
-   **核心库**：
    -   [Android Jetpack](https://developer.android.google.cn/jetpack/): Lifecycle, ViewModel, LiveData, Room, WorkManager, Navigation, Paging, Startup, Biometric 等。
    -   [Jetpack Compose](https://developer.android.com/jetpack/compose): 用于构建现代原生 UI。
    -   [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/): 网络请求。
    -   [Koin](https://insert-koin.io/): 依赖注入。
    -   [Glide](https://github.com/bumptech/glide): 图片加载。
    -   [Gson](https://github.com/google/gson): JSON 解析。
    -   [Coroutines](https://github.com/Kotlin/kotlinx.coroutines): 异步编程。
    -   [Material Design 3](https://m3.material.io/): UI 设计规范。

## 📦 快速开始

### 1. 添加依赖

在项目的 `build.gradle.kts` 或 `libs.versions.toml` 中添加依赖：

```kotlin
dependencies {
    implementation("com.github.SheTieJun:BaseKit:latest_version")
}
```

### 2. 初始化

推荐使用 `androidx.startup` 进行初始化（BaseKit 已内置部分自动初始化），或在 `Application` 中配置全局参数：

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 Koin, Timber 等
    }
}
```

### 3. 使用示例

**Activity 继承 BaseBindingActivity**

```kotlin
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {
    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
    /**
     * step 1
     * Init base view
     * i.e. setTitle, setToolbar, setBackButton, etc.
     */
    override fun initBaseView() {}

    /**
     * step 2
     * All initialization related work will be done in this method.
     * i.e. Handling lifecycle methods.
     */
    override fun onInitialized() {}

    /**
     * step 3
     * All observer listener code will be handled in this method inside controllers.
     */
    override fun addObservers() {}

    /**
     * step 4
     * All click action code will be handled in this method inside controllers.
     */
    override fun setUpClicks() {}
}
```

**网络请求**

```kotlin
// 使用 KCHttp (BaseKit 内置网络封装)
scope.launch {
    val result = KCHttp.get<User>("https://api.example.com/user")
    // 处理 result
}
```

## 📂 目录结构

```
BaseKit
├── app                 # 示例应用 (Demo)
├── baseKit             # 核心库代码
│   ├── src/main/java/me/shetj/base
│   │   ├── base        # BaseActivity, BaseFragment, BaseViewModel 等
│   │   ├── ktx         # Kotlin 扩展函数 (ActivityExt, ViewExt 等)
│   │   ├── network     # 网络请求封装 (Retrofit, Interceptor)
│   │   ├── tools       # 工具类 (File, Image, App 等)
│   │   ├── di          # Koin 依赖注入模块
│   │   └── weight      # 自定义 View
├── gradle              # Gradle 配置与版本管理 (Version Catalog)
└── build.gradle.kts    # 项目构建配置
```

## 📖 文档与资源

-   **Wiki**: [项目 Wiki](https://github.com/SheTieJun/BaseKit/wiki) (包含详细使用指南)
-   **相关文章**:
    -   [Flow 和 LiveData 之操作符](https://blog.csdn.net/StjunF/article/details/120872772)
    -   [Kotlin协程+Retrofit下载文件并实现进度监听](https://blog.csdn.net/StjunF/article/details/120909119)
    -   [WindowInsetsControllerCompat使用](https://blog.csdn.net/StjunF/article/details/121840122)
    -   [ActivityResultLauncher使用](https://github.com/SheTieJun/BaseKit/wiki/ActivityResultLauncher%E4%BD%BF%E7%94%A8)

## 🏗 架构设计与经验总结

### 模块化设计

-   **模块类型**
    -   应用模块
    -   数据模块
    -   功能模块
    -   通用模块
        -   界面模块
        -   网络模块
        -   辅助模块
    -   通信模块（中介模块）：用于模块间的通信

![](/doc/img/模块化-依赖项反转.webp)

### 界面层 (UI Layer)

1.  **逻辑分离**：明确区分界面元素（界面逻辑）和界面操作元素（业务逻辑）。
2.  **UIState 定义**：推荐使用 `Sealed Class` 或 `Data Class` 定义 UI 状态，例如 `TracksUIState`, `TrackItemUIState`。

![](/doc/img/MVVM.webp)

### 数据层 (Data Layer)

1.  **DataStore**：适合存储键值对（如用户设置、时间格式、通知偏好）。支持使用协议缓冲区存储类型化对象。
2.  **WorkManager**：用于调度可靠的异步任务（持久性工作），如 `RefreshLatestNewsWorker`。
3.  **Mutex**：用于保护来自不同线程的读写操作，确保线程安全。
4.  **async/await**：
    -   `launch`：适用于“发射并忘记”。
    -   `async`：适用于“异步并等待结果”。
    -   **场景示例**：使用 `async` 启动协程并调用 `await`，如果在网络请求返回前用户离开屏幕，`await` 会被取消，但 `async` 内部逻辑继续执行（如写入缓存）。
5.  **存储选型**：Room / DataStore / File。

### 关键技术选型与场景

-   **导航**：使用 `Navigation` 组件代替 `ARouter`。
-   **初始化**：使用 `App Startup` 代替在 `Application` 中直接初始化。
    -   支持分阶段初始化（如隐私政策同意后、登录后）。
-   **调试工具**：支持拦截网页请求，或动态添加 vConsole 进行 H5 调试。
-   **AOP**：使用注解 + ASM 进行字节码插桩。

## ✅ 最佳实践

-   **架构**：遵循 MVVM 模式，将 UI 逻辑与业务逻辑分离。
-   **协程最佳实践**：
    1.  **依赖注入**：将 `Dispatcher` 注入到类中，便于单元测试（替换为 TestDispatcher）。
    2.  **作用域管理**：ViewModel/Presenter 层应创建自己的协程 Scope，以便在生命周期结束时自动取消。
    3.  **API 设计**：ViewModel/Presenter 下层（Repository/DataSource）应公开挂起函数 (`suspend functions`) 和 `Flow`。
    4.  **全局操作**：对于不应被 UI 生命周期取消的操作，请在 `Application` 类中创建独立作用域（`GlobalScope` 或自定义 Application Scope）来启动协程。
-   **代码质量**：
    -   集成 [Detekt](https://github.com/detekt/detekt) 进行静态代码分析。
    -   集成 [Spotless](https://github.com/diffplug/spotless) 统一代码格式。

## 🔄 分支说明

-   `master`: 主分支，包含最新特性。
-   `base_rx`: 包含 RxJava 的旧分支（已不再主要维护）。
-   `feat_viewbinding`: 仅 ViewBinding (无 DataBinding) 的分支。


## 📄 许可证

[Apache License 2.0](LICENSE)
