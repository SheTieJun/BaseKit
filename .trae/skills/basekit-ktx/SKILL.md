---
name: "basekit-ktx"
description: "Collection of Kotlin extensions for Android development. Invoke when needing common Android/Kotlin shortcuts."
---

# BaseKit KTX Skills

## 简介
`me.shetj.base.ktx` 模块提供了一系列 Kotlin 扩展函数，涵盖了 Android 开发的各个方面，从 UI 操作到系统组件交互，旨在提高开发效率。此外，`me.shetj.base.tools.file` 包下提供了一系列基础工具类。

## 核心功能分类

### 1. Context, Activity, Fragment & Window
提供界面跳转、权限请求、结果回调、沉浸式状态栏及 Context 增强功能。

- **Context 增强 (`ContextKt`)**:
  ```kotlin
  // 获取生命周期协程作用域 (支持 Activity, Service, App)
  context.lifeScope.launch { ... }
  
  // 安全转换为 Activity
  val activity = context.asActivity
  val componentActivity = context.asComponentActivity
  
  // 应用前台切换
  context.moveToFrontApp() // 将 APP 移至前台
  context.moveToFront(activity) // 将指定 Activity 移至前台
  
  // 资源获取与启动
  context.getIdByName("layout", "activity_main")
  context.openActivity("scheme://...")
  context.openActivityByPackageName("com.example.app")
  ```

- **ActivityResultLauncher (`ActivityResultExt`)**:
  自动管理生命周期的 Launcher 注册，简化 `startActivityForResult` 和权限请求。
  ```kotlin
  // 请求单个权限
  startRequestPermission(Manifest.permission.CAMERA) { granted -> }
  
  // 请求多个权限
  startRequestPermissions(arrayOf(Manifest.permission.CAMERA)) { result -> }
  
  // 启动 Activity 并获取结果
  startActivityResult(intent) { result -> }
  
  // 拍照/录像/选择文件/创建文件
  takePicture { uri -> }
  selectFile { uri -> }
  createFile("test.txt") { uri -> }
  
  // 图片裁剪
  cropImage(CropImage(uri, 1, 1)) { uri -> }
  
  // Android 13+ 照片选择器
  pickVisualMedia(PickVisualMediaRequest.Builder().build()) { uri -> }
  ```

- **WindowInsets & 沉浸式 (`WinCompatExt`)**:
  基于 `WindowCompat` 和 `enableEdgeToEdge` 的现代化窗口控制。
  ```kotlin
  // 开启沉浸式 (自动处理状态栏/导航栏颜色)
  immerse(
      type = Type.statusBars(), 
      statusIsBlackText = true, 
      navigationIsBlackLine = true
  )
  
  // 仅设置状态栏外观
  setAppearance(isBlackText = true, color = Color.WHITE)
  
  // 隐藏/显示系统栏
  hideSystemUI()
  showSystemUI()
  
  // 获取高度
  getStatusBarsHeight()
  getNavigationBarsHeight()
  ```

- **Fragment 管理与转场 (`ActivityUtilsExt`, `TransitionExt`)**:
  ```kotlin
  // Fragment 事务简化
  supportFragmentManager.replaceFragment(fragment, R.id.container)
  supportFragmentManager.addFragmentToActivity(fragment, R.id.container)
  fragment.show(supportFragmentManager, R.id.container)
  
  // 共享元素动画
  val options = activity.getActivityOptions(sharedView, "transitionName")
  activity.startNewActivity(options, TargetActivity::class.java)
  
  // 获取 Transition 资源
  context.getTransition(R.transition.slide)
  ```

### 2. View & UI 增强
简化 View 动画、布局、约束、文本样式及常用控件操作。

- **View 基础 (`ViewExt`)**:
  ```kotlin
  // 点击动画效果
  view.setClicksAnimate()
  
  // 圆角裁剪
  view.clipRound(10f.dp2px)
  
  // 宽高比设置
  view.layoutRatio(ratioW = 16, ratioH = 9)
  
  // 可见性检查
  view.isCompletelyVisible(parentView)
  ```

- **EditText 输入流 (`EditTextExt`)**:
  ```kotlin
  // 监听输入变化转为 Flow
  editText.asFlow().collect { text -> ... }
  
  // 监听输入变化转为 LiveData
  editText.asLiveData().observe(owner) { text -> ... }
  ```

- **AppBar & Menu (`AppBarExt`, `MenuExt`)**:
  ```kotlin
  // 启用/禁用 AppBarLayout 滑动
  appBarLayout.enableAppBar(true)
  
  // 设置所有菜单项可见性
  menu.setVisible(true)
  ```

- **ConstraintLayout 动态布局 (`ConstraintSetExt`)**:
  DSL 风格的约束设置。
  ```kotlin
  constraintLayout.reLayout {
      topToParent(viewId, margin = 10)
      startToStart(viewId, targetId)
      // ...
  }
  ```

