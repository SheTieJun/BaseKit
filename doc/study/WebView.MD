### 一、启动调整模式
在开发过程中，我们可以启用 WebView 的调试模式，以便在 Chrome DevTools 中查看 WebView 的内容、网络请求等信息。
```kotlin
WebView.setWebContentsDebuggingEnabled(true)
```

### 二、WebChromeClient

WebChromeClient 是一个抽象基类，它的实例可以被传递给 WebView.setWebChromeClient() 方法，以处理与 JavaScript 交互和网页元素相关的事件。

**2.1 WebChromeClient一进度相关**
- `onProgressChanged(WebView view, int newProgress)`: 当页面加载进度改变时调用。newProgress参数表示当前页面加载的百分比。

**2.2 WebChromeClient-标题、图标相关**
- `onReceivedTitle(WebView view, String title)`: 当前页面的标题已经被接收到时调用。title参数是新的标题。
- `onReceivedIcon(WebView view, Bitmap icon)`: 当前页面的图标已经被接收到时调用。icon参数是新的图标。
- `onReceivedTouchIconUrl(WebView view, String url, boolean precomposed)`: 当网页的触摸图标URL被接收到时调用。url参数是图标的URL，precomposed参数表示图标是否已经被合成。

**2.3 WebChromeClient-权限相关**
- `onPermissionRequest(PermissionRequest request)`: 当网页请求一个权限时调用，例如摄像头、麦克风等。你可以在这个方法中处理权限请求。
- `onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)`: 当网页请求获取地理位置权限时调用。origin参数是请求权限的网页的源，callback参数是用于设置权限的回调。
```kotlin
     override fun onPermissionRequest(request: PermissionRequest?) {
                    "onPermissionRequest".logD(WEB_TAG)
                    try {
                        request?.let {
                            kotlin.runCatching {
                                if (isVideo(request.resources)) {
                                    startRequestPermissions(permissions = arrayOf(permission.CAMERA, permission.RECORD_AUDIO)) {
                                        if (it.filter { !it.value }.isEmpty()) {
                                            request.grant(request.resources)
                                            request.origin
                                        }
                                    }
                                } else if (isOnlyAudio(request.resources)) {
                                    startRequestPermission(permission = permission.RECORD_AUDIO) {
                                        if (it) {
                                            request.grant(request.resources)
                                            request.origin
                                        }
                                    }
                                }
                            }
                        }
                    }catch (e :Exception){
                        e.message.logE(WEB_TAG)
                    }
                }

                private fun isVideo(resources: Array<String>): Boolean {
                    val strings = listOf(*resources)
                    return strings.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
                }

                private fun isOnlyAudio(resources: Array<String>): Boolean {
                    val strings = listOf(*resources)
                    return !strings.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE) && strings.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                }
            }
```

**2.4 WebChromeClient-文件处理**
- `onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)`: 当网页需要用户选择文件时调用，例如HTML的<input type="file">元素被点击时。你可以在这个方法中打开一个文件选择器，并将用户选择的文件的URI通过filePathCallback返回。

**2.5 WebChromeClient-弹窗、JS相关**
- `onJsAlert(WebView view, String url, String message, JsResult result)`: 当JavaScript的alert()函数被调用时调用。message参数是alert()函数的参数。
- `onJsConfirm(WebView view, String url, String message, JsResult result)`: 当JavaScript的confirm()函数被调用时调用。message参数是confirm()函数的参数。
- `onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result)`: 当JavaScript的prompt()函数被调用时调用。message参数是prompt()函数的第一个参数，defaultValue参数是prompt()函数的第二个参数。
- `onConsoleMessage(ConsoleMessage consoleMessage)`: 当JavaScript的console.log()函数被调用时调用。consoleMessage参数包含了日志消息的详细信息。
- `onJsBeforeUnload(WebView view, String url, String message, JsResult result)`: 当JavaScript的beforeunload事件被触发时调用。message参数是beforeunload事件的返回值。
- `onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg)`: 当JavaScript的window.open()函数被调用时调用。你可以在这个方法中创建一个新的WebView，并将它通过resultMsg返回。
- `onCloseWindow(WebView window)`: 当JavaScript的window.close()函数被调用时调用。你可以在这个方法中关闭之前通过onCreateWindow创建的WebView。

