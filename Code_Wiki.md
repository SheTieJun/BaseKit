# BaseKit 项目文档 (Code Wiki)

## 项目简介
BaseKit 是一个基于 Kotlin 和 Android 现代化技术栈（Jetpack, Coroutines, Koin, Compose）构建的 Android 基础组件库及示例应用。它旨在提供一套标准化的架构模式和丰富的工具类，帮助开发者快速搭建稳定、高效的 Android 应用。

## 项目整体架构
项目采用多模块架构，主要分为两个部分：
1. **`app` 模块**：示例应用（Demo App），用于演示 `baseKit` 模块中各种基础组件、工具类、自定义 View 和新特性的使用方法。
2. **`baseKit` 模块**：核心基础组件库，封装了网络请求、数据存储、架构基类（MVP/MVVM）、依赖注入、UI 组件及大量实用工具扩展。

### 核心架构模式
- **MVVM / MVP**：提供了 `BaseBindingActivity`、`BaseViewModel` 等基类，支持 ViewBinding 和 DataBinding，同时兼容传统 MVP 架构。
- **协程 (Coroutines)**：全面拥抱 Kotlin 协程，提供 `CoroutineDispatcherProvider` 和基于 `lifecycleScope` 的封装，简化异步编程。
- **依赖注入 (DI)**：基于 Koin 实现轻量级依赖注入，统一管理网络模块（HttpModule）和数据库模块（DBModule）。

## 主要模块与职责

### `baseKit` 模块目录结构说明
*   **`anim`**：动画相关辅助类（如 `CircularRevealHelper`）。
*   **`base`**：基础核心组件基类（`AbBaseActivity`、`BaseKTAdapter` 等）。
*   **`coroutine`**：协程调度器与扩展工具。
*   **`di`**：Koin 依赖注入模块配置。
*   **`init`**：基于 App Startup 的组件初始化（`CommonInitialize`）。
*   **`ktx`**：丰富的 Kotlin 扩展函数（Activity, Context, View, String, Flow 等）。
*   **`model` / `mvp` / `mvvm`**：架构模式基类及 LiveData 封装（如 `NetWorkLiveDate`、`GrayThemeLiveData`）。
*   **`netcoroutine` / `network`**：基于 Retrofit 和 OkHttp 的网络请求封装，支持缓存、拦截器、SSE 以及协程请求（`KCHttp`）。
*   **`saver`**：基于 Room 数据库的数据保存组件。
*   **`share`**：文件分享相关工具。
*   **`tip`**：加载弹窗与提示工具（`TipKit`、`SimLoadingDialog`）。
*   **`tools`**：海量实用工具类合集：
    *   `app`：应用级别工具（AppUtils, LanguageKit, KeyboardUtil, WindowKit）。
    *   `debug`：调试相关（DebugFunc, LogManager）。
    *   `file`：文件读写与操作。
    *   `image`：图片处理。
    *   `json`：JSON 解析与加解密（GsonKit, DES）。
    *   `os` / `reflect` / `time`：系统信息、反射、时间处理。
*   **`view` / `weight`**：自定义 View 与弹窗组件。

## 关键类与函数说明

*   **[BaseKit](file:///workspace/baseKit/src/main/java/me/shetj/base/BaseKit.kt) (单例)**：核心初始化类。提供 `init()` 方法用于配置全局 Context、Koin、网络基础配置及异常捕获。
*   **[CommonInitialize](file:///workspace/baseKit/src/main/java/me/shetj/base/init/CommonInitialize.kt)**：实现了 `androidx.startup.Initializer`，用于在应用启动时自动初始化 `BaseKit` 和网络状态监听。
*   **[BaseBindingActivity](file:///workspace/baseKit/src/main/java/me/shetj/base/mvvm/viewbind/BaseBindingActivity.kt)**：MVVM 架构的 Activity 基类，封装了 ViewBinding 的初始化、ViewModel 的注入及基础生命周期管理。
*   **[KCHttp](file:///workspace/baseKit/src/main/java/me/shetj/base/netcoroutine/KCHttp.kt)**：网络请求核心封装类，结合协程提供简洁的 GET/POST 等 API 请求方式，支持统一异常处理和结果解析。
*   **[TipKit](file:///workspace/baseKit/src/main/java/me/shetj/base/tip/TipKit.kt)**：UI 提示工具，提供全局统一的 Loading 弹窗和 Toast 提示机制。
*   **[WindowKit](file:///workspace/baseKit/src/main/java/me/shetj/base/tools/app/WindowKit.kt) & [ScreenshotKit](file:///workspace/baseKit/src/main/java/me/shetj/base/tools/app/ScreenshotKit.kt)**：提供窗口Insets监听、折叠屏姿态获取及全局截屏监听功能。

## 技术栈与依赖关系

项目统一使用 `libs.versions.toml` 和 `build.gradle.kts` 进行依赖管理。
*   **语言**：Kotlin (JVM Toolchain 17, API Version 2.0)
*   **核心框架**：AndroidX, Jetpack Compose, Material Design 3
*   **架构与生命周期**：Lifecycle, ViewModel, LiveData
*   **异步与网络**：Kotlin Coroutines, Retrofit2, OkHttp (支持 SSE)
*   **依赖注入**：Koin (Android, WorkManager)
*   **数据存储**：Room, DataStore (Preferences)
*   **UI/图片**：Glide, Lottie, BaseRecyclerViewAdapterHelper (BRV), QMUI
*   **序列化**：Gson, Kotlinx Serialization
*   **其他能力**：CameraX (在 App 模块), WorkManager, Navigation3

## 快速开始与项目运行方式

### 环境要求
*   Android Studio (推荐最新版 Ladybug 或更高版本)
*   JDK 17
*   Android SDK 34+

### 运行步骤
1.  **克隆项目**：
    ```bash
    git clone https://github.com/SheTieJun/BaseKit.git
    cd BaseKit
    ```
2.  **同步依赖**：
    使用 Android Studio 打开项目，等待 Gradle Sync 完成（项目包含多模块配置，首次同步可能需要下载所需依赖）。
3.  **配置签名**：
    `app/build.gradle.kts` 中配置了 release 签名（`test.jks`）。在本地开发时，默认直接使用 Debug 构建类型即可。
4.  **运行项目**：
    在 Android Studio 顶部的运行配置中选择 `app` 模块，点击 Run (或 Shift+F10) 将应用部署到模拟器或真机。

### 使用示例 (在自身项目中引入 BaseKit)
通常在 `Application` 或通过 App Startup 自动初始化：
```kotlin
// BaseKit 会通过 App Startup 自动完成基础初始化。
// 如果需要额外的 Koin 模块或自定义配置，可以参考：
startKoin {
    androidContext(this@MyApplication)
    modules(getDBModule(), getHttpModule())
}

// 在 Activity 中使用：
class MyActivity : BaseBindingActivity<ActivityMyBinding, MyViewModel>() {
    override fun initBaseView() {
        super.initBaseView()
        // 使用 KTX 扩展
        "Hello BaseKit".showToast()
        
        // 开启 Loading
        TipKit.loading(this) {
            // 执行协程网络请求等耗时操作
        }
    }
}
```