- **文本样式 (`SpanExt`, `StringExt`)**:
  ```kotlin
  // 链式构建 Span
  textView.text = "Hello".toSizeSpan(0..5, 1.5f).toColorSpan(...)
  
  // 高亮关键词
  textView.text = highString("Content with key", listOf("key"), "#FF0000")
  
  // HTML 解析
  "<b>Bold</b>".fromHtml()
  ```

- **动画 (`AnimatorExt`, `LayoutAnimateExt`)**:
  ```kotlin
  // 旋转动画
  view.rotation(duration = 1000)
  
  // 布局变化动画
  viewGroup.addLayoutChangeAnim { ... }
  ```

- **RecyclerView (`RecyclerViewExt`)**:
  ```kotlin
  recyclerView.smoothToPosition(pos)
  recyclerView.isCompleteVisibleScreen(pos)
  ```

### 3. 数据处理与工具
JSON 解析、字符串处理、日志打印、数据流操作等。

- **String 常用扩展 (`StringExt`)**:
  ```kotlin
  // 校验
  "13800000000".isPhone()
  "420...".isIdCard()
  
  // 转换
  "content".md5
  "#FFFFFF".toColor
  
  // 复制到剪贴板
  "text".copy(context)
  
  // 读取 Assets 文件
  getAssetsString(context, "config.json")
  ```

- **JSON & Gson (`GsonExt`, `StringExt`)**:
  ```kotlin
  // JSON 转换
  obj.toJson()
  jsonString.toBean<User>()
  jsonString.toList<User>()
  jsonString.toMap()
  
  // Gson Type 构建
  val listType = GsonExt.list(User::class.java)
  val mapType = GsonExt.map(String::class.java, User::class.java)
  ```

- **日志打印 (`StringExt`)**:
  提供基于 Timber 的便捷日志打印，支持 JSON 格式化和长日志分段。
  ```kotlin
  "message".logI()
  "error".logE()
  exception.logE()
  
  // 打印 JSON（自动格式化）
  jsonString.logJson()
  
  // 打印超长日志（分段）
  longString.logChunked()
  
  // UI 生命周期日志
  "onResume".logUILife()
  ```

- **LiveData & Flow (`LiveDataExt`, `FlowExt`)**:
  ```kotlin
  // 防抖与节流
  liveData.throttleLast(1000)
  liveData.throttleFirst(1000)
  
  flow.throttleFirst(1000)
  ```

- **反射与泛型 (`ClassExt`, `TypeExt`)**:
  ```kotlin
  // 获取父类泛型类型 (如 BaseActivity<ViewModel>)
  val clazz = getClazz<MyViewModel>(this)
  
  // 获取接口泛型类型
  val clazzInterface = getClazzByInterface<MyInterface>(this)
  
  // 创建泛型实例 (无参构造)
  val obj = getObjByClassArg<MyObject>(this)
  
  // 获取 Raw Type
  val rawType = getRawType(type)
  ```

- **数据转换 (`DataExt`)**:
  ```kotlin
  10.dp2px
  100.toNumberCH() // 转中文数字
  runTimeMillis { /* code */ } // 计算执行时间
  ```

### 4. 组件集成与工具
Intent、Navigation、Glide、Uri、Saver 等工具集成。

- **Intent 跳转 (`IntentExt`)**:
  ```kotlin
  context.openUri("https://...")
  context.openMarket()
  context.openSetting()
  context.openNotificationSetting()
  context.sendEmailText(...)
  ```

- **Navigation 导航 (`NavExt`)**:
  ```kotlin
  // 修复 FragmentContainerView 中查找 NavController 的问题
  activity.fixFindNavController(R.id.nav_host)
  
  // 获取 SavedStateHandle (支持当前栈和上一级栈)
  navController.getCurSavedStateHandle()
  navController.getPreSavedStateHandle()
  ```

- **Glide 图片加载 (`GlideExt`)**:
  ```kotlin
  imageView.loadImage(url)
  imageView.loadImageBitmap(url)
  
  // 下载并保存图片到相册
  saveImage(context, url)
  ```

- **文件与 Uri (`ContentExt`, `UriExt`)**:
  ```kotlin
  // 创建兼容 Android Q 的图片/视频 Uri
  createImagePathUri(context)
  createVideoPathUri(context)
  
  // 解析 Uri 参数
  uri.parseUriParams()
  ```

- **数据持久化 (`SaverExt`)**:
  ```kotlin
  // 创建并更新 Saver (键值对存储)
  saverCreate("group", "key", "value").updateToDB()
  ```

- **线程切换 (`ArmExt`, `CoroutineExt`)**:
  ```kotlin
  runOnMain { ... }
  runOnIo { ... }
  
  // 协程中切换
  data.withIO { ... }
  ```

