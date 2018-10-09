# Base
   自用[include](include.MD)



最新
```
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
    }
```

```
  implementation 'com.github.SheTieJun:Base:master-SNAPSHOT'
```

或者自己用
```
buildscript {
    ext.base_version ="21bbbba885" //最新的提交
    }
```

```
    implementation "com.github.SheTieJun:Base:$base_version"
```