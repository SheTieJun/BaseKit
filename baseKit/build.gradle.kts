import tools.compileSdk
import tools.minCompileSdk
import tools.minSdk

plugins {
    id("kotlin-kapt")
    id("com.android.library")
    id("kotlin-parcelize")
    id("maven-publish")
    kotlin("android")
}

android {
    compileSdk = project.compileSdk
    namespace = "me.shetj.base"
    resourcePrefix = "base_"
    defaultConfig {
        aarMetadata {
            this.minCompileSdk = project.minCompileSdk
        }
        minSdk = project.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }


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

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
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

    api(libs.androidx.work.runtime)
    api(libs.androidx.work.multiprocess)

    api(libs.androidx.navigation)
    api(libs.androidx.navigation.ui)
}

apply(from = "uploadLocal.gradle")
apply(from = "../gradle/spotless.gradle")