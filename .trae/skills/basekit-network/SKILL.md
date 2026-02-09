---
name: "basekit-network"
description: "Complete guide for BaseKit Network module, including Coroutines support (KCHttp), error handling, HTTPS, SSE, and caching strategies."
---

# BaseKit Network Skills

## 简介
BaseKit 的网络层由 `me.shetj.base.network`（基础组件）和 `me.shetj.base.netcoroutine`（协程封装）两个核心模块组成。它提供了从底层配置到上层业务调用的完整解决方案。

## Part 1: 协程网络请求 (KCHttp)
`me.shetj.base.netcoroutine` 模块是基于 Kotlin 协程和 Retrofit 的高级封装，是日常开发中的主要入口。

### 1. 统一请求入口 (`KCHttp`)
`KCHttp` 单例对象提供了 GET、POST (Form/Json/Body) 等常用方法。
- **GET 请求**：
  ```kotlin
  val result = KCHttp.get<User>(
      url = "https://api.example.com/user",
      maps = mapOf("id" to "123"),
      requestOption = RequestOption(cacheMode = CacheMode.FIRST_NET)
  )
  ```
- **POST JSON**：
  ```kotlin
  val result = KCHttp.postJson<ApiResponse>(
      url = "https://api.example.com/update",
      json = "{\"name\":\"test\"}",
      requestOption = RequestOption(timeout = 5000)
  )
  ```

### 2. 请求选项 (`RequestOption`)
精细控制请求行为：
- **cacheKey**: 缓存键（必填，若使用缓存）。
- **cacheTime**: 缓存有效期（秒）。
- **cacheMode**: 缓存策略。
- **timeout**: 自定义超时时间。
- **repeatNum**: 失败重试次数。

### 3. 缓存策略 (`CacheMode`)
- `DEFAULT`: 仅使用 OkHttp 标准缓存。
- `FIRST_NET`: 优先网络，失败后读缓存。
- `FIRST_CACHE`: 优先缓存，无缓存读网络。
- `ONLY_NET`: 仅网络（更新缓存）。
- `ONLY_CACHE`: 仅缓存。
- `CACHE_NET_DISTINCT`: 先返缓存，后台请求网络，数据变化则再次返回（需配合 LiveData/Flow）。

### 4. 结果处理与下载
- **结果处理**：返回 `HttpResult<T>` (Success/Failure/Loading)。
  ```kotlin
  KCHttp.get<User>(...).fold(
      onSuccess = { user -> ... },
      onFailure = { error -> ... }
  )
  ```
- **文件下载**：
  ```kotlin
  KCHttp.download(url, outputFile, 
      onProcess = { cur, total, prog -> ... },
      onSuccess = { file -> ... }
  )
  ```

## Part 2: 基础网络组件
`me.shetj.base.network` 模块提供了底层支持，如异常处理、安全配置和拦截器。

### 1. 统一异常处理 (`ApiException`)
将所有异常统一为 `ApiException`。
```kotlin
try {
    // request
} catch (e: Throwable) {
    val error = ApiException.handleException(e)
    // error.code, error.message
}
```

### 2. HTTPS 配置 (`HttpsUtils`)
便捷配置 SSL 证书（单向/双向认证）。
```kotlin
val sslParams = HttpsUtils.getSslSocketFactory(null, null, arrayOf(certStream))
OkHttpClient.Builder()
    .sslSocketFactory(sslParams.sSLSocketFactory!!, sslParams.trustManager!!)
```

### 3. 日志拦截器 (`HttpLoggingInterceptor`)
支持 JSON 格式化输出和文件导出的日志拦截器。
```kotlin
val logger = HttpLoggingInterceptor("Tag").setLevel(Level.BODY)
OkHttpClient.Builder().addInterceptor(logger)
```

### 4. SSE 客户端 (`SseClient`)
基于 OkHttp 的 Server-Sent Events 实现。
```kotlin
SseClient.start(url)
lifecycleScope.launch {
    SseClient.shareFlow.collect { msg -> ... }
}
```

### 5. HTTPDNS (`OkHttpDns`)
防 DNS 劫持，支持自定义 IP 映射。
```kotlin
OkHttpClient.Builder().dns(OkHttpDns.getInstance())
```

## 目录结构
- **netcoroutine** (协程业务层)
  - `KCHttp.kt`: 统一入口。
  - `cache/`: LRU 磁盘缓存实现。
  - `RequestOption.kt`: 请求配置。
- **network** (基础组件层)
  - `exception/`: 异常定义。
  - `https/`: 证书配置。
  - `interceptor/`: 拦截器。
  - `sse/`: SSE 客户端。
