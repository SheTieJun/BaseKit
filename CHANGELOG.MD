## CHANGELOG

# 2024年1月30日
- [ProjectUtils.kt](buildSrc/src/main/kotlin/tools/ProjectUtils.kt):用于读取`gradle.properties`中的配置
- [buildSrc](buildSrc):用于管理`build.gradle`中的依赖版本号
- Coroutines : 1.7.3 -> 1.8.0-RC2
- Lifecycle : 2.5.1 -> 2.7.0
- Navigation : 2.6.0 -> 2.7.6
- Room : 2.6.0 -> 2.6.1
- material : 1.9.0-> 1.11.0

# 2024年1月22日
- Gradle : 7.4 -> 8.0
- [遇到的问题](doc/gradle/Gradle7-8.MD)

# 2023年9月21日
- add androidx.window

# 2023年9月19日
- core-ktx :      1.10.1 -> 1.12.0

# 2023年8月2日
- recyclerview:   1.2.1 -> 1.3.1
- paging :        3.1.1 -> 3.2.0
- Room :          2.5.0 -> 2.5.2
- core-ktx :      1.9.0 -> 1.10.1
- fragment-ktx:   1.6.0-alpha04 -> 1.6.1
- activity-ktx:   1.7.0-alpha02 -> 1.3.2
- workVersion:    2.8.0 -> 2.8.1
## Fix
- newInstance -> getDeclaredConstructor().newInstance() 