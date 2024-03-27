## 适配Android14-34

## 前台服务类型是必需的
如果您的应用以 Android 14 为目标平台，则必须指定适当的前台服务类型。与以前的 Android 版本一样，可组合使用多个类型。下面列出了可供选择的前台服务类型：
- camera
- connectedDevice
- dataSync
- health
- location
- mediaPlayback
- mediaProjection
- microphone
- phoneCall
- remoteMessaging
- shortService
- specialUse
- systemExempted

```
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
    <application ...>
      <service
          android:name=".MyMediaPlaybackService"
          android:foregroundServiceType="mediaPlayback"
          android:exported="false">
      </service>
    </application>
```

## 监听截图
```kotlin
android.permission.DETECT_SCREEN_CAPTURE
```

## 授予对照片和视频的部分访问权限
```kotlin
// Register ActivityResult handler
val requestPermissions = registerForActivityResult(RequestMultiplePermissions()) { results ->
    // Handle permission requests results
    // See the permission example in the Android platform samples: https://github.com/android/platform-samples
}

// Permission request logic
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
    requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
} else {
    requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
}
```

## 用户发起的数据传输作业的权限
```xml
    <uses-permission android:name="android.permission.RUN_USER_INITIATED_JOBS" />
```
```xml
  android:permission="android.permission.BIND_JOB_SERVICE"
```