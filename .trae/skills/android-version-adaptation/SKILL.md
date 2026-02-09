---
name: "android-version-adaptation"
description: "Expert guide for adapting Android apps to new Android versions (8.0-15.0). Invoke when updating targetSDK, fixing crashes on new Android versions, or handling permission changes."
---

# Android Version Adaptation Guide

This skill provides a comprehensive checklist and solution guide for adapting Android applications to newer Android versions, ranging from Android 8.0 (Oreo) to Android 15 (Vanilla Ice Cream).

## 🚀 Adaptation Strategy
1.  **Update `compileSdkVersion`**: Always compile with the latest SDK to access new APIs.
2.  **Update `targetSdkVersion` Incrementally**: Update one version at a time (e.g., 30 -> 31 -> 32) and test thoroughly.
3.  **Review Changes**: Check "Behavior changes: all apps" and "Behavior changes: apps targeting Android X" for the specific version.
4.  **Test**: Test on emulators or real devices running the target version.

---

## 📱 Version-Specific Changes

### Android 15 (API 35) - Vanilla Ice Cream
*Ref: [Official Android 15 Migration Guide](https://developer.android.com/about/versions/15)*

-   **Edge-to-Edge Enforcement**:
    -   **Change**: Apps targeting SDK 35 are displayed edge-to-edge by default. Status bar and navigation bar backgrounds are transparent.
    -   **Action**: Use `WindowInsets` to handle overlaps.
    -   **Opt-out**: Set `<item name="android:windowOptOutEdgeToEdgeEnforcement">true</item>` in your theme (Temporary).
    -   **Deprecated**: `window.statusBarColor` and `window.navigationBarColor` are ignored.
-   **Foreground Service Timeouts**:
    -   **Change**: `dataSync` and `mediaProcessing` foreground services have a roughly 6-hour execution limit.
-   **Screen Recording Detection**:
    -   **New API**: Apps can detect if they are being recorded using `WindowManager.addScreenRecordingCallback`.
-   **Package Visibility**:
    -   **Change**: Stopped apps are no longer visible to other apps, even if they have `QUERY_ALL_PACKAGES` permission.

### Android 14 (API 34) - Upside Down Cake
*Ref: [Official Android 14 Migration Guide](https://developer.android.com/about/versions/14)*

-   **Foreground Service Types (Mandatory)**:
    -   **Change**: Must specify `android:foregroundServiceType` in Manifest for all services calling `startForeground()`.
    -   **Types**: `camera`, `location`, `mediaPlayback`, `dataSync`, `microphone`, `phoneCall`, `connectedDevice`, etc.
    -   **Action**: `<service android:name=".MyService" android:foregroundServiceType="mediaPlayback" />`
-   **Exact Alarms Denied by Default**:
    -   **Change**: `SCHEDULE_EXACT_ALARM` permission is no longer pre-granted to most apps.
    -   **Action**: Use `USE_EXACT_ALARM` (if eligible) or prompt user to grant permission.
-   **Selected Photos Access**:
    -   **Change**: User can grant access to only *selected* photos.
    -   **Action**: Handle `READ_MEDIA_VISUAL_USER_SELECTED` permission.
-   **Implicit Intents**:
    -   **Change**: Implicit intents are restricted for internal app components. Exported components must be explicit.

### Android 13 (API 33) - Tiramisu
*Ref: [Official Android 13 Migration Guide](https://developer.android.com/about/versions/13)*

-   **Notification Permission**:
    -   **Change**: Runtime permission `POST_NOTIFICATIONS` is required to post notifications.
    -   **Action**: Request permission at runtime.
-   **Granular Media Permissions**:
    -   **Change**: `READ_EXTERNAL_STORAGE` is deprecated. Use granular permissions:
        -   `READ_MEDIA_IMAGES`
        -   `READ_MEDIA_VIDEO`
        -   `READ_MEDIA_AUDIO`
-   **Nearby Wi-Fi Devices**:
    -   **Change**: `NEARBY_WIFI_DEVICES` permission for local Wi-Fi scanning. No longer requires Location permission for this use case.
-   **Photo Picker**:
    -   **New API**: System UI for picking photos without requiring full storage permissions.

### Android 12 (API 31/32) - Snow Cone
*Ref: [Official Android 12 Migration Guide](https://developer.android.com/about/versions/12)*

-   **Splash Screen**:
    -   **Change**: System enforces a default splash screen.
    -   **Action**: Use `androidx.core:core-splashscreen` library to customize it.
-   **PendingIntent Mutability**:
    -   **Change**: Must specify `PendingIntent.FLAG_IMMUTABLE` or `PendingIntent.FLAG_MUTABLE`.
-   **Exported Components**:
    -   **Change**: Must explicitly set `android:exported="true"` or `"false"` for activities/services/receivers with intent filters.
-   **Bluetooth Permissions**:
    -   **Change**: New runtime permissions `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_ADVERTISE`. Legacy `BLUETOOTH` and `BLUETOOTH_ADMIN` are deprecated.

### Android 11 (API 30) - Red Velvet Cake
*Ref: [Official Android 11 Migration Guide](https://developer.android.com/about/versions/11)*

-   **Package Visibility**:
    -   **Change**: Apps can no longer see all installed packages.
    -   **Action**: Use `<queries>` in Manifest to declare packages you interact with.
-   **Scoped Storage (Enforced)**:
    -   **Change**: `WRITE_EXTERNAL_STORAGE` is deprecated and has no effect on API 30+.
    -   **Action**: Use `MediaStore` or `Storage Access Framework` (SAF). For file managers, request `MANAGE_EXTERNAL_STORAGE`.
-   **Toast**:
    -   **Change**: Custom toasts from background are blocked.
-   **Phone Number Access**:
    -   **Change**: `READ_PHONE_STATE` no longer grants access to phone numbers. Use `READ_PHONE_NUMBERS`.

### Android 10 (API 29) - Quince Tart
-   **Scoped Storage (Introduced)**:
    -   **Action**: Can opt-out using `requestLegacyExternalStorage="true"` (Only works up to API 29).
-   **Background Location**:
    -   **Change**: `ACCESS_BACKGROUND_LOCATION` required for accessing location while app is in background.
-   **Device Identifiers**:
    -   **Change**: IMEI/Serial no longer accessible to non-system apps. Use Android ID or GUID.

### Android 9.0 (API 28) - Pie
-   **Cleartext Traffic (HTTP)**:
    -   **Change**: HTTP is disabled by default.
    -   **Action**: Add `android:usesCleartextTraffic="true"` in Manifest or configure `network_security_config.xml`.
-   **Foreground Service Permission**:
    -   **Change**: Requires `FOREGROUND_SERVICE` permission (normal permission, auto-granted).

### Android 8.0 (API 26) - Oreo
-   **Notification Channels**:
    -   **Change**: Mandatory. All notifications must be assigned to a channel.
-   **Background Execution Limits**:
    -   **Change**: Services cannot run in background when app is idle. Use `JobScheduler` or `WorkManager`.
-   **APK Installation**:
    -   **Change**: Requires `REQUEST_INSTALL_PACKAGES` permission.

---

## 🛠 Common Solutions & Code Snippets

### 1. Notification Permission Check (Android 13+)
```kotlin
fun checkNotificationPermission(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }
    }
}
```

### 2. Creating Notification Channel (Android 8.0+)
```kotlin
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "My Channel"
        val descriptionText = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
```

### 3. FileProvider Configuration (Android 7.0+)
Required for sharing files (installing APKs, taking photos).

**AndroidManifest.xml**:
```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

**res/xml/file_paths.xml**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="external_files" path="."/>
    <cache-path name="cache_files" path="."/>
</paths>
```

### 4. Network Security Config (Allow HTTP)
**AndroidManifest.xml**:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

**res/xml/network_security_config.xml**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- Allow Cleartext Traffic -->
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    
    <!-- Allow User Certificates for Debugging (Charles/Fiddler) -->
    <debug-overrides>
        <trust-anchors>
            <certificates src="user" />
            <certificates src="system" />
        </trust-anchors>
    </debug-overrides>
</network-security-config>
```

### 5. Package Visibility (Android 11+)
**AndroidManifest.xml**:
```xml
<manifest package="com.example.app">
    <queries>
        <!-- Specific apps -->
        <package android:name="com.whatsapp" />
        <package android:name="com.facebook.katana" />
        
        <!-- Intent signatures -->
        <intent>
            <action android:name="android.intent.action.SEND" />
            <data android:mimeType="image/jpeg" />
        </intent>
    </queries>
    ...
</manifest>
```
