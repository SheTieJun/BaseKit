### 导航栏，状态栏高度和状态 11
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

## 包可见 Android 11

<queries>
<package android:name="com.tencent.mm" />
<package android:name="com.tencent.mobileqq" />
<package android:name="com.sina.weibo" />
</queries>

## 文件访问 Android 11

```xml
   <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
        tools:ignore="ScopedStorage" />
```

```kotlin
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
    // 有权限,把老的文件移到新的目录
} else {
    OrangeWeikeDialog.Builder(context)
        .title("温馨提示")
        .content("因为应用市场要求强制兼容Android11，需要您手动授权获取应用外部存储权限，否则会丢失您以前的录音")
        .positiveText("去获取")
        .onPositive { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(java.lang.String.format("package:%s", context.packageName))
                context.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                context.startActivity(intent)
            }
        }
        .show()
    return true
}

/**
 *  读取外部存储的权限
 */
fun checkHasExternalFile(context: Activity, isRequest: Boolean = true): Boolean {
    return if (VERSION.SDK_INT < VERSION_CODES.R || Environment.isExternalStorageManager()) {
        context.hasPermission(permission.READ_EXTERNAL_STORAGE, isRequest = isRequest)
    } else {
        if (isRequest) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(java.lang.String.format("package:%s", context.packageName))
                context.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                context.startActivity(intent)
            }
        }
        false
    }
}
```