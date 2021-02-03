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
- 特定于应用的目录 –> 无需权限 –> 访问方法 getExternalFilesDir () –> 卸载应用时移除文件
- 媒体集合 (照片、视频、音频) –> 需要权限 READ_EXTERNAL_STORAGE (仅当访问其他应用的文件时) –> 访问方法 MediaStore –> 卸载应用时不移除文件
- 下载内容（文档和电子书籍）–> 无需权限 –> 存储访问框架（加载系统的文件选择器）–> 卸载应用时不移除文件