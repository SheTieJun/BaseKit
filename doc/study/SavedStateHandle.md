# SavedStateHandle 使用指南

## 概述

`SavedStateHandle` 是 Android Jetpack `lifecycle-viewmodel-savedstate` 提供的组件，用于在 **进程被系统杀死后恢复 ViewModel 中的数据**。它解决了 ViewModel 在进程死亡时数据丢失的问题，弥补了 ViewModel 只存活于内存的短板。

### 核心对比

| 机制 | 配置变更（旋转） | 进程死亡 | 用户主动关闭 |
|------|:---:|:---:|:---:|
| ViewModel | 保留 | **丢失** | 丢失 |
| onSaveInstanceState | 保留 | 保留 | 保留 |
| SavedStateHandle | 保留 | **保留** | 丢失 |

**结论**：`SavedStateHandle` 是 ViewModel 中的数据在进程死亡时的保底方案。

---

## 依赖

```kotlin
// build.gradle.kts
dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.7")
}
```

主流的 `lifecycle-viewmodel-ktx` 和 `activity-ktx` / `fragment-ktx` 已传递依赖此库，通常无需额外声明。

---

## 基础用法

### 1. 在 ViewModel 构造函数中声明

```kotlin
class MyViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 读取，key 不存在时返回默认值
    val userId: String = savedStateHandle["userId"] ?: ""

    // 写入
    fun setUserId(id: String) {
        savedStateHandle["userId"] = id
    }
}
```

### 2. 使用 LiveData / StateFlow 绑定

```kotlin
class SearchViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 绑定到 LiveData —— 读写操作自动同步
    val query: LiveData<String> = savedStateHandle.getLiveData("query", "")

    // 绑定到 StateFlow
    val filter: StateFlow<Int> = savedStateHandle.getStateFlow("filter", 0)
}
```

### 3. 复杂类型的读写

```kotlin
class FormViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class FormData(val name: String, val age: Int, val email: String)

    // 通过 get<T>() / set() 操作可序列化对象
    var formData: FormData?
        get() = savedStateHandle["form_data"]
        set(value) { savedStateHandle["form_data"] = value }
}
```

> **注意**：复杂对象必须实现 `Parcelable` 或 `Serializable`，才能被 SavedState 序列化。

---

## 使用场景

### 场景 1：搜索页面 —— 恢复搜索关键词

用户输入搜索词后 App 被后台杀死，重新打开时恢复之前的搜索内容和结果。

```kotlin
class SearchViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _query = savedStateHandle.getStateFlow("search_query", "")

    val searchResults: StateFlow<List<Item>> = _query
        .debounce(300)
        .flatMapLatest { query -> searchRepository.search(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChanged(query: String) {
        savedStateHandle["search_query"] = query
    }
}
```

### 场景 2：表单页面 —— 防丢失已填写内容

用户填写了一半的表单，切到后台后被系统回收进程，回来时已填内容还在。

```kotlin
class FormViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val name: MutableStateFlow<String> = savedStateHandle
        .getStateFlow("form_name", "")
        .let { flow ->
            // 创建一个可写的 StateFlow，同时同步到 SavedStateHandle
            object : MutableStateFlow<String> by flow as MutableStateFlow<String> {
                override var value: String
                    get() = flow.value
                    set(value) {
                        savedStateHandle["form_name"] = value
                        (flow as MutableStateFlow).value = value
                    }
            }
        }
}
```

> 更推荐直接通过属性委托简化：

```kotlin
class FormViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    init {
        // 恢复已保存的值
        _name.value = savedStateHandle["form_name"] ?: ""
    }

    fun onNameChanged(value: String) {
        _name.value = value
        savedStateHandle["form_name"] = value // 每次变更都同步写入
    }
}
```

### 场景 3：分页列表 —— 恢复滚动位置

列表滑到第 N 页，进程死亡后恢复上次浏览位置。

```kotlin
class PagingViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _currentPage = savedStateHandle.getStateFlow("page", 1)

    fun loadPage(page: Int) {
        savedStateHandle["page"] = page
        // 触发数据加载...
    }
}
```

