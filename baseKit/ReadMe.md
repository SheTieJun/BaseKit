## Base

### 功能说明
| 功能 | 源码 | 说明 |
| ---- | ---- | ---- |
| 框架     | [MVP](src/main/java/me/shetj/base/mvp), [MVVM](src/main/java/me/shetj/base/mvvm)      |      |
|      |   |      |
| 扩展函数工具    | [KTX](src/main/java/me/shetj/base/ktx)     |     |
| 系统自带分享   |  [share](src/main/java/me/shetj/base/share)    |      |
| motionLayout使用   |  [motionLayout](src/main/java/me/shetj/base/anim/motion)    |      |
| 网络请求封装   |  [network_coroutine](src/main/java/me/shetj/base/network_coroutine)    | 协程， 支持重试 ， 支持缓存([多模式](src/main/java/me/shetj/base/network_coroutine/RequestOption.kt)) ， 支持timeOut   |
| 键盘/状态栏/导航栏   |  [WinCompatExt](src/main/java/me/shetj/base/ktx/WinCompatExt.kt)     | 键盘高度监听，导航栏/状态栏关闭打开高度等等     |
| 音频焦点   |   [AudioManagerKit](src/main/java/me/shetj/base/tools/app/AudioManagerKit.kt) 和 [KeyboardUtil](src/main/java/me/shetj/base/tools/app/KeyboardUtil.kt)   |  支持绑定LifecycleOwner   |
| 是否有网络监听   |  [NetWorkLiveDate](src/main/java/me/shetj/base/model/NetWorkLiveDate.kt)    |      |
| 日历   |  [CalendarReminderUtils](src/main/java/me/shetj/base/tools/time/CalendarReminderUtils.kt)   |      |
| 悬浮窗   | [floatview](src/main/java/me/shetj/base/view/floatview)     |      |
| gson封装   | [GsonKit](src/main/java/me/shetj/base/tools/json/GsonKit.kt)     |      |
| 工具   |      | [tools](src/main/java/me/shetj/base/tools)     |