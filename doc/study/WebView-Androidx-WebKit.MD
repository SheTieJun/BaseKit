# WebView-Androidx-WebKit

这里是WebKit对于的 WebView 的使用方法

## 启动安全浏览服务
```kotlin
    /**
         * Start safe browsing
         * 用于启动安全浏览服务。这个服务可以帮助 WebView 防止用户访问被认为是恶意的网站
         */
        fun startSafeBrowsing(){
            if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
                WebViewCompat.startSafeBrowsing(BaseKit.app) {
                    ("WebView.startSafeBrowsing isSuccess = $it").logI()
                }
            }
        }
```

## 设置代理
```kotlin

if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
    ProxyConfig proxyConfig = new ProxyConfig.Builder()
            .addProxyRule("localhost:7890") //添加要用于所有 URL 的代理
            .addProxyRule("localhost:1080") //优先级低于第一个代理，仅在上一个失败时应用
            .addDirect()                    //当前面的代理失败时，不使用代理直连
            .addBypassRule("www.baidu.com") //该网址不使用代理，直连服务
            .addBypassRule("*.cn")          //以.cn结尾的网址不使用代理
            .build();
    Executor executor = ...
    Runnable listener = ...
    ProxyController.getInstance().setProxyOverride(proxyConfig, executor, listener);
｝
```

## 安全的 WebView 和 Native 通信支持
```Kotlin
// App  
    val myListener = object : WebViewCompat.WebMessageListener {

            /**
             * On post message
             *
             * @param view WebView
             * @param message js代码发送的消息
             * @param sourceOrigin 发送消息的网页地址
             * @param isMainFrame 是否是主页面，iFrame中的页面为false
             * @param replyProxy 回复消息的代理
             */
            override fun onPostMessage(view: WebView, message: WebMessageCompat, sourceOrigin: Uri, isMainFrame: Boolean, replyProxy: JavaScriptReplyProxy) {
            // do something about view, message, sourceOrigin and isMainFrame.
                }
    }
    val allowedOriginRules = allowedRules ?: setOf()
        WebViewCompat.addWebMessageListener(
        /* webView = */ webView,
        /* jsObjectName = */jsObjectName,
        /* allowedOriginRules = */ allowedOriginRules,
        /* listener = */myListener
    )
```
```Java
// Web page (in JavaScript)
myObject.onmessage = function(event) {
  // prints "Got it!" when we receive the app's response.
  console.log(event.data);
}
myObject.postMessage("I'm ready!");
```
### 1. 文件传递
#### 1.1 Native 传递文件给 WebView:
```Java

// App (in Java)
WebMessageListener myListener = new WebMessageListener() {
  @Override
  public void onPostMessage(WebView view, WebMessageCompat message, Uri sourceOrigin,
           boolean isMainFrame, JavaScriptReplyProxy replyProxy) {
    // Communication is setup, send file data to web.
    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_ARRAY_BUFFER)) {
      // Suppose readFileData method is to read content from file.
      byte[] fileData = readFileData("myFile.dat");
      replyProxy.postMessage(fileData);
    }
  }
}
```
```Javascript
// Web page (in JavaScript)
myObject.onmessage = function(event) {
  if (event.data instanceof ArrayBuffer) {
    const data = event.data;  // Received file content from app.
    const dataView = new DataView(data);
    // Consume file content by using JavaScript DataView to access ArrayBuffer.
  }
}
myObject.postMessage("Setup!");
```
#### 1.2 WebView 传递文件给 Native:
```Javascript

// Web page (in JavaScript)
const response = await fetch('example.jpg');
if (response.ok) {
    const imageData = await response.arrayBuffer();
    myObject.postMessage(imageData);
}
```
```Java

// App (in Java)
WebMessageListener myListener = new WebMessageListener() {
  @Override
  public void onPostMessage(WebView view, WebMessageCompat message, Uri sourceOrigin,
           boolean isMainFrame, JavaScriptReplyProxy replyProxy) {
    if (message.getType() == WebMessageCompat.TYPE_ARRAY_BUFFER) {
      byte[] imageData = message.getArrayBuffer();
      // do something like draw image on ImageView.
    }
  }
};
```
## 深色主题的支持
简单来说如果您想让 WebView 的内容和应用的主题相匹配，您应该始终定义深色主题并实现 prefers-color-scheme，而对于未定义 prefers-color-scheme 的页面，系统按照不同的策略选择算法生成或者显示默认页面。

