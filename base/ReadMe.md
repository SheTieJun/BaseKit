## Base

#### 初始化
```
class APP : Application() {
    override fun onCreate() {
        super.onCreate()
        S.init(this, true, "https://xxxx.com")
        initKoin(allModules)
    }
}
```

### [motionLayout](src/main/java/me/shetj/base/anim/motion)

### [MVP](src/main/java/me/shetj/base/mvp)

### [MVVM](src/main/java/me/shetj/base/mvvm)

### [KTX](src/main/java/me/shetj/base/ktx)

### [分享](src/main/java/me/shetj/base/share)
```
    Share.shareText(activity,content = "测试")
```

### [NETWORK](src/main/java/me/shetj/base/network)
``` kotlin
        RxHttp.get(testUrl)
                .executeCus(simpleNetCallBack)
```

### [NETWORK_COROUTINE](src/main/java/me/shetj/base/network_coroutine)
```kotlin
     KCHttp.get<ResultMusic>(testUrl)
```


### [Saver](src/main/java/me/shetj/base/saver)
```kotlin
 saverCreate(key = "测试key", value = "测试value").apply {
                saverDB.insert(this)
                        .subscribeOn(Schedulers.io())
                        .subscribe()
            }

```