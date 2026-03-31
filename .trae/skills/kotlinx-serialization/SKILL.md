---
name: "kotlinx-serialization"
description: "Guide for kotlinx.serialization in Kotlin/Android. Invoke when user asks about JSON parsing, serialization, deserialization, or setting up Kotlin Serialization."
---

# kotlinx.serialization

`kotlinx.serialization` 是 Kotlin 官方提供的跨平台序列化和反序列化库。它通过编译器插件生成序列化代码，支持 JSON、Protobuf、CBOR 等多种格式，完全支持 JVM、JS 和 Native。

## 依赖集成 (Gradle Kotlin DSL)

在 `gradle/libs.versions.toml` 中配置：

```toml
[versions]
kotlin = "2.1.10" # 必须与你项目使用的 Kotlin 版本一致
kotlinxSerialization = "1.7.3" # 推荐最新稳定版

[plugins]
jetbrains-kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }

[libraries]
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinxSerialization" }
# 如果不需要绑定具体格式，也可以只引入 core
# kotlinx-serialization-core = { module = "org.jetbrains.kotlinx:kotlinx-serialization-core", version.ref = "kotlinxSerialization" }
```

在模块的 `build.gradle.kts` 中应用插件和依赖：

```kotlin
plugins {
    // 应用 Kotlin 序列化编译器插件
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

dependencies {
    // 添加 JSON 格式的运行时库依赖
    implementation(libs.kotlinx.serialization.json)
}
```

## 基本使用

### 1. 标记可序列化类

使用 `@Serializable` 注解标记需要被序列化/反序列化的 `data class`。

```kotlin
import kotlinx.serialization.Serializable

@Serializable 
data class Project(
    val name: String, 
    val language: String,
    // 支持默认值，如果 JSON 中缺少该字段，将使用默认值
    val stars: Int = 0
)
```

### 2. 序列化与反序列化

```kotlin
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun main() {
    // 1. 将对象序列化为 JSON 字符串
    val data = Project("kotlinx.serialization", "Kotlin")
    val string = Json.encodeToString(data)  
    println(string) // 输出: {"name":"kotlinx.serialization","language":"Kotlin","stars":0} 
    
    // 2. 将 JSON 字符串反序列化为对象
    val obj = Json.decodeFromString<Project>(string)
    println(obj) // 输出: Project(name=kotlinx.serialization, language=Kotlin, stars=0)
}
```

## 进阶配置与技巧

### 1. 自定义 Json 实例配置

默认的 `Json` 对象是严格模式。通常在生产环境中，我们需要配置一个宽松的 `Json` 实例来忽略未知键等：

```kotlin
val format = Json { 
    ignoreUnknownKeys = true // 忽略 JSON 中存在但数据类中未定义的键
    isLenient = true         // 允许宽松的 JSON 语法 (如带引号的布尔值)
    encodeDefaults = true    // 序列化时包含带有默认值的属性
    prettyPrint = true       // 格式化输出的 JSON 字符串
}

val obj = format.decodeFromString<Project>("""{"name":"Test", "language":"Kotlin", "unknown_key": "value"}""")
```

### 2. 字段重命名 (@SerialName)

如果 JSON 中的键名与 Kotlin 属性名不一致（例如 JSON 使用下划线命名法，而 Kotlin 使用驼峰命名法）：

```kotlin
import kotlinx.serialization.SerialName

@Serializable
data class User(
    val id: Int,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String
)
```

### 3. 忽略属性 (@Transient)

如果某个属性不需要被序列化，可以使用 `@Transient` 注解（注意：被标记为 `@Transient` 的属性必须有默认值）。

```kotlin
import kotlinx.serialization.Transient

@Serializable
data class Session(
    val token: String,
    @Transient val createdAt: Long = System.currentTimeMillis() // 此字段不会被序列化和反序列化
)
```

### 4. ProGuard / R8 混淆配置

如果你在 Android 中使用了具名的伴生对象（named companion objects），请在 `proguard-rules.pro` 中添加以下规则（默认库中已包含大部分基础规则）：

```proguard
-keepattributes InnerClasses
-keepnames class <1>$$serializer {
    static <1>$$serializer INSTANCE;
}
```

## Retrofit 配合使用

如果你在 Android 项目中使用 Retrofit 进行网络请求，可以使用官方提供的转换器 `retrofit2-kotlinx-serialization-converter`：

```kotlin
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

val contentType = "application/json".toMediaType()
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.example.com/")
    .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory(contentType))
    .build()
```