**2.6 WebChromeClient-视频相关**
- `onShowCustomView(View view, CustomViewCallback callback)`: 当网页进入全屏模式时调用。在这种情况下，网页内容将不再在WebView中渲染，而是在传入的view中渲染。你应该将这个View添加到一个配置了WindowManager.LayoutParams.FLAG_FULLSCREEN标志的Window中，以实际全屏显示这个网页内容。
- `onHideCustomView()`: 当网页退出全屏模式时调用。你应该隐藏自定义的View（之前传给onShowCustomView(View view, CustomViewCallback callback)的View）。在这个方法调用后，网页内容将再次在原来的WebView中渲染。
- `getDefaultVideoPoster()`: 当视频元素不在播放状态时，它们由一个'poster'图像表示。可以通过HTML中的video标签的poster属性指定要使用的图像。如果该属性不存在，则使用默认的poster。这个方法允许ChromeClient提供默认的poster图像。
- `getVideoLoadingProgressView()`: 获取在全屏视频缓冲期间显示的View。主应用程序可以覆盖此方法以提供包含旋转器或类似物的View。

### 三、WebViewClient
WebViewClient 是一个抽象基类，它的实例可以被传递给 WebView.setWebViewClient() 方法，以处理与网页加载和渲染相关的事件。

**3.1 WebViewClient-重定向**
- `shouldOverrideUrlLoading(WebView view, String url)`: 当 WebView 即将加载一个 URL 时调用。你可以在这个方法中决定是否要覆盖这个 URL 的加载，如果你想覆盖这个 URL 的加载，那么你应该返回 true，并在这个方法中进行你自己的处理，例如打开一个新的 Activity 来加载这个 URL。

**3.2 WebViewClient-页面加载**
- `onPageStarted(WebView view, String url, Bitmap favicon)`: 当网页开始加载时调用。url 参数是正在加载的网页的 URL。
- `onPageFinished(WebView view, String url)`: 当网页加载完成时调用。url 参数是刚刚加载完成的网页的 URL。
- `onLoadResource(WebView view, String url)`: 当 WebView 正在加载一个资源（例如图片或者 JavaScript 文件）时调用。url 参数是正在加载的资源的 URL。

**3.3 WebViewClient-认证请求相关**
- `onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm)`: 当 WebView 需要进行 HTTP 认证时调用。你可以在这个方法中处理认证请求，例如显示一个输入用户名和密码的对话框。
- `onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args)`: 当 WebView 需要进行自动登录时调用。你可以在这个方法中处理登录请求，例如从存储的账户信息中获取用户名和密码。

**3.4 WebViewClient-其他**
- `onReceivedError(WebView view, int errorCode, String description, String failingUrl)`: 当 WebView 加载网页时发生错误时调用。errorCode 参数是错误码，description 参数是错误描述，failingUrl 参数是发生错误的网页的 URL。
- `onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse)`: 当 WebView 接收到 HTTP 错误时调用。request 参数是发生错误的请求，errorResponse 参数是服务器的响应。
- `onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)`: 当 WebView 加载的网页有 SSL 错误时调用。你可以在这个方法中处理 SSL 错误，例如显示一个对话框让用户决定是否要继续加载。
- `onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback)`: 当 WebView 访问一个被 Safe Browsing 判断为可能是恶意的网站时被调用。你可以在这个方法中处理这个事件，例如显示一个警告对话框，或者导航到一个安全的网页。callback 参数是一个 SafeBrowsingResponse，你可以调用它的 showInterstitial(boolean) 或 proceed(boolean) 方法来决定是否显示一个安全警告的插页广告，或者继续加载这个网页。
- `onFormResubmission(WebView view, Message dontResend, Message resend)`: 当 WebView 尝试重新提交一个表单，并且需要用户确认是否重新提交时调用。你可以在这个方法中处理这个事件，例如显示一个确认对话框。
- `onScaleChanged(WebView view, float oldScale, float newScale)`: 当 WebView 的缩放级别改变时调用。你可以在这个方法中处理缩放级别的改变，例如更新一个缩放级别的显示。
- `onUnhandledKeyEvent(WebView view, KeyEvent event)`: 当 WebView 收到一个未处理的按键事件时调用。你可以在这个方法中处理未处理的按键事件，例如实现自定义的按键处理。
- `doUpdateVisitedHistory(WebView view, String url, boolean isReload)`: 一个页面的访问历史记录被更新时，这个方法会被调用。

### 四、WebSettings
`WebSettings` 是一个类，它用于管理 WebView 的各种设置。你可以通过 `WebView.getSettings()` 方法获取到一个 `WebSettings` 实例，然后通过这个实例来配置 WebView 的设置。

