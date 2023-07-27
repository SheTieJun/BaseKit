## 主题切换工具类 MD3ThemeKit

### 1. 介绍
基于material - MD的颜色系统做主题切换

比如：
- [MD3 Color Roles](https://m3.material.io/styles/color/the-color-system/color-roles)

如果你使用上面的颜色标准进行设计APP,后面想要切换主题，那么就可以使用这个工具类进行切换


### 2. 使用
#### 1. 初始化
```kotlin
MDThemeKit.startInit(context, listOf(
    ThemeBean(R.style.BaseTheme_MD3, "默认主题-跟随系统"),
    ThemeBean(R.style.BaseTheme_MD3, "默认主题-黑夜", AppCompatDelegate.MODE_NIGHT_YES),
    ThemeBean(R.style.BaseTheme_MD3, "默认主题-日间", AppCompatDelegate.MODE_NIGHT_NO),
    ThemeBean(R.style.Base_Theme_LF_Green, "春色宜人(GREEN)", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY), 
    ThemeBean(R.style.Base_Theme_LF_RED, "红红火火(RED)", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY),
))
```
#### 2. 快速使用1：使用自带的主题切换弹窗显示
```kotlin
MD3ThemeKit.showChangeThemeDialog(fragmentActivity)
```
#### 2. 快速使用2：代码直接设置
```kotlin
fun updateTheme(theme: ThemeBean) 
fun updateThemeByType(@StyleRes style: Int, name: String,@NightMode nightMode: Int = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
```
```kotlin
MD3ThemeKit.updateThemeByType(style.BaseTheme_MD3, "默认主题-跟随系统11")

```
#### 3. 其他相关方法，如果你想自定义主题切换弹窗，可以使用下面的方法

```kotlin
    val isDark: Boolean
    
    fun getCurrentTheme(): SingleLiveEvent<ThemeBean>  
    
    fun clearTheme()  
    
    fun getThemeList(): ArrayList<ThemeBean>  
    
    fun getThemeTitleList(): List<String>  
    
    fun getThemeByWhich(which: Int): ThemeBean  
    
    fun getThemeNameByWhich(which: Int): String  
    
    fun getNightModeByWhich(which: Int): Int  
    
    fun getIndexByTheme(theme: ThemeBean) 
    
    fun updateTheme(theme: ThemeBean) 
    
    fun updateTheme(@StyleRes style: Int, name: String,@NightMode nightMode: Int = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY) 
    
    fun applyThemeOverlay(context: Context, @StyleRes theme: Int)  
```

### 已知问题
那就是如果APP的闪图界面使用了`Theme.SplashScreen.IconBackground`将无法修改，
因为postSplashScreenTheme被固定了
```XML
    <style name="App.SplashScreen" parent="Theme.SplashScreen.IconBackground">
        <item name="windowSplashScreenAnimatedIcon">@drawable/unsplash_cat1</item>
        <item name="windowSplashScreenBackground">@color/white</item>
        <item name="windowSplashScreenIconBackgroundColor">@color/white</item>

        <!--        must be set-->
        <item name="postSplashScreenTheme">@style/BaseTheme.MD3</item> 
    </style>
```