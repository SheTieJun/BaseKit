## Gradle 7-8遇到的问题
1. Android、kotlin插件升级
2. 必须要有namespace
3. room:kotlin 1.9.0之后需要升级到2.6.0
4. com.android.tools.r8.internal.nc: Sealed classes are not supported as program classes( Java 17 导致的问题)
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