以下是一些常用的 `WebSettings` 方法：
- `setJavaScriptEnabled(boolean enabled)`: 设置 WebView 是否支持 JavaScript。默认值为 `false`。
- `setSupportZoom(boolean support)`: 设置 WebView 是否支持缩放。默认值为 `false`。
- `setDisplayZoomControls(boolean enabled)`: 设置 WebView 是否显示缩放控件。默认值为 `true`。
- `setBuiltInZoomControls(boolean enabled)`: 设置 WebView 是否使用内置的缩放机制。默认值为 `false`。
- `setLoadWithOverviewMode(boolean overview)`: 设置 WebView 是否应该启用概览模式，即总是缩放内容以适应屏幕宽度。默认值为 `false`。
- `setUseWideViewPort(boolean use)`: 设置 WebView 是否应该启用宽视图端口。默认值为 `false`。
- `setJavaScriptCanOpenWindowsAutomatically(boolean allow)`: 设置 WebView 的 JavaScript 是否可以自动打开窗口。默认值为 `false`。
- `setMediaPlaybackRequiresUserGesture(boolean require)`: 设置为 false，那么 WebView 中的音频和视频将会自动播放，不需要用户交互。
- `setLoadsImagesAutomatically(boolean flag)`:  用于设置 WebView 是否自动加载图片。  如果设置为 true，WebView 会自动加载网页中的图片。如果设置为 false，所有的图片都不会被加载，只有当 LOAD_CACHE_ELSE_NETWORK 或 LOAD_NO_CACHE 被使用时，才会加载。

`WebSettings` 提供了一些方法来管理 WebView 的缓存：
1. `setCacheMode(int mode)`: 设置 WebView 的缓存模式。可选的值有：
   - `LOAD_DEFAULT`: 默认的缓存模式。如果没有 `Cache-Control` 或 `Expires` 头，缓存会被存储，当资源过期时，WebView 会尝试从网络加载。如果没有网络，WebView 会从缓存加载。
   - `LOAD_CACHE_ELSE_NETWORK`: 只要缓存存在，即使过期也会从缓存加载。如果缓存不存在，WebView 会从网络加载。
   - `LOAD_NO_CACHE`: 不使用缓存，WebView 会从网络加载。
   - `LOAD_CACHE_ONLY`: 不从网络加载，只从缓存加载。
2. `setAppCacheEnabled(boolean enabled)`: 设置 WebView 是否启用应用缓存。默认值为 `false`。注意，你还需要通过 `setAppCachePath` 方法设置一个应用缓存的路径。
3. `setAppCachePath(String appCachePath)`: 设置应用缓存的路径。这个路径必须是可以让应用读写的。
4. `setAppCacheMaxSize(long appCacheMaxSize)`: 设置应用缓存的最大大小。
5. `setDatabaseEnabled(boolean enabled)`: 设置 WebView 是否启用数据库存储 API。默认值为 `false`。
6. `setDomStorageEnabled(boolean enabled)`: 设置 WebView 是否启用 DOM 存储 API。默认值为 `false`，`true`可以使用 sessionStorage 和 localStorage 对象来存储和检索数据。。

```Kotlin
  //设置Cookie
  fun setCookie(map: MutableMap<String, String>) {
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        map.onEach { entry ->
            cookieManager.setCookie(entry.key, entry.value)
        }
        cookieManager.flush()
    }
    /**
     * 给设置localStorage 设置数据
     */
    fun setLocalStorage(itmes: Map<String, String>) {
        val jsonBuf = StringBuilder()
        for (key in itmes.keys) {
            if (isNotEmpty(itmes[key])) {
                jsonBuf.append("localStorage.setItem('key', '")
                    .append(itmes[key])
                    .append("');")
            }
        }
        val info = jsonBuf.toString()
        if (isNotEmpty(info)) {
            webView.evaluateJavascript(info, null)
        }
    }
```

### 五、WebView和Native交互
WebView 和 Native 交互主要有两种方式：JavaScriptInterface 和 WebView.evaluateJavascript。

1. **JavaScriptInterface**：这是一种将 Java 对象映射到 JavaScript 的方式。你可以创建一个 Java 对象，这个对象的公共方法可以在 JavaScript 中被调用。例如：

```kotlin
class JavaScriptInterface(private val context: Context) {
    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

val webView: WebView = findViewById(R.id.webview)
webView.addJavascriptInterface(JavaScriptInterface(this), "Android")
```

在上述代码中，我们创建了一个 `JavaScriptInterface` 类，并将其实例添加到了 WebView 中。在 JavaScript 中，我们可以通过 `Android.showToast(message)` 来调用 `showToast` 方法。

2. **WebView.evaluateJavascript**：这是一种在 WebView 中执行 JavaScript 代码的方式。你可以使用这个方法来调用 JavaScript 函数，并获取返回值。例如：

```kotlin
webView.evaluateJavascript("document.title") { title ->
    Log.d("WebView", "Document title: $title")
}
```
### 六、WebKIt
[WebView-Androidx-WebKit](WebView-Androidx-WebKit.MD)