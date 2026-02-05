# BaseModule 参考文档

`BaseModule` 是 BaseKit 中用于定义 Koin 依赖注入模块的核心文件，主要包含网络请求 (`HttpModule`) 和数据库 (`DBModule`) 的相关配置。

文件路径: `baseKit/src/main/java/me/shetj/base/di/BaseModule.kt`

## 1. HttpModule (网络模块)

`getHttpModule()` 函数返回一个 Koin Module，用于提供网络请求相关的单例对象。

### 核心组件

| 组件 | 说明 | 配置详情 |
| :--- | :--- | :--- |
| **OkHttpClient** | HTTP 客户端核心 | - **超时时间**: 20秒 (连接/读取/写入)<br>- **拦截器**: `HeadersInterceptor`, `ReceivedCookiesInterceptor`, `HttpLoggingInterceptor`<br>- **SSL/主机验证**: 信任所有证书和主机 (开发模式配置)<br>- **缓存**: 12MB, 路径为 `cacheDir/.unKnow`<br>- **DNS**: 使用 `OkHttpDns` |
| **Retrofit** | REST 客户端构建器 | - **Converter**: `GsonConverterFactory`<br>- **BaseUrl**: 默认为 `https://x.com/`，可通过 `BaseKit.baseUrl` 配置<br>- **Eager Validation**: Debug 模式下开启 |
| **KCApiService** | 通用 API 接口服务 | 基于配置好的 Retrofit 实例创建 |
| **HttpLoggingInterceptor** | 日志拦截器 | - **Level**: BODY (打印请求和响应体)<br>- **Enable**: true |
| **HttpHeaders** | 全局 HTTP 头 | 包含 `User-Agent` 和 `Accept-Language` |

### 缓存组件

- **LruDiskCache**: 磁盘缓存，大小 100MB，路径 `externalFilesDir/cacheFile`。
- **KCCache**: 线程安全的磁盘缓存包装类，基于 `LruDiskCache` 实现，内部使用读写锁 (`ReentrantReadWriteLock`) 保证并发安全。提供 `load`, `save`, `remove` 等操作。

### 注意事项

> ⚠️ **安全警告**: 当前 SSL 配置 (`trustManager`) 和 HostnameVerifier 配置为信任所有证书和主机。建议仅在开发或测试环境中使用此配置，生产环境应配置正确的证书验证。

## 2. DBModule (数据库模块)

`getDBModule()` 函数返回数据库相关的 Koin Module。

### 核心组件

| 组件 | 说明 | 配置详情 |
| :--- | :--- | :--- |
| **SaverDatabase** | 数据库实例 | - **Scope**: Single (单例)<br>- **Lazy**: `createdAtStart = false` (懒加载，首次使用时创建)<br>- 获取方式: `SaverDatabase.getInstance(BaseKit.app)` |
| **SaverDao** | 数据访问对象 | - **Scope**: Single (单例)<br>- **Lazy**: `createdAtStart = false`<br>- 来源: 从 `SaverDatabase` 实例中获取 |

## 3. 使用示例

在应用初始化时（通常在 `Application` 类中），加载这些模块：

```kotlin
startKoin {
    androidContext(this@MyApplication)
    modules(getHttpModule(), getDBModule())
}
```

获取实例：

```kotlin
// 在 Activity/Fragment 或其他 Koin Component 中
val apiService: KCApiService by inject()
val saverDao: SaverDao by inject()
```