### 5. 基础工具类 (Tools)
位于 `me.shetj.base.tools.file` 包下，提供文件、IO、SP 等静态工具方法。

- **文件操作 (`FileUtils`)**:
  ```kotlin
  FileUtils.isFileExists(path)
  FileUtils.createOrExistsFile(path)
  FileUtils.deleteFile(path)
  FileUtils.copyFile(src, dest, listener)
  FileUtils.rename(file, newName)
  FileUtils.getFileMD5ToString(path)
  ```

- **文件读写 (`FileIOUtils`)**:
  ```kotlin
  // 字符串写入
  FileIOUtils.writeFileFromString(file, content)
  // 读取为字符串
  val content = FileIOUtils.readFile2String(file)
  // 读取为 List
  val lines = FileIOUtils.readFile2List(file)
  ```

- **Android Q 适配 (`FileQUtils`)**:
  ```kotlin
  // 获取 URI 对应的绝对路径 (兼容 Q+)
  FileQUtils.getFileAbsolutePath(context, uri)
  ```

- **SharedPreferences (`SPUtils`)**:
  ```kotlin
  SPUtils.put(context, "key", value)
  val value = SPUtils.get(context, "key", default)
  SPUtils.remove(context, "key")
  SPUtils.clear(context)
  ```

- **字符串工具 (`StringUtils`)**:
  ```kotlin
  StringUtils.isPhone(number)
  StringUtils.isIdCard(id)
  StringUtils.numberToCH(123) // "一百二十三"
  ```

- **环境路径 (`EnvironmentStorage`)**:
  ```kotlin
  val sdCard = EnvironmentStorage.sdCardPath
  val cache = EnvironmentStorage.cache
  ```

### 6. 日志与调试 (Debug Tools)
位于 `me.shetj.base.tools.debug` 包下，提供高性能的日志管理系统。

- **LogManager**: 核心日志管理器，支持缓冲写入、文件分割、Crash 保护。
  ```kotlin
  // 初始化 (通常在 Application 中)
  LogManager.init {
      isEnable = true
      isPrintToConsole = BuildConfig.DEBUG
      logDir = "..." // 自定义日志路径
      bufferSize = 50 // 缓冲条数
  }
  
  // 记录日志
  LogManager.log(LogLevel.INFO, "Tag", "Message")
  LogManager.log(LogLevel.ERROR, "Tag", "Error Message")
  
  // 强制刷新 (Crash 时自动调用)
  LogManager.flushSync()
  ```

- **DebugFunc**: 旧版兼容入口，现作为 LogManager 的门面。
  ```kotlin
  DebugFunc.getInstance().initContext(context)
  DebugFunc.getInstance().logBehavior("UserAction", "Click Button")
  ```

- **BaseUncaughtExceptionHandler**: 全局异常捕获，自动保存 Crash 堆栈到日志文件。

## 目录结构
- **ktx/**
  - `ActivityResultExt.kt`: ActivityResultLauncher 封装。
  - `WinCompatExt.kt`: 窗口与沉浸式适配。
  - `ContextKt.kt`: Context 扩展。
  - `ActivityExt.kt` / `FragmentExt.kt`: 基础扩展。
  - `ActivityUtilsExt.kt`: Fragment 事务与转场工具。
  - `ViewExt.kt`: View 基础扩展。
  - `EditTextExt.kt`: EditText 转 Flow/LiveData。
  - `ConstraintSetExt.kt`: 约束布局扩展。
  - `SpanExt.kt` / `StringExt.kt`: 字符串与富文本。
  - `GsonExt.kt`: Gson 工具。
  - `DateKit.kt`: 日期工具。
  - `LiveDataExt.kt` / `FlowExt.kt`: 数据流扩展。
  - `IntentExt.kt`: 系统 Intent 跳转。
  - `GlideExt.kt`: 图片加载。
  - `ContentExt.kt`: Uri/File 创建兼容。
  - `NavExt.kt`: Navigation 扩展。
  - `ClassExt.kt` / `TypeExt.kt`: 反射与泛型工具。
- **tools/file/**
  - `FileUtils.kt`: 文件操作工具。
  - `FileIOUtils.kt`: 文件读写工具。
  - `FileQUtils.kt`: Android Q 适配工具。
  - `SPUtils.kt`: SharedPreferences 工具。
  - `StringUtils.kt`: 字符串工具。
  - `EnvironmentStorage.kt`: 存储路径工具。
- **tools/debug/**
  - `LogManager.kt`: 日志管理核心。
  - `LogConfig.kt`: 日志配置。
  - `DebugFunc.kt`: 调试工具门面。
  - `BaseUncaughtExceptionHandler.kt`: Crash 捕获。
