## gradle相关
> 记录一下gradle的一些用法

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
