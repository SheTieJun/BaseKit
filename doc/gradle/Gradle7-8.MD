## Gradle 7.5-8.0遇到的问题
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-all.zip
```

1. Android、kotlin插件升级，需要使用JDK17, jvmTarget 11 ->17 ，我的AS修改后需要重新打开项目,否则不起效果
    ```groovy
        kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        }
    ```
2. 必须要有namespace，同时会导致模块的R资源路径问题
3. room版本问题:kotlin 1.9.0之后需要升级到2.6.0,否则编译错误
4. com.android.tools.r8.internal.nc: Sealed classes are not supported as program classes( Java 17 导致的问题)，还有就是Gradle降级
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
5. AIDL编译问题 在8.0默认false，需要手动开启生成对应的java文件
```groovy
buildFeatures {
    aidl = true
}
```
6. 8.0之后的版本，需要手动开启buildConfig支持,否则会找不到对应的类`BuildConfig`
```kotlin
buildFeatures {
    buildConfig = true
}
```