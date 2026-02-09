import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters.None
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.objectweb.asm.ClassVisitor
import tools.addGuava
import tools.addProInstaller
import tools.androidApplication


plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}


androidApplication("shetj.me.base") {


    defaultConfig {
        ndk {
            this.abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86_64", "x86"))
        }
        setProperty("archivesBaseName", "Base-$versionName") //修改Apk的输出名字
    }
    signingConfigs {
        create("release") {
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

//        create("benchmark") {
//            initWith(getByName("release"))
//            matchingFallbacks += listOf("release")
//            signingConfig = signingConfigs.getByName("debug")
//            isDebuggable = false
//        }
    }

    lint {
        disable.addAll(listOf("NullSafeMutableLiveData","EnsureInitializerMetadata"))
        checkDependencies = true
    }

    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}


composeCompiler {
    featureFlags.add(ComposeFeatureFlag.StrongSkipping)
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
//    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}


kotlin {
    jvmToolchain(17)
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
}

dependencies {
    implementation(fileTree("libs") {
        include("*.jar", "*.aar")
    })
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
//    androidTestImplementation(libs.ui.test.junit4)
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
    implementation(libs.shetj.record.core)
    implementation(libs.shetj.record.mix)


    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    implementation(libs.androidx.camera.lifecycle)
    // If you want to additionally use the CameraX VideoCapture library
    implementation(libs.androidx.camera.video)
    // If you want to additionally use the CameraX View class
    implementation(libs.androidx.camera.view)
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation(libs.androidx.camera.mlkit.vision)
    // If you want to additionally use the CameraX Extensions library
    implementation(libs.androidx.camera.extensions)
}

androidComponents {
    onVariants {
        if (it.name.contains("debug", true)) {
            it.instrumentation.transformClassesWith(DebugAsmFactory::class.java, com.android.build.api.instrumentation.InstrumentationScope.PROJECT) {}
//            it.instrumentation.transformClassesWith(PrivacyCheckFactory::class.java, com.android.build.api.instrumentation.InstrumentationScope.ALL) {}
        }
    }
}

// 配合注解，输出调用栈
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