## JavaScript and WebAssembly执行引擎支持
JavascriptEngine 直接使用了 WebView 的 V8 实现，由于不用分配其他 WebView 资源所以资源消耗更低，并可以开启多个独立运行的沙箱环境，还针对传递大量数据做了优化。
```java

if(!JavaScriptSandbox.isSupported()){
return;
}
//连接到引擎
ListenableFuture<JavaScriptSandbox> jsSandboxFuture =
               JavaScriptSandbox.createConnectedInstanceAsync(context);
//创建上下文 上下文间有简单的数据隔离
JavaScriptIsolate jsIsolate = jsSandbox.createIsolate();
//执行函数 && 获取结果
final String code = "function sum(a, b) { let r = a + b; return r.toString(); }; sum(3, 4)";
ListenableFuture<String> resultFuture = jsIsolate.evaluateJavaScriptAsync(code);
String result = resultFuture.get(5, TimeUnit.SECONDS);
Futures.addCallback(resultFuture,
       new FutureCallback<String>() {
           @Override
           public void onSuccess(String result) {
               text.append(result);
           }
           @Override
           public void onFailure(Throwable t) {
               text.append(t.getMessage());
           }
       },
       mainThreadExecutor); //Wasm运行
final byte[] hello_world_wasm = {
   0x00 ,0x61 ,0x73 ,0x6d ,0x01 ,0x00 ,0x00 ,0x00 ,0x01 ,0x0a ,0x02 ,0x60 ,0x02 ,0x7f ,0x7f ,0x01,
   0x7f ,0x60 ,0x00 ,0x00 ,0x03 ,0x03 ,0x02 ,0x00 ,0x01 ,0x04 ,0x04 ,0x01 ,0x70 ,0x00 ,0x01 ,0x05,
   0x03 ,0x01 ,0x00 ,0x00 ,0x06 ,0x06 ,0x01 ,0x7f ,0x00 ,0x41 ,0x08 ,0x0b ,0x07 ,0x18 ,0x03 ,0x06,
   0x6d ,0x65 ,0x6d ,0x6f ,0x72 ,0x79 ,0x02 ,0x00 ,0x05 ,0x74 ,0x61 ,0x62 ,0x6c ,0x65 ,0x01 ,0x00,
   0x03 ,0x61 ,0x64 ,0x64 ,0x00 ,0x00 ,0x09 ,0x07 ,0x01 ,0x00 ,0x41 ,0x00 ,0x0b ,0x01 ,0x01 ,0x0a,
   0x0c ,0x02 ,0x07 ,0x00 ,0x20 ,0x00 ,0x20 ,0x01 ,0x6a ,0x0b ,0x02 ,0x00 ,0x0b,
};
final String jsCode = "android.consumeNamedDataAsArrayBuffer('wasm-1').then(" +
       "(value) => { return WebAssembly.compile(value).then(" +
       "(module) => { return new WebAssembly.Instance(module).exports.add(20, 22).toString(); }" +
       ")})";
boolean success = js.provideNamedData("wasm-1", hello_world_wasm);
if (success) {
    FluentFuture.from(js.evaluateJavaScriptAsync(jsCode))
           .transform(this::println, mainThreadExecutor)
           .catching(Throwable.class, e -> println(e.getMessage()), mainThreadExecutor);
} else {
   // the data chunk name has been used before, use a different name
}
```