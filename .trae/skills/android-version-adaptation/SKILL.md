---
name: "android-version-adaptation"
description: "Expert guide for adapting Android apps to new Android versions (8.0-15.0). Invoke when updating targetSDK, fixing crashes on new Android versions, or handling permission changes."
---

# Android Version Adaptation Guide

This skill provides a comprehensive checklist and solution guide for adapting Android applications to newer Android versions, ranging from Android 8.0 (Oreo) to Android 15.

## 🚀 Adaptation Strategy
1.  **Update `targetSdkVersion`**: Update incrementally and test.
2.  **Review Changes**: Check behavior changes for the specific version.
3.  **Test**: Test on emulators or real devices running the target version.

## 📱 Version-Specific Changes

### Android 15 (API 35)
-   **Edge-to-Edge**: Enforced by default for targetSDK 35.
    -   *Opt-out (Temporary)*: `<item name="android:windowOptOutEdgeToEdgeEnforcement">true</item>` in theme.
    -   *Deprecated*: `window.statusBarColor`, `window.navigationBarColor`.
-   **Audio Focus**: Restricted for background apps unless they are top-level or running a specific foreground service.
-   **Screen Recording**: New callback `WindowManager.addScreenRecordingCallback` (Requires `DETECT_SCREEN_RECORDING`).
-   **PDFRenderer**: Major improvements in API.

### Android 14 (API 34)
-   **Foreground Services**: Must specify `android:foregroundServiceType` in Manifest.
    -   Types: `camera`, `location`, `mediaPlayback`, `dataSync`, `microphone`, `phoneCall`, etc.
    -   Example: `<service android:name=".MyService" android:foregroundServiceType="mediaPlayback" />`
-   **Media Permissions**: Partial access support via `READ_MEDIA_VISUAL_USER_SELECTED`.
-   **Screen Capture**: New permission `DETECT_SCREEN_CAPTURE` for screenshot listening.
-   **JobScheduler**: `RUN_USER_INITIATED_JOBS` permission required for user-initiated data transfers.

### Android 13 (API 33)
-   **Notifications**: Runtime permission `POST_NOTIFICATIONS` is required to post notifications.
-   **Media Permissions**: Granular permissions replace `READ_EXTERNAL_STORAGE`:
    -   `READ_MEDIA_IMAGES`
    -   `READ_MEDIA_VIDEO`
    -   `READ_MEDIA_AUDIO`
-   **Wifi**: `NEARBY_WIFI_DEVICES` permission for local Wi-Fi scanning (no location needed).

### Android 12 (API 31/32)
-   **PendingIntent**: Must specify `PendingIntent.FLAG_IMMUTABLE` or `PendingIntent.FLAG_MUTABLE`.
-   **Exported Components**: Must explicitly set `android:exported="true"` or `"false"` for activities/services/receivers with intent filters.
-   **Bluetooth**: New runtime permissions `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_ADVERTISE`.

### Android 11 (API 30)
-   **Package Visibility**: Apps can no longer see all installed packages. Use `<queries>` in Manifest to declare packages you interact with.
-   **Scoped Storage**: Forced enforcement. `WRITE_EXTERNAL_STORAGE` is deprecated.
    -   Use `MANAGE_EXTERNAL_STORAGE` for file manager apps (requires Google Play review).
-   **Toast**: Custom toasts from background blocked.
-   **Status Bar**: Use `WindowInsets` to get height instead of legacy methods.

### Android 10 (API 29)
-   **Scoped Storage**: Introduced. Use `requestLegacyExternalStorage="true"` in Manifest for temporary opt-out.
-   **Location**: `ACCESS_BACKGROUND_LOCATION` required for accessing location while app is in background.
-   **IMEI/Serial**: No longer accessible to non-system apps. Use Android ID or GUID.

### Android 9.0 (API 28)
-   **Network**: Cleartext (HTTP) disabled by default.
    -   *Fix*: Add `android:usesCleartextTraffic="true"` in Manifest or configure `network_security_config.xml`.
-   **Foreground Service**: Requires `FOREGROUND_SERVICE` permission (normal permission, auto-granted).
-   **Apache HTTP**: Library removed. Add `<uses-library android:name="org.apache.http.legacy" android:required="false"/>` to Manifest.
-   **Notch Support**: Use `WindowInsets` or `DisplayCutout` to handle notches.

### Android 8.0 (API 26)
-   **Install APK**: Requires `REQUEST_INSTALL_PACKAGES` permission and user authorization.
-   **Notifications**: Notification Channels are mandatory. All notifications must be assigned to a channel.
-   **Background Execution**: Strict limits on background services and implicit broadcasts.
-   **Translucent Activity**: Crash if fixed orientation (e.g., `screenOrientation="portrait"`) is set on a translucent activity. Fix: remove orientation or translucency.

## 🛠 Common Solutions

### FileProvider Configuration (Android 7.0+)
Required for sharing files (installing APKs, taking photos).
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

### Network Security Config (Allow HTTP)
`res/xml/network_security_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

### Checking Notification Permission (Android 13+)
```kotlin
if (Build.VERSION.SDK_INT >= 33) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
    }
}
```
