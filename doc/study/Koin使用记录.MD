```
 https://github.com/InsertKoinIO/koin
```

demo
```
https://github.com/skydoves/DisneyMotions
```

1. 配置
    `ext.koin_version            = '2.1.6'`
    ```
      dependencies {
        ...
        classpath "org.koin:koin-gradle-plugin:$koin_version"
    } 
   ```

    ```
       // Koin AndroidX Scope features
    api "org.koin:koin-androidx-scope:$koin_version"
    // Koin AndroidX ViewModel features
    api "org.koin:koin-androidx-viewmodel:$koin_version"
    // Koin AndroidX Fragment features
    api "org.koin:koin-androidx-fragment:$koin_version"
    // Koin AndroidX Experimental features
    api "org.koin:koin-androidx-ext:$koin_version" 
   ```


```
   
val appModule = module() {
    single { UpToken() } //单例

}



val mvvmModule = module {
    viewModel (named("test")){ object :ViewModel(){} }
}

/**
 * Scope就是一个实例的作用范围，一旦该作用范围结束，该实例就是从容器中移除。
 *  single： 单实例scope，该scope下的实例不会被移除
 *  factory： 每次都是创建新实例
 *  scoped： 自定义scope
 */
val mvpModule = module {
    //工程创建？  val presenter : Presenter by inject { parametersOf(view) }
    factory { (view:IView) -> CommonPresenter(view) }
    //单例
    single { CommonModel() }

    // // Inject presenter from MVPActivity's scope
    //    val scopedPresenter: MainPresenter by lifecycleScope.inject()
    scope<MainActivity> {
        scoped { MainPresenter(get()) }
    }
}

val scopeModule = module {

}

val allModules = appModule + mvpModule + mvvmModule + scopeModule
```

```
    startKoin {
            androidLogger()
            androidContext(application)
            androidFileProperties()
            fragmentFactory()
            modules(allModules)
        }
```

```
   loadKoinModules(modules)
```