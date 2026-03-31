---
name: "compose-navigation3"
description: "Guide for Jetpack Navigation 3 in Compose. Invoke when user asks to set up Navigation 3, manage back stack, or use NavDisplay."
---

# Jetpack Navigation 3 (Compose)

Navigation 3 是一款专门为 Compose 设计的全新导航库。它放弃了传统 Navigation 复杂的 NavController，提供了对返回栈（Back Stack）的完全控制，并将界面直接与返回栈状态绑定。

## 核心概念

1. **完全控制返回栈**：返回栈（Back Stack）只是一个普通的列表/集合，你可以像操作普通列表一样（添加、移除元素）来控制导航。
2. **NavDisplay**：用于根据当前的返回栈显示对应的内容界面。每当返回堆栈发生变化时，它都会自动更新界面并包含动画过渡。
3. **内容键（Keys）**：返回栈中的每个条目都通过唯一的键（Key）表示用户要前往的目的地内容。
4. **范围与状态保留**：提供返回栈中项的范围，允许在项位于返回栈中时保留状态。
5. **自适应布局**：支持自适应布局系统，可同时显示多个目的地，并允许在这些布局之间无缝切换。

## 依赖集成

在 `gradle/libs.versions.toml` 中添加依赖：
```toml
[versions]
navigation3 = "1.1.0-beta01"
lifecycleViewmodelNav3 = "2.11.0-alpha02"
kotlinSerialization = "2.2.21"
kotlinxSerializationCore = "1.9.0"
material3AdaptiveNav3 = "1.3.0-alpha09"

[plugins]
jetbrains-kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlinSerialization"}

[libraries]
# Core Navigation 3 libraries
androidx-navigation3-runtime = { module = "androidx.navigation3:navigation3-runtime", version.ref = "navigation3" }
androidx-navigation3-ui = { module = "androidx.navigation3:navigation3-ui", version.ref = "navigation3" }

# Optional add-on libraries
androidx-lifecycle-viewmodel-navigation3 = { module = "androidx.lifecycle:lifecycle-viewmodel-navigation3", version.ref = "lifecycleViewmodelNav3" }
kotlinx-serialization-core = { module = "org.jetbrains.kotlinx:kotlinx-serialization-core", version.ref = "kotlinxSerializationCore" }
androidx-material3-adaptive-navigation3 = { group = "androidx.compose.material3.adaptive", name = "adaptive-navigation3", version.ref = "material3AdaptiveNav3" }
```

在模块的 `build.gradle.kts` 中引用：
```kotlin
plugins {
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.material3.adaptive.navigation3)
}
```

## 基本使用示例

在 Navigation 3 中，你不再需要 `NavHost` 和 `NavController`，而是使用 `NavDisplay` 配合自定义的返回栈状态，并通过 `entryProvider` 来解析内容。

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import kotlinx.serialization.Serializable

// 1. 定义目的地的键（Keys），实现 NavKey 接口以便状态保存
sealed class Screen : NavKey {
    @Serializable data object Home : Screen()
    @Serializable data class Profile(val userId: String) : Screen()
    @Serializable data object Settings : Screen()
}

