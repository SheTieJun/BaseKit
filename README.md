# BaseKit

为减少写重复代码

-----------------
### 框架
- MVC/MVP/MVVM
 
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
```
```
## 文章
#### [Flow 和 LiveData 之操作符](https://blog.csdn.net/StjunF/article/details/120872772)
#### [Kotlin协程+Retrofit下载文件并实现进度监听](https://blog.csdn.net/StjunF/article/details/120909119)
#### [WindowInsetsControllerCompat使用](https://blog.csdn.net/StjunF/article/details/121840122)
#### [ActivityResultLauncher使用](https://github.com/SheTieJun/BaseKit/wiki/ActivityResultLauncher%E4%BD%BF%E7%94%A8)
#### [DataStoreKit使用](https://github.com/SheTieJun/BaseKit/wiki/DataStoreKit%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)

## 分支
- [Base:含有RxJava的分支，很老的分支](https://github.com/SheTieJun/BaseKit/tree/base_rx)
- [Base:只有viewbinding没有Databinding的分支](https://github.com/SheTieJun/BaseKit/tree/feat_viewbinding)

## 应用场景记录
- 使用`Navigation`代替`ARouter`
- 用`start_up`代替`Application`中初始化
  - 隐私政策，可以分阶段初始化，比如登录后才初始化
- 可视化日志，用自己写的[LogKit](https://github.com/SheTieJun/LogKit)
  - 拦截网页的请求，或者动态添加vConsole

```
```
## 模块类型
- 应用模块
- 数据模块
- 功能模块
- 通用模块
  - 界面模块
  - 网络模块
  - 辅助模块
- 通信模块：用于模块间的通信