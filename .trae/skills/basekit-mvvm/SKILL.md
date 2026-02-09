---
name: "basekit-mvvm"
description: "Guide for BaseKit MVVM architecture. Invoke when implementing Activities, Fragments, ViewModels, or Adapters using BaseKit."
---

# BaseKit MVVM & Base Skills

## 简介
BaseKit 提供了一套基于 Android Jetpack (MVVM + ViewBinding + Coroutines) 的快速开发框架。封装了 Activity、Fragment、Adapter 以及 ViewModel 的基础实现，旨在简化代码，提升开发效率。

## 核心功能 (Skills)

### 1. MVVM 架构支持
位于 `me.shetj.base.mvvm` 包下，提供标准的 MVVM 实现。
- **自动绑定**: `BaseBindingActivity` 和 `BaseBindingFragment` 利用泛型和反射自动初始化 `ViewBinding` 和 `ViewModel`，减少样板代码。
- **UI 事件总线**: `BaseViewModel` 内置 `baseAction` (SingleLiveEvent)，统一分发 `TipAction` (Toast/Snack)、`NetErrorAction`、`LoadingAction` 等事件，Activity/Fragment 自动订阅处理。
- **状态保存**: `SaveStateViewModel` 集成 `SavedStateHandle`，支持进程重建后的数据恢复。
- **Koin 支持**: 提供 `BaseKoinBindingActivity` 等类（如果需要），便于集成 Koin 依赖注入。

### 2. 增强型基类 (Base Components)
位于 `me.shetj.base.base` 包下，提供丰富的通用能力。
- **主题与适配**: 支持灰度模式 (`GrayThemeLiveData`)，屏幕方向设置，多语言上下文适配 (`LanguageKit`)。
- **生命周期管理**: 内置生命周期日志打印 (`logUILife`)，方便调试 UI 生命周期。
- **交互控制**: 封装 `OnBackPressedCallback` 处理返回逻辑；处理 Window 焦点和系统 UI 隐藏。

### 3. 高效 Adapter
基于 `BaseRecyclerViewAdapterHelper` 的扩展。
- **BaseViewBindingAdapter**: 结合 ViewBinding，消除 findViewById，直接在 `convert` 方法中使用 Binding 对象。
- **BaseKTAdapter**: 注入 LifecycleOwner，提供 `lifeKtScope` 协程作用域，防止 Adapter 中的异步操作内存泄漏。
- **BaseSAdapter**: 集成 `SelectionTracker`，支持列表项的多选功能。

## 技术栈
- **Language**: Kotlin
- **Android Jetpack**: ViewBinding, ViewModel, LiveData, Lifecycle, SavedStateHandle
- **Async**: Coroutines (Kotlin 协程)
- **Dependency**: BaseRecyclerViewAdapterHelper (BRVAH)

## 快速开始

### 继承 BaseBindingActivity
```kotlin
// 泛型指定 ViewBinding 和 ViewModel
class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    override fun initViewModel(): MainViewModel {
        // 可选重写，默认反射创建
        return super.initViewModel()
    }

    override fun initView(savedInstanceState: Bundle?) {
        // mBinding 直接使用，无需 setContentView
        mBinding.tvTitle.text = "Hello BaseKit"
        
        // mViewModel 直接使用
        mViewModel.testAction()
    }
}
```

### 定义 ViewModel
```kotlin
class MainViewModel : BaseViewModel() {
    fun testAction() {
        // 发送 UI 事件，Activity 会自动处理（如弹 Toast）
        baseAction.value = TipAction(TipType.INFO, "操作成功")
    }
}
```

### 使用 BaseViewBindingAdapter
```kotlin
// 泛型指定数据类型和 Item ViewBinding
class MyAdapter(data: MutableList<String>) : 
    BaseViewBindingAdapter<String, ItemBinding>(R.layout.item_layout, data) {

    override fun convert(holder: ItemBinding, item: String) {
        holder.tvName.text = item
    }
}
```

## 目录结构说明
- `me.shetj.base.mvvm`
  - `viewbind/`: 包含 MVVM 核心基类 `BaseBindingActivity`, `BaseBindingFragment`, `BaseViewModel`。
- `me.shetj.base.base`
  - `AbBaseActivity/Fragment`: 顶层抽象基类，处理底层系统交互。
  - `Base*Adapter`: 针对 RecyclerView 的各种增强适配器。
