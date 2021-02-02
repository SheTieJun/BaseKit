## Base
implementation 'me.shetj.sdk:base:1.0.6'

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
V2
``` kotlin
       doOnIO {
           val data = KCHttpV2.get<ResultMusic>(testUrl)
           data.fold(onSuccess = {
               data.getOrNull()
           },onFailure = {
               null
           })
       }
```

### [Saver](src/main/java/me/shetj/base/saver)
```kotlin
 saverCreate(key = "测试key", value = "测试value").apply {
                saverDB.insert(this)
                        .subscribeOn(Schedulers.io())
                        .subscribe()
            }

```

### [AbLoadingDialog](src/main/java/me/shetj/base/weight/AbLoadingDialog.kt)
```
/**
 * 必须设置：android:configChanges="orientation|keyboardHidden|screenSize"
 */
class SimLoadingDialog : AbLoadingDialog() {

    override fun createLoading(context: Context, cancelable: Boolean): Dialog? {
        val view = LayoutInflater.from(context).inflate(R.layout.base_dialog_loading, null)
        return Dialog(context, R.style.CustomProgressDialog).apply {
            setContentView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
            setCancelable(cancelable)
        }
    }

    companion object {
        /**
         * 和协程一起使用
         */
        inline fun showWithAction(context: Context, crossinline action: suspend () -> Unit): AbLoadingDialog {
            return SimLoadingDialog().showWithAction(context, action)
        }

        /**
         * 和RxJava 一起使用
         */
        fun showWithRxAction(context: Context, action: () -> Disposable): AbLoadingDialog {
            return SimLoadingDialog().showWithRxAction(context, action)
        }

        /**
         * 和RxJava 一起使用
         */
        fun showWithDisposable(context: Context, disposable: Disposable): AbLoadingDialog {
            return SimLoadingDialog().showWithDisposable(context,disposable)
        }

        @JvmStatic
        fun showNoAction(context: Context, cancelable: Boolean = true): Dialog? {
            return SimLoadingDialog().showLoading(context, cancelable)
        }
    }
}
```