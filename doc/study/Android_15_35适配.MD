## Android_15_35适配

### 应用默认进行全屏布局（edge-to-edge）配置
对于 targetSDK >= 35 的应用，运行在 Android 15+ 设备上时将会默认进行全屏布局（edge-to-edge）配置。

即使应用的 targetSDKVersion 达到了35， 您仍可在应用的 theme.xml 文件中通过配置 windowOptOutEdgeToEdgeEnforcement 属性来规避强制全屏布局（edge-to-edge）特性的生效。

默认情况下，windowOptOutEdgeToEdgeEnforcement 值为 false。如果将此值设置为 true，则强制全屏布局（edge-to-edge）特性不会生效。然而，需要注意的是，这个属性将在未来的SDK版本中被弃用和禁用，此规避方式也将失效。
```xml
<resources>
	<style>
        <item name="android:windowOptOutEdgeToEdgeEnforcement">true</item>
    </style>
</resources>
```


### 以下是已弃用和禁用的API列表：
``` 
R.attr#statusBarColor
R.attr#navigationBarColor
R.attr#navigationBarDividerColor
Window#setDecorFitsSystemWindows
Window#setStatusBarColor
Window#setStatusBarContrastEnforced
Window#setNavigationBarColor
Window#setNavigationBarDividerColor
Window#getStatusBarColor
Window#getStatusBarContrastEnforced
Window#getNavigationBarColor
Window#getNavigationBarDividerColor
```

### elegantTextHeight 属性默认为 true

### 请求音频焦点的限制
对于TargetSDKVersion >= 35的应用，必须是顶层应用或运行一个与音频相关的前台服务，才能请求音频焦点。如果一个应用在不满足这些要求之一的情况下尝试请求焦点，调用将返回 AUDIOFOCUS_REQUEST_FAILED。

如果前台服务的类型是 mediaPlayback、camera、microphone 或 phoneCall，则被认为是与音频相关的。
 
### 屏幕录制检测
您可使用 WindowManager 类的 addScreenRecordingCallback 方法进行屏幕录制检测。
```java
public int addScreenRecordingCallback (Executor executor,
Consumer<Integer> callback)
```

使用该API需要 Manifest.permission.DETECT_SCREEN_RECORDING 权限。


### PDFRender改进
Android 15 包含了对 PdfRenderer API 的重大改进。