### 场景 4：多 Tab 页面 —— 恢复当前选中的 Tab

底部导航栏或 ViewPager 切换 Tab 后被杀，恢复时回到之前的 Tab。

```kotlin
class MainViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val selectedTab: StateFlow<Int> = savedStateHandle.getStateFlow("tab", 0)

    fun onTabSelected(index: Int) {
        savedStateHandle["tab"] = index
    }
}
```

### 场景 5：筛选/过滤 —— 恢复筛选条件

商品列表的多维度筛选，进程死亡后筛选条件还在。

```kotlin
class FilterViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val minPrice: StateFlow<Int> = savedStateHandle.getStateFlow("min_price", 0)
    val maxPrice: StateFlow<Int> = savedStateHandle.getStateFlow("max_price", Int.MAX_VALUE)
    val category: StateFlow<String> = savedStateHandle.getStateFlow("category", "")
    val sortBy: StateFlow<String> = savedStateHandle.getStateFlow("sort_by", "default")
}
```

### 场景 6：Navigation Compose 参数传递

使用 Navigation Compose 时，`SavedStateHandle` 可以直接接收导航参数，并且自动存活进程死亡。

```kotlin
class DetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 自动从导航参数中提取 itemId，仅首次取值时生效
    private val itemId: String = savedStateHandle["itemId"] ?: ""

    val detail: StateFlow<Detail?> = flow {
        emit(repository.getDetail(itemId))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
```

对应导航图定义：

```kotlin
NavHost(navController, startDestination = "list") {
    composable(
        route = "detail/{itemId}",
        arguments = listOf(navArgument("itemId") { type = NavType.StringType })
    ) { backStackEntry ->
        val viewModel: DetailViewModel = hiltViewModel() // 由 Hilt 注入 SavedStateHandle
        // ...
    }
}
```

---

## 最佳实践

### 1. 只存必要的 UI 状态

不要把所有 ViewModel 数据都塞进 `SavedStateHandle`。只保存需要在进程死亡后恢复的关键状态：

- 搜索关键词、筛选条件
- 当前页码
- 选中的 Tab
- 表单已填内容
- 列表滚动位置索引

### 2. 注意数据量限制

`SavedStateHandle` 底层复用的是 `Bundle`，建议总数据量控制在 **50KB 以内**。大数据（如 Bitmap、长列表）不要存入。

### 3. 复杂对象实现 Parcelable

```kotlin
@Parcelize
data class User(val id: String, val name: String) : Parcelable

// 读写
savedStateHandle["current_user"] = user          // 写入
val user: User? = savedStateHandle["current_user"] // 读取
```

### 4. 与 Hilt 配合

Hilt 1.0+ 自动支持 `SavedStateHandle` 注入，无需额外配置：

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MyRepository
) : ViewModel()
```

### 5. 与 Koin 配合

```kotlin
val appModule = module {
    viewModel { MyViewModel(get<SavedStateHandle>(), get()) }
}
```

### 6. 清理不需要的数据

当用户离开某个页面不再返回时，主动清理以避免内存浪费：

```kotlin
fun clearFormData() {
    savedStateHandle.remove<FormData>("form_data")
}
```

---

## 常见问题

### Q：SavedStateHandle 和 onSaveInstanceState 有什么区别？

`onSaveInstanceState` 是 Activity/Fragment 层面的状态保存，数据量限制更严格（约 1MB 但推荐更小）。`SavedStateHandle` 位于 ViewModel 层，天然和 ViewModel 的生命周期绑定，使用更便捷。

**实际使用建议**：优先用 `SavedStateHandle`，仅 View 特有的瞬时状态（如 EditText 光标位置）才用 `onSaveInstanceState`。

### Q：进程被杀死后，SavedStateHandle 的数据能保留多久？

数据会被序列化到系统的 Saved State Registry，只要用户不主动从最近任务中划掉 App，数据就会保留。

### Q：getLiveData / getStateFlow 和直接 get/set 怎么选？

- **getLiveData / getStateFlow**：适合 UI 层通过 observer 绑定的数据，读写自动同步
- **get / set**：适合在 ViewModel 内部逻辑中使用的数据，手动控制读写时机
