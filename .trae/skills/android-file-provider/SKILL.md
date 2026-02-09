---
name: "android-file-provider"
description: "Best practices for configuring Android FileProvider. Invoke when setting up file sharing, fixing exposed file uri exceptions, or configuring file_paths.xml."
---

# Android FileProvider Configuration Guide

This skill provides a secure and comprehensive configuration guide for Android `FileProvider`, ensuring compliance with Android 7.0+ (API 24+) strict file sharing policies and Android 10+ (API 29+) Scoped Storage requirements.

## 🚀 Core Principles

1.  **Least Privilege**: Only expose necessary subdirectories, avoid exposing the entire root (`.`).
2.  **Explicit Mapping**: Map specific paths (e.g., `Download/`, `Pictures/`) instead of broad wildcards.
3.  **Scoped Storage**: Respect Android 10+ storage isolation; prefer `Context.getExternalFilesDir()` over raw SD card paths.

## 📁 Recommended `res/xml/file_paths.xml`

Use this structure to balance security and compatibility.

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <!-- ========================================================= -->
    <!-- Internal Storage (Private & Secure) -->
    <!-- Cleared on app uninstall. Best for sensitive files. -->
    <!-- ========================================================= -->

    <!-- Context.getFilesDir() -->
    <!-- Recommended: Use a subfolder for sharing -->
    <files-path name="internal_shared" path="shared/" />
    <!-- Compatibility: Root of files dir (Use with caution) -->
    <files-path name="internal_root" path="." />

    <!-- Context.getCacheDir() -->
    <cache-path name="internal_cache" path="." />

    <!-- ========================================================= -->
    <!-- External Storage (App Private) -->
    <!-- Cleared on app uninstall. -->
    <!-- ========================================================= -->

    <!-- Context.getExternalFilesDir(null) -->
    <external-files-path name="external_files" path="." />
    
    <!-- Context.getExternalCacheDir() -->
    <external-cache-path name="external_cache" path="." />

    <!-- Context.getExternalMediaDirs() -->
    <external-media-path name="external_media" path="." />

    <!-- ========================================================= -->
    <!-- External Storage (Public Shared) -->
    <!-- Persists after uninstall. Restricted on Android 10+. -->
    <!-- ========================================================= -->

    <!-- Environment.getExternalStorageDirectory() -->
    <!-- WARNING: Exposing root is dangerous. Map specific folders only. -->
    
    <external-path name="external_download" path="Download/" />
    <external-path name="external_pictures" path="Pictures/" />
    <external-path name="external_dcim" path="DCIM/" />
    <external-path name="external_documents" path="Documents/" />
    
    <!-- Fallback: Expose root (Not recommended, use only if necessary) -->
    <external-path name="external_root" path="." />
</paths>
```

## ⚙️ Manifest Configuration

Ensure your `AndroidManifest.xml` correctly references the XML file.

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

## 💻 Usage Examples (Kotlin)

### 1. Sharing a File (Common Scenario)

This is the standard way to share a file (e.g., PDF, Image) with another app.

```kotlin
fun shareFile(context: Context) {
    // 1. Prepare file in a shared path
    val sharedDir = File(context.filesDir, "shared")
    if (!sharedDir.exists()) sharedDir.mkdirs()
    val file = File(sharedDir, "report.pdf")

    // 2. Get URI using FileProvider
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    // 3. Share URI with Intent
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share File"))
}
```

### 2. Installing an APK (Android 7.0+)

Installing an APK requires `REQUEST_INSTALL_PACKAGES` permission and FileProvider.

```kotlin
fun installApk(context: Context, apkFile: File) {
    val intent = Intent(Intent.ACTION_VIEW)
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        apkFile
    )
    
    intent.setDataAndType(uri, "application/vnd.android.package-archive")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    
    context.startActivity(intent)
}
```

### 3. Taking a Photo (Camera Intent)

Pass a FileProvider URI to the camera app to save the captured image.

```kotlin
fun dispatchTakePictureIntent(activity: Activity, photoFile: File, requestCode: Int) {
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    
    val photoURI: Uri = FileProvider.getUriForFile(
        activity,
        "${activity.packageName}.fileprovider",
        photoFile
    )
    
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    // Grant write permission to the camera app
    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) 
    
    if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
        activity.startActivityForResult(takePictureIntent, requestCode)
    }
}
```

## ⚠️ Common Pitfalls

1.  **`IllegalArgumentException: Failed to find configured root`**:
    *   **Cause**: The file you are trying to share is located in a path not declared in `file_paths.xml`.
    *   **Fix**: Ensure the file's parent directory matches one of the `<*-path>` entries.
2.  **`SecurityException: Permission Denial`**:
    *   **Cause**: Forgetting `FLAG_GRANT_READ_URI_PERMISSION`.
    *   **Fix**: Always add this flag to your Intent.
3.  **Root Path Exposure**:
    *   **Risk**: `<external-path path="."/>` allows access to any file on the SD card if the attacker guesses the path.
    *   **Fix**: Use specific subdirectories (e.g., `Download/`) as shown above.
