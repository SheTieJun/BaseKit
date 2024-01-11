## 适配Android14-34

## 前台服务类型
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