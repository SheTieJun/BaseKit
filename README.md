# BaseKit

为减少写重复代码


> 把简单的事情程序化 
> 把复杂的事情程序化 
> 把重复的事情程序化 
> 一切为了效率（偷懒）

-----------------
### 框架
- MVC/MVP/MVVM

MVVM = (ViewBinding/DataBinding)+ViewModel+LiveData

 

### Libraries

1. Retrofit- REST API Call
   - https://square.github.io/retrofit/
2. Glide - Image Loading and caching.
   - https://github.com/bumptech/glide
3. koin - Dependency Injection
   - https://insert-koin.io/
4. gson - json serialization/deserialization
   - https://github.com/google/gson
5. Android Jetpack 
   - https://developer.android.google.cn/jetpack/
   - stable-channel : https://developer.android.com/jetpack/androidx/versions/stable-channel
6. Material Design Components - Google's latest Material Components.
   - https://material.io/develop/android

---------------------

## 文章
#### [Flow 和 LiveData 之操作符](https://blog.csdn.net/StjunF/article/details/120872772)
#### [Kotlin协程+Retrofit下载文件并实现进度监听](https://blog.csdn.net/StjunF/article/details/120909119)
#### [WindowInsetsControllerCompat使用](https://blog.csdn.net/StjunF/article/details/121840122)
#### [ActivityResultLauncher使用](https://github.com/SheTieJun/BaseKit/wiki/ActivityResultLauncher%E4%BD%BF%E7%94%A8)
#### [DataStoreKit使用](https://github.com/SheTieJun/BaseKit/wiki/DataStoreKit%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)
#### [基于MD颜色系统的主题切换工具](https://github.com/SheTieJun/BaseKit/wiki/MDThemeKit-%EF%BC%9A%E4%B8%BB%E9%A2%98%E5%88%87%E6%8D%A2%E5%B7%A5%E5%85%B7%E7%B1%BB)
---------------------
## 分支
- [Base:含有RxJava的分支，很老的分支](https://github.com/SheTieJun/BaseKit/tree/base_rx)
- [Base:只有viewbinding没有Databinding的分支](https://github.com/SheTieJun/BaseKit/tree/feat_viewbinding)
---------------------
## 应用场景记录
- 使用`Navigation`代替`ARouter`
- 用`start_up`代替`Application`中初始化
  - 隐私政策，可以分阶段初始化，比如登录后才初始化
- 拦截网页的请求，或者动态添加vConsole
- 注解+ASM 
---------------------
## 模块类型
- 应用模块
- 数据模块
- 功能模块
- 通用模块
  - 界面模块
  - 网络模块
  - 辅助模块
- 通信模块（中介模块）：用于模块间的通信
---------------------
![](/doc/img/模块化-依赖项反转.webp)

---------------------

### 界面层相关记录
1. 展开界面元素（界面逻辑）和界面操作元素（业务逻辑）
2. 定义UIState，例如：`TracksUIState`,`TrackItemUIState`
   ![](/doc/img/UI_state_and_logic.webp)

### 数据层相关记录
1. DataStore 非常适合存储键值对，例如用户设置，具体示例可能包括时间格式、通知偏好设置，以及是显示还是隐藏用户已阅读的新闻报道。DataStore 还可以使用协议缓冲区来存储类型化对象。
2. 借助 WorkManager，可以轻松调度异步的可靠工作，并可以负责管理约束条件。我们建议使用该库执行持久性工作。为了执行上面定义的任务，我们创建了一个 Worker 类：RefreshLatestNewsWorker。此类以 NewsRepository 作为依赖项，以便获取最新新闻并将其缓存到磁盘中。
3. 为了保护来自不同线程的读取和写入操作，我们使用了 Mutex。
4. async 用于在外部作用域内启动协程。await 在新的协程上调用，以便在网络请求返回结果并且结果保存到缓存中之前，一直保持挂起状态。如果届时用户仍位于屏幕上，就会看到最新新闻；如果用户已离开屏幕，await 将被取消，但 async 内部的逻辑将继续执行。`luanch`适用于“发射并忘记”，而`async`适用于“异步并等待结果”
5. [数据和文件存储](https://developer.android.com/training/data-storage?hl=zh-cn)：room /DataStore/ File

### 协程最佳实践
1. 将 Dispatcher 注入到类中，易于测试，因为您可以轻松替换它们进行单元测试和仪器测试。
2. ViewModel/Presenter层应该创建自己的协程，方便取消
3. ViewModel/Presenter 层下面的层应该公开挂起函数和 Flows
4. 对于不应该取消的操作，请在 Application 类中创建您自己的作用域，并在由它启动的协程中调用不想被ViewModel/Presenter取消的代码。

### 代码检测
1. [detekt](https://github.com/detekt/detekt):Detekt 是一种静态代码分析工具，用于检测 Kotlin 代码中的潜在问题和不规范的编码实践。它是为 Kotlin 语言开发的，并且可以帮助开发者发现可能导致错误、性能问题或代码可维护性下降的代码片段。
2. [spotless](https://github.com/diffplug/spotless):Spotless 是一个开源的代码格式化和风格检查工具，可以帮助开发团队维持一致的代码风格和格式。

### Android版本行为变更:[https://developer.android.com/about/versions?hl=zh-cn](https://developer.android.com/about/versions?hl=zh-cn)