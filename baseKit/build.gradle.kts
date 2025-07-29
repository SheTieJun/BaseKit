import tools.androidLibrary

plugins {
    id("com.android.library")
    id("kotlin-parcelize")
    id("maven-publish")
    kotlin("android")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.ksp)
}

androidLibrary("me.shetj.base",config = true){
    resourcePrefix = "base_"
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "consumer-rules.pro"
            )
        }
    }
    lint {
        abortOnError = true
        checkDependencies = true
        checkOnly.addAll(setOf("NewApi", "HandlerLeak"))
    }

    packaging {
        //pickFirst "**/xxx.so" //告诉Gradle包装时只选一个，否则你会得到冲突。
        resources.excludes += "DebugProbesKt.bin"
    }
    //    ./gradlew printProjectStructure
    tasks.register<tools.PrintProjectStructureTask>("printProjectStructure")
}

dependencies {
    addAndroid()
    addRetrofit2()
    addKoin()
    addCoroutines()
    addOther()
    addRoom()
    addCompose()
    addPage()
}

composeCompiler {
}

kotlin {
    jvmToolchain(17)
}
fun DependencyHandler.addPage(){
    api(libs.androidx.paging)
    api(libs.compose.paging)
}

fun DependencyHandler.addCompose() {
    api(platform(libs.compose.bom))
    api(libs.compose.material3)
    api(libs.compose.foundation)
    api(libs.ui)
    api(libs.ui.graphics)
    api(libs.ui.tooling.preview)
    api(libs.compose.material.iconsCore)
    api(libs.compose.material.iconsExt)
    api(libs.compose.material3.windowSize)
    api(libs.activity.compose)
    api(libs.compose.livedata)
    api(libs.compose.lifecycle.viewmodel)
    api(libs.compose.constraintlayout)

    debugApi(libs.ui.tooling)
//    androidTestApi(libs.ui.test.junit4)
    debugApi(libs.ui.test.manifest)
}

fun DependencyHandler.addKoin(){
    api(libs.koinAndroid)
    api(libs.koinWorkManager)
}

fun DependencyHandler.addOther(){
    api(libs.third.gson)
    api(libs.third.glide)
    api(libs.third.brv)
    api(libs.third.timber)
    api(libs.shetj.qmui)
}


fun DependencyHandler.addRetrofit2(){
    api(libs.retrofit)
    api(libs.retrofit.gson)
    api(libs.okhttp.sse)
}

fun DependencyHandler.addRoom(){
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    add("ksp", libs.androidx.room.compiler)
}

fun DependencyHandler.addCoroutines(){
    api(libs.coroutines.android)
    api(libs.coroutines.core)
}

fun DependencyHandler.addAndroid(){
    api(libs.androidx.appcompat)
    api(libs.androidx.core)
    api(libs.androidx.material)
    api(libs.androidx.fragment)
    api(libs.androidx.activity)

    api(libs.androidx.lifecycle.runtime)
    api(libs.androidx.lifecycle.livedata)
    api(libs.androidx.lifecycle.viewmodel)

    api(libs.androidx.recyclerview)
    api(libs.androidx.recyclerview.selection)
    api(libs.androidx.cardview)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.swiperefreshlayout)
    api(libs.androidx.palette)
    api(libs.androidx.splashscreen)
    api(libs.androidx.startup)
    api(libs.androidx.webKit)
    api(libs.androidx.browser)
    api(libs.androidx.window)

    api(libs.androidx.animationCore)
    api(libs.androidx.datastoreCore)
    api(libs.androidx.datastore.preferences)

    api(libs.androidx.concurrent)

    api(libs.androidx.work.runtime) //workManager后台工作组件
    api(libs.androidx.work.multiprocess)

    api(libs.androidx.navigation)
    api(libs.androidx.navigation.ui)

    api(libs.androidx.preference) //设置组件

    api(libs.androidx.biometric.ktx)  //指纹识别

//    api(libs.androidx.asynclayoutinflater) //异步膨胀布局以避免界面出现卡顿。

//    api(libs.androidx.pdf) // pdf预览
}

apply(from = "uploadLocal.gradle")
//apply(from = "../gradle/spotless.gradle")