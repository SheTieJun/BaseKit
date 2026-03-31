## gradle相关
> 记录一下gradle的一些用法

### 2026年3月 Gradle生态升级记录 (Gradle 9.x + AGP 9.x)
- **Gradle Wrapper**: 升级至 **9.3.1**
- **Android Gradle Plugin (AGP)**: 升级至 **9.0.0**
- **Kotlin**: 升级至 **2.3.20**，配置中显式声明 `apiVersion = KOTLIN_2_0`，启用 JVM Toolchain 17。
- **Android SDK**: `compileSdk` 和 `targetSdk` 升级至 **36** (Android 16)。
- **R8 Compiler**: 为修复 Java 17 环境下新版 Kotlin 编译引发的 `Sealed classes are not supported as program classes` 错误，在根目录的 `build.gradle.kts` 中强制指定 `classpath("com.android.tools:r8:8.9.35")`。
- **Version Catalogs**: 全面推进并更新了大量的依赖，包括 `compose-bom: 2026.03.01`、`ksp: 2.3.6`、`koin: 4.2.0`、`retrofit: 3.0.0` 等。

###  Gradle加载优先级
```
buildTypes > productFlavors > Main > dependencies
```

### 渠道配置
```
  productFlavors {
        higher {
            buildConfigField 'boolean', 'isLog', "false"
            manifestPlaceholders = [
                    app_icon    : "@mipmap/ic_launcher_normal",
                    channelName : "higher",
                    verNum      : "2",
                    app_name_new: "baseNew"
            ]
        }
  }
```

### 特定资源
在 buildTypes, productFlavors 中定义 resConfigs
```
android {
    ...
    defaultConfig {
        ...
        resConfigs "zh","en"//剔除不需要的国际化
        resConfigs "hdpi","xhdpi","xxhdpi","xxxhdpi"//剔除自身，三方库，sdk中不需要的dpi资源
        ndk {
            //选择要添加的对应cpu类型的.so库。
            abiFilters 'armeabi', 'armeabi-v7a' //, 'x86', 'arm64-v8a', 'x86_64', 'mips', 'mips64'
        }
    }
}
```

### 一些命令
`gradlew task --all`  //查看所有的任务
`dependencies` 查看依赖
`gradlew wrapper -v ` 查看当前版本号
`gradlew –daemon` 开启守护线程 `–no-daemon`取消
