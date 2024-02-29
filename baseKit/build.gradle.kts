import tools.androidLibrary

plugins {
    id("kotlin-kapt")
    id("com.android.library")
    id("kotlin-parcelize")
    id("maven-publish")
    kotlin("android")
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
        resources.merges.add("../LICENSE")
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
    api(libs.shetj.datastore)
    api(libs.shetj.qmui)
    api(libs.shetj.activity)
}


fun DependencyHandler.addRetrofit2(){
    api(libs.retrofit)
    api(libs.retrofit.gson)
}

fun DependencyHandler.addRoom(){
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    add("kapt", libs.androidx.room.compiler)
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
}

apply(from = "uploadLocal.gradle")
apply(from = "../gradle/spotless.gradle")