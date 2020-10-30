## Hilt使用文档

1. 添加依赖
```
 classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_version"
    implementation "com.google.dagger:hilt-android:2.28-alpha"
    kapt "com.google.dagger:hilt-android-compiler:2.28-alpha"
```

注意事项：
```
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
```

2. 初始化
所有应用都要给`Application`添加`@hiltAndroidApp`
```
@HiltAndroidApp
class ExampleApplication : Application()
```

3. 给需要依赖注入的类进行标记
```
@AndroidEntryPoint
class ExampleActivity : AppCompatActivity()
```
一共：
```
Application（通过使用 @HiltAndroidApp）
Activity
Fragment
View
Service
BroadcastReceiver
```

4. 给注解执行字段进行标记
```
@Inject lateinit var i
```
由 Hilt 注入的字段不能为私有字段。尝试使用 Hilt 注入私有字段会导致编译错误。

5. 构造函数注入,带有注释的构造函数的参数即是该类的依赖项
```
class AnalyticsAdapter @Inject constructor() 
```

6. Hilt 模块 ：@Module
您必须使用 @InstallIn 为 Hilt 模块添加注释，以告知 Hilt 每个模块将用在或安装在哪个 Android 类中。

7. @Binds 注入接口实例
```
    @Binds  //抽象实例 （括号里面是具体实现）
    abstract fun getIView(iView: KtTestActivity):IView
```

8. @Provides 注入实例
带有注释的函数会向 Hilt 提供以下信息：

函数返回类型会告知 Hilt 函数提供哪个类型的实例。
函数参数会告知 Hilt 相应类型的依赖项。
函数主体会告知 Hilt 如何提供相应类型的实例。每当需要提供该类型的实例时，Hilt 都会执行函数主体。

9. 同一个类型多个绑定
```

@Qualifier
annotation class main1

@Qualifier
annotation class main2
```

10. Hilt 中的预定义限定符 @ActivityContext @ApplicationContext
由于您可能需要来自应用或 Activity 的 Context 类，因此 Hilt 提供了 @ApplicationContext 和 @ActivityContext 限定符。
```
class AnalyticsAdapter @Inject constructor(
    @ActivityContext private val context: Context,
    private val service: AnalyticsService
) { ... }
```

11. 在 Hilt 不支持的类中注入依赖项
 需要为所需的每个绑定类型定义一个带有 @EntryPoint 注释的接口并添加限定符。然后，添加 @InstallIn 以指定要在其中安装入口点的组件，
```

```
