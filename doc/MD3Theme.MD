## 主题切换工具类 MD3ThemeKit

### 1. 介绍
基于material - MD3的颜色系统做主题切换

### 2. 使用
1. 默认通过start-up进行初始化,
```xml
      <provider
            android:name="androidx.startup.InitializationProvider"
            android:authorities="${applicationId}.androidx-startup"
            android:exported="false"
            tools:node="merge">
            <meta-data
                android:name="me.shetj.base.init.MD3ThemeInitialize"
                android:value="androidx.startup" />
        </provider>

```
如果需要移除
```xml
      <provider
            android:name="androidx.startup.InitializationProvider"
            android:authorities="${applicationId}.androidx-startup"
            android:exported="false"
            tools:node="remove">
            <meta-data
                android:name="me.shetj.base.init.MD3ThemeInitialize"
                android:value="androidx.startup" />
        </provider>

```
2. 默认使用自带的主题切换，弹窗显示
```kotlin
MD3ThemeKit.showChangeThemeDialog(fragmentActivity)
```

3. 自定义主题切换-添加可以切换的主题
```kotlin
addTheme(style.BaseTheme_MD3, "默认主题-跟随系统11")
addTheme(style.BaseTheme_MD3, "默认主题-黑夜12", AppCompatDelegate.MODE_NIGHT_YES)
addTheme(style.BaseTheme_MD3, "默认主题-日间13", AppCompatDelegate.MODE_NIGHT_NO)
```
4. 通过代码设置
```kotlin
MD3ThemeKit.updateThemeByType(style.BaseTheme_MD3, "默认主题-跟随系统11")
//fun updateThemeByType(@StyleRes style: Int, name: String,@NightMode nightMode: Int = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
```