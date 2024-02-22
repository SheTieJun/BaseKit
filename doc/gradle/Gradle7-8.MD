## 记录Android Studio 升级Gradle7.5-8.5遇到的问题

首先说明我升级到的是：8.5，Android、kotlin相关插件升级这里就不做说明了

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

### 问题1：Gradle8.0+后需要使用JDK17，JavaVersion.VERSION_17

我的AS修改后需要重新打开项目,否则不起效果。

```Kotlin
compileOptions {
   sourceCompatibility = JavaVersion.VERSION_17
   targetCompatibility = JavaVersion.VERSION_17
}

kotlinOptions {
   jvmTarget = JavaVersion.VERSION_17.toString()
}
```
### 问题2 ：namespace，所有模块必须要有namespace

所有模块添加namespace，同时会导致模块的R资源路径问题，同时需要移除AndroidManifest.xml中的`<manifest package="com.xxx.xxx">`

### 问题3：Room编译问题

升级kotlin 1.9.0之后需要把Room升级到2.6.0,否则编译错误。

### 问题4：com.android.tools.r8.internal.nc: Sealed classes are not supported as program classes

升级Java 17 导致的 sealed 修饰符问题。

**解决方法**：

```groovy
    pluginManagement {
    buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://storage.googleapis.com/r8-releases/raw")
        }
    }
    dependencies {
        classpath("com.android.tools:r8:8.2.24")
    }
 }
```

### 问题5：AIDL编译在Gradle8.0默认false

需要手动开启，才可以生成对应的java文件

```groovy
buildFeatures {
    aidl = true
}
```
### 问题6 ：buildConfig在Gradle8.0默认false

需要手动开启`buildConfig`支持,否则会找不到对应的类`BuildConfig.java`

```kotlin
buildFeatures {
    buildConfig = true
}
```