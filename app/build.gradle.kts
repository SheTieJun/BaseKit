import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters.None
import org.objectweb.asm.ClassVisitor
import tools.addGuava
import tools.addProInstaller
import tools.addNav
import tools.addPaging


plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    kotlin("android")
}

android {
    compileSdk = 34
    namespace = "shetj.me.base"
    defaultConfig {
        applicationId = "shetj.me.base"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        ndk {
            this.abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86_64", "x86"))
        }
        setProperty("archivesBaseName", "Base-$versionName") //修改Apk的输出名字
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    //产品变种组,
    flavorDimensions += (listOf("dev", "demo"))

//    productFlavors {
//        this.create("dev") {
//            dimension = "dev"
//            versionNameSuffix = "-dev"
//            applicationIdSuffix = ".dev"
//        }
//        this.create("demo") {
//            dimension = "demo"
//            versionNameSuffix = "-demo"
//            applicationIdSuffix = ".demo"
//            minSdk = 24
//        }
//    }

    signingConfigs {
        create("release") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
            keyAlias = "shetj"
            keyPassword = "123456"
            storeFile = file("test.jks")
            storePassword = "123456"
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
            signingConfig = signingConfigs.getByName("release")
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

        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
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
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation(project(":baseKit"))
//    implementation("com.github.SheTieJun:base:1.1.38")
//    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("com.airbnb.android:lottie:5.2.0")
    //图片预览 https://github.com/iielse/ImageWatcher
    implementation("com.github.iielse:ImageWatcher:1.1.5")
//    val qmuiversionShetj = "79920f62d5"
//    implementation("com.github.SheTieJun.QMUI_Android:qmui:$qmuiversionShetj")
    implementation("androidx.core:core-splashscreen:1.0.0")//启动图
    implementation("androidx.draganddrop:draganddrop:1.0.0") //拖动
    implementation("androidx.metrics:metrics-performance:1.0.0-alpha04") // 指标
    implementation("androidx.tracing:tracing-ktx:1.1.0")//将跟踪事件写入系统跟踪缓冲区。

    implementation("com.github.SheTieJun:RoundedProgressBar:550a631d74")
    addPaging()
    addNav()
    addGuava()//大部分功能kotlin都有了
    addProInstaller()
    //https://github.com/SheTieJun/LogKit
    implementation("com.github.SheTieJun.LogKit:logkit-messenger:1.7")
}

androidComponents {
    onVariants {
        if (it.name.contains("debug",true)){
            it.instrumentation.transformClassesWith(DebugAsmFactory::class.java, com.android.build.api.instrumentation.InstrumentationScope.PROJECT) {}
//            it.instrumentation.transformClassesWith(PrivacyCheckFactory::class.java, com.android.build.api.instrumentation.InstrumentationScope.ALL) {}
        }
    }
}

abstract class DebugAsmFactory : AsmClassVisitorFactory<None> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return asm.DebugClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}


abstract class PrivacyCheckFactory : AsmClassVisitorFactory<None> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return asm.PrivacyClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}
