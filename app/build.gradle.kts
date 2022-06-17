plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31
    namespace = "shetj.me.base"
    defaultConfig {
        applicationId = "shetj.me.base"
        minSdk = 24
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        ndk {
            this.abiFilters.add("armeabi-v7a")
            this.abiFilters.add("arm64")
            this.abiFilters.add("x86_64")
            this.abiFilters.add("x86")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    //产品变种组
    flavorDimensions.add("dev")

    productFlavors {
        this.create("dev") {
            dimension = "dev"
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"
        }
    }

    buildTypes {
        release {
            buildConfigField("boolean", "LOG_DEBUG", "false")
            buildConfigField("boolean", "USE_CANARY", "false")
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true // 移除无用的resource文件
            multiDexEnabled = true //ex突破65535的限制
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            buildConfigField("boolean", "LOG_DEBUG", "true")
            buildConfigField("boolean", "USE_CANARY", "true")
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false // 移除无用的resource文件
            multiDexEnabled = true //ex突破65535的限制
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lint {
        checkDependencies = true
    }
}
//
//// 优化编译速度 如果有用到kapt添加如下配置
//kapt {
//    useBuildCache = true
//    javacOptions {
//        option("-Xmaxerrs", 500)
//    }
//}

dependencies {
//    implementation fileTree (include: ["*.jar"], dir: "libs")
    testImplementation("junit:junit:4.13.2")
    val androidx = "1.0.0"
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("androidx.legacy:legacy-support-v4:$androidx")
//    implementation project(path: ":annotation")
//    kapt project(path: ":compilers")
    //https://material.io/develop/android/
//    implementation "androidx.legacy:legacy-support-v4:1.0.0"
//    val fragment_version = "1.1.0"
//    implementation "androidx.fragment:fragment:$fragment_version"
//    implementation "androidx.fragment:fragment-ktx:$fragment_version"


//    implementation "androidx.datastore:datastore-rxjava3:1.0.0-alpha06"
//(Proto DataStore，它允许您存储类型化的对象（由协议缓冲区提供支持）)

    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation(project(":baseKit"))
    //使用AS 自带得就可以了
//    debugImplementation "com.squareup.leakcanary:leakcanary-android:2.0-alpha-2"
//    releaseImplementation "com.squareup.leakcanary:leakcanary-android-no-op:1.6.1"
//    testImplementation "com.squareup.leakcanary:leakcanary-android-no-op:1.6.1"

    implementation("com.github.LuckSiege.PictureSelector:picture_library:v2.5.8")

    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("com.airbnb.android:lottie:3.7.0")

//    implementation "com.github.razerdp:BasePopup:2.2.1"
//    implementation "com.afollestad.material-dialogs:core:3.1.0"

    //图片预览 https://github.com/iielse/ImageWatcher
    implementation("com.github.iielse:ImageWatcher:1.1.5")

    val workVersion = "2.7.0"
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("androidx.work:work-runtime:$workVersion")
    //文件下载
    // core
//    implementation "com.liulishuo.okdownload:okdownload:1.0.7"
// provide sqlite to store breakpoints
//    implementation "com.liulishuo.okdownload:sqlite:1.0.7"
// provide okhttp to connect to backend
//    implementation "com.liulishuo.okdownload:okhttp:1.0.7"

    val qmuiversionShetj = "79920f62d5"
    implementation("com.google.guava:guava:29.0-android")
    implementation("com.github.SheTieJun.QMUI_Android:qmui:$qmuiversionShetj")

//    debugImplementation "com.glance.guolindev:glance:1.0.0" //debug 情况下查看数据库
//    implementation "androidx.core:core-splashscreen:1.0.0-alpha02"

    val pickVersion = "4.1.7"
    implementation("com.github.gzu-liyujiang.AndroidPicker:Common:$pickVersion")
    implementation("com.github.gzu-liyujiang.AndroidPicker:WheelPicker:$pickVersion")
    implementation("androidx.core:core-splashscreen:1.0.0-beta02")
    implementation("androidx.draganddrop:draganddrop:1.0.0")
    implementation("androidx.metrics:metrics-performance:1.0.0-alpha01")
}

apply(from = "../spotless.gradle")