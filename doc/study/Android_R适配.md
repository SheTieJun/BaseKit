### 导航栏，状态栏高度和状态
```
content.setOnApplyWindowInsetsListener { view, windowInsets ->
    //状态栏
    val statusBars = windowInsets.getInsets(WindowInsets.Type.statusBars())
    //导航栏
    val navigationBars = windowInsets.getInsets(WindowInsets.Type.navigationBars())
    //键盘
    val ime = windowInsets.getInsets(WindowInsets.Type.ime())
    windowInsets
}
```
显示或者隐藏
```
 activity.window.insetsController?.hide(WindowInsets.Type.navigationBars())
 activity.window.insetsController?.show(WindowInsets.Type.navigationBars())
```

### 存储分区必须兼容