@Composable
fun MyApp() {
    // 2. 创建并管理返回栈 (直接使用 MutableList)，使用 rememberNavBackStack 来保存和恢复状态
    // 注意：实际迁移中建议将状态提升到 ViewModel 或专用的 State 类中
    val backStack = rememberNavBackStack(Screen.Home) // 需要依赖 kotlinx-serialization 且路由实现 NavKey

    // 3. 使用 NavDisplay 显示内容
    NavDisplay(
        backstack = backStack,
        onBack = { 
            if (backStack.size > 1) {
                backStack.removeLast() 
            }
        },
        // 4. 将键解析为具体的界面内容 (之前由 NavGraph 负责)
        // 推荐使用 entryProvider DSL 来替代原有的尾随 lambda when (key) 写法
        entryProvider = entryProvider {
            entry<Screen.Home> {
                HomeScreen(
                    onNavigateToProfile = { userId ->
                        backStack.add(Screen.Profile(userId)) // 像操作列表一样导航
                    },
                    onNavigateToSettings = {
                        backStack.add(Screen.Settings)
                    }
                )
            }
            // 通过 entry<T>(metadata = mapOf(...)) 还可以配置特定页面的额外元数据（例如局部动画）
            entry<Screen.Profile> { key ->
                ProfileScreen(
                    userId = key.userId,
                    onBack = { backStack.removeLast() } // 像操作列表一样返回
                )
            }
            entry<Screen.Settings> {
                SettingsScreen(
                    onBack = { backStack.removeLast() }
                )
            }
        }
    )
}
```

## 从 Navigation 2 迁移指南

如果从 Navigation 2 迁移，核心的转变是从“依赖框架”转变为“状态驱动”。具体步骤如下：

1. **强类型路由转 NavKey**：将所有的类型安全路由（使用 `@Serializable` 注解的 data class/object）修改为实现 `NavKey` 接口。
2. **状态容器**：创建一个自定义的类（如 `NavigationState` 或 ViewModel）来保存 `mutableStateListOf` 返回栈，取代原来的 `NavController`。
3. **转移提供者**：将 `NavHost` 中庞大的 `NavGraph` 解析逻辑提取出来（如提取到 `entryProvider`），并将其作为 `NavDisplay` 的尾随 lambda 传递。
4. **替换组件**：使用 `NavDisplay` 完全替换原有的 `NavHost`。
5. **不支持特性提醒**：目前 Navigation 3 的迁移指南尚未涵盖：多级嵌套导航、跨栈共享目的地和自定义目的地类型。深层链接（Deep Links）的处理需要通过状态驱动方式手动实现。

## 高级特性与最佳实践

### 1. 动画过渡 (Animation)
NavDisplay 提供内置动画功能，可通过 `transitionSpec` (前进)、`popTransitionSpec` (后退)、`predictivePopTransitionSpec` (预测性返回) 覆盖默认动画。
另外，还可以在具体路由条目 (entry) 的 `metadata` 中通过 `NavDisplay.TransitionKey` 等键配置该场景特有的动画。若要在多场景间保持连贯过渡，可以利用 `SharedTransitionScope`。

### 2. 状态保存 (State Preservation)
- **返回栈保存**：使用 `rememberNavBackStack(Screen.Home)` 创建返回栈，它可以自动在配置更改或进程终止后恢复。要求路由键必须实现 `NavKey` 并且添加 `@Serializable` 注解。
- **ViewModel 作用域**：默认 ViewModel 的作用域是 Activity/Fragment。通过引入 `androidx.lifecycle:lifecycle-viewmodel-navigation3` 并配置 `entryDecorators`，可以将 ViewModel 的生命周期绑定到特定的 NavEntry。当该条目从返回栈移除时，ViewModel 会被自动清理。

```kotlin
NavDisplay(
    backStack = backStack,
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator() // 绑定 ViewModel 生命周期
    ),
    // ...
)
```

### 3. 模块化 (Modularization)
大型应用建议将导航代码模块化：
- **API 模块**：存放所有的路由键 (NavKey)。
- **Impl 模块**：存放具体的 UI 内容和路由解析逻辑。通过在 `EntryProviderScope` 上创建扩展函数（如 `fun EntryProviderScope<NavKey>.featureAEntryBuilder()`）将界面的解析逻辑拆分。
- 主应用模块中通过 Dagger/Hilt 等依赖注入框架收集所有的 builder，统一注册到 `NavDisplay` 的 `entryProvider` 中，实现完全解耦。

### 4. 深层链接 (Deep Links)
在 Navigation 3 中，由于你完全控制了表示导航状态的返回栈集合，因此**深层链接可以直接通过解析传入的 Intent/URI，并手动构造对应的路由键列表来覆盖或推入到返回栈中实现**。这完全契合其“状态驱动”的理念，不需要额外的复杂深层链接声明配置。

**深层链接实现示例：**
```kotlin
@Composable
fun AppNavigation(intent: Intent?) {
    // 1. 初始化默认的返回栈
    val backStack = rememberNavBackStack(Screen.Home)

    // 2. 监听 Intent 的变化来处理 Deep Link
    LaunchedEffect(intent) {
        intent?.data?.let { uri ->
            // 解析 URI, 例如: myapp://profile/123
            if (uri.scheme == "myapp" && uri.host == "profile") {
                val userId = uri.lastPathSegment
                if (userId != null) {
                    // 重置或修改状态以响应深层链接：先确保在 Home 栈底，再推入 Profile
                    backStack.clear()
                    backStack.add(Screen.Home)
                    backStack.add(Screen.Profile(userId))
                }
            }
        }
    }

    NavDisplay(
        backstack = backStack,
        // ...
    )
}
```

## 主要优势

- **更简单的 Compose 集成**：API 完全契合 Compose 声明式、状态驱动的思想。
- **状态驱动**：由于返回栈只是一个普通的状态列表（`mutableStateListOf`），这使得深层链接（Deep Links）、多返回栈和状态恢复变得极为简单，只需直接修改或序列化该列表即可。
- **灵活的场景管理**：`NavDisplay` 允许灵活定义不同平台下的布局策略，使布局能够同时从返回堆栈中读取多个目的地，从而适应窗口大小的变化。
