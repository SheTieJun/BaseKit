import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters.None
import org.objectweb.asm.ClassVisitor
import tools.addGuava
import tools.addProInstaller
import tools.androidApplication


plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    kotlin("android")
}


androidApplication("shetj.me.base"){
    defaultConfig {
        ndk {
            this.abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86_64", "x86"))
        }
        setProperty("archivesBaseName", "Base-$versionName") //修改Apk的输出名字
    }
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
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true  //删除无用资源
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
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

dependencies {
    implementation(fileTree("libs") {
        include("*.jar","*.aar")
    })
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.ui.tooling.preview)
    androidTestImplementation(libs.ui.test.junit4)
    testImplementation(libs.junit)
    implementation(libs.androidx.legacy.v4)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(project(":baseKit"))
    implementation(libs.lottie)
    implementation(libs.androidx.dragAndDrop) //拖动
    implementation(libs.androidx.metrics.performance) // 指标
    implementation(libs.androidx.tracing.ktx)//将跟踪事件写入系统跟踪缓冲区。
    implementation(libs.roundedProgressBar)
    addGuava()//大部分功能kotlin都有了
    addProInstaller()

    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.ui)

    //https://github.com/SheTieJun/LogKit
    implementation(libs.logkit.messenger)
    implementation(libs.androidx.preference)
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
