# BaseKit

To reduce writing duplicate code

> Program simple things
> Program complex things
> Program repetitive things
> Everything is for efficiency (lazy)

* * *

### frame

-   MVC/MVP/MVVM

MVVM = (ViewBinding/DataBinding)+ViewModel+LiveData

### Libraries

1.  Retrofit- REST API Call
    -   <https://square.github.io/retrofit/>
2.  Glide - Image Loading and caching.
    -   <https://github.com/bumptech/glide>
3.  koin - Dependency Injection
    -   <https://insert-koin.io/>
4.  gson - json serialization/deserialization
    -   <https://github.com/google/gson>
5.  Android Jetpack
    -   <https://developer.android.google.cn/jetpack/>
    -   stable-channel :<https://developer.android.com/jetpack/androidx/versions/stable-channel>
6.  Material Design Components - Google's latest Material Components.
    -   <https://material.io/develop/android>

## Using the Android SDK Upgrade Assistant

The Android SDK Upgrade Assistant is a tool in Android Studio that helps you upgrade the targetSdkVersion (that is, your app's target API level). Be sure to keep your targetSdkVersion updated to take advantage of the latest features of the platform. The Android SDK Upgrade Assistant is available in Android Studio Giraffe and higher.<https://developer.android.com/build/sdk-upgrade-assistant?hl=zh-cn>

* * *

## article

#### [Flow and LiveData operators](https://blog.csdn.net/StjunF/article/details/120872772)

#### [Kotlin coroutine + Retrofit download files and implement progress monitoring](https://blog.csdn.net/StjunF/article/details/120909119)

#### [WindowInsetsControllerCompat uses](https://blog.csdn.net/StjunF/article/details/121840122)

#### [Using ActivityResultLauncher](https://github.com/SheTieJun/BaseKit/wiki/ActivityResultLauncher%E4%BD%BF%E7%94%A8)

#### [DataStoreKit uses](https://github.com/SheTieJun/BaseKit/wiki/DataStoreKit%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)

#### [Theme switching tool based on MD color system](https://github.com/SheTieJun/BaseKit/wiki/MDThemeKit-%EF%BC%9A%E4%B8%BB%E9%A2%98%E5%88%87%E6%8D%A2%E5%B7%A5%E5%85%B7%E7%B1%BB)

* * *

## branch

-   [Base: the branch containing RxJava, a very old branch](https://github.com/SheTieJun/BaseKit/tree/base_rx)
-   [Base: Branch with only viewbinding and no Databinding](https://github.com/SheTieJun/BaseKit/tree/feat_viewbinding)

* * *

## Application scenario record

-   use`Navigation`replace`ARouter`
-   use`start_up`replace`Application`Medium initialization
    -   The privacy policy can be initialized in stages, such as after logging in.
-   Intercept web page requests or dynamically add vConsole
-   Annotation+ASM

* * *

## module type

-   application module
-   data module
-   functional module
-   Common module
    -   interface module
    -   network module
    -   Auxiliary module
-   Communication module (mediation module): used for communication between modules

* * *

![](/doc/img/模块化-依赖项反转.webp)

* * *

### Interface layer related records

1.  Expand interface elements (interface logic) and interface operation elements (business logic)
2.  Define UIState, for example:`TracksUIState`,`TrackItemUIState`![](/doc/img/UI_state_and_logic.webp)

### Data layer related records

1.  DataStore is great for storing key-value pairs such as user settings, examples might include time format, notification preferences, and whether to show or hide news stories the user has read. DataStore can also use protocol buffers to store typed objects.
2.  WorkManager makes it easy to schedule asynchronous, reliable work and take care of managing constraints. We recommend using this library to perform persistence work. To perform the tasks defined above, we create a Worker class: RefreshLatestNewsWorker. This class has NewsRepository as a dependency in order to get the latest news and cache it to disk.
3.  To protect read and write operations from different threads, we use Mutex.
4.  async is used to launch coroutines in an outer scope. await is called on a new coroutine to remain pending until the network request returns a result and the result is saved to the cache. If the user is still on the screen at that time, they will see the latest news; if the user has left the screen, the await will be canceled, but the logic inside async will continue to execute.`luanch`applies to "fire and forget", whereas`async`Applies to "Async and wait for result"
5.  [Data and file storage](https://developer.android.com/training/data-storage?hl=zh-cn)：room /DataStore/ File

### Coroutine best practices

1.  Injecting Dispatchers into classes makes them easy to test as you can easily replace them for unit testing and instrumentation testing.
2.  The ViewModel/Presenter layer should create its own coroutine to facilitate cancellation
3.  Layers below the ViewModel/Presenter layer should expose suspend functions and Flows
4.  For operations that shouldn't be canceled, create your own scope in the Application class and call the code that doesn't want to be canceled by the ViewModel/Presenter in the coroutine started by it.

### Code detection

1.  [detector](https://github.com/detekt/detekt):Detekt is a static code analysis tool used to detect potential issues and irregular coding practices in Kotlin code. It was developed for the Kotlin language and helps developers discover code snippets that may lead to bugs, performance issues, or reduced code maintainability.
2.  [spotless](https://github.com/diffplug/spotless):Spotless is an open source code formatting and style checking tool that can help development teams maintain consistent code style and format.

### Android version behavior changes:<https://developer.android.com/about/versions?hl=zh-cn>
