---
name: "android-gradle-troubleshooting"
description: "Solutions for common Android Gradle build errors. Invoke when encountering build failures, duplicate file conflicts, manifest merger issues, or dependency resolution errors."
---

# Android Gradle Troubleshooting Guide

This skill provides solutions for common build errors encountered in Android projects using Gradle.

## 1. Duplicate Files Conflict (Packaging Options)

**Error Pattern**:
- `More than one file was found with OS independent path 'META-INF/...'`
- `2 files found with path 'META-INF/versions/9/OSGI-INF/MANIFEST.MF'`

**Cause**:
Multiple dependencies include the same file (often license files, generic manifests, or module metadata) in their JARs/AARs. Gradle doesn't know which one to include in the final APK.

**Solution**:
Configure the `packaging` block in your module's `build.gradle.kts` (usually `app/build.gradle.kts`) to exclude, pick first, or merge the conflicting files.

```kotlin
android {
    packaging {
        resources {
            // Option 1: Exclude the file (Recommended for metadata/licenses)
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "META-INF/*.kotlin_module"
            
            // Option 2: Pick the first occurrence (If content is identical or one is sufficient)
            pickFirsts += "META-INF/DEPENDENCIES"
            pickFirsts += "META-INF/LICENSE"
            
            // Option 3: Merge files (For service loaders or configuration files)
            merges += "META-INF/services/javax.annotation.processing.Processor"
        }
    }
}
```

## 2. Manifest Merge Conflicts

**Error Pattern**:
- `Manifest merger failed : Attribute application@appComponentFactory value=(...)`
- `Attribute application@allowBackup value=(true) from AndroidManifest.xml:10:9 is also present at ... value=(false)`

**Cause**:
Your app's `AndroidManifest.xml` defines an attribute value that conflicts with a value defined in a library dependency's manifest.

**Solution**:
Use the `tools:replace` attribute in the element where the conflict occurs to explicitly tell the merger which value to keep.

```xml
<manifest xmlns:tools="http://schemas.android.com/tools">
    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        tools:replace="android:allowBackup,android:label">
        <!-- ... -->
    </application>
</manifest>
```

## 3. Dependency Version Conflict

**Error Pattern**:
- `Conflict with dependency 'com.google.guava:guava' in project ':app'. Resolved versions for app (20.0) and test app (28.1-android) differ.`
- `Duplicate class com.google.common.util.concurrent.ListenableFuture found in modules ...`

**Cause**:
Different dependencies rely on different versions of the same transitive dependency, or different libraries contain the same class.

**Solution 1: Force a Resolution Strategy**
Force a specific version for the entire configuration.

```kotlin
// build.gradle.kts (Module level)
configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:31.1-android")
    }
}
```

**Solution 2: Exclude Transitive Dependency**
Exclude the unwanted module from a specific dependency.

```kotlin
dependencies {
    implementation("com.example:library:1.0") {
        exclude(group = "com.google.guava", module = "guava")
    }
}
```

**Solution 3: BOM (Bill of Materials)**
Use a BOM to align versions (common for Firebase/Jetpack Compose).

```kotlin
dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui") // No version needed
}
```

## 4. Kotlin/Java Version Incompatibility

**Error Pattern**:
- `Class file has wrong version 61.0, should be 55.0`
- `Cannot inline bytecode built with JVM target 17 into bytecode that is being built with JVM target 1.8`

**Cause**:
The library was compiled with a newer Java/Kotlin version than what your project is configured to use.

**Solution**:
Update `compileOptions` and `kotlinOptions` to match the required version (e.g., Java 17).

```kotlin
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}
```
