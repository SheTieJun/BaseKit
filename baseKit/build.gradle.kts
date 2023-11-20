import tools.addAndroid
import tools.addCoroutines
import tools.addGson
import tools.addKoin
import tools.addOther
import tools.addRetrofit2
import tools.addRoom

plugins {
    id("kotlin-kapt")
    id("com.android.library")
    id("kotlin-parcelize")
    id("maven-publish")
    kotlin("android")
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "me.shetj.base"
    resourcePrefix = "base_"
    defaultConfig {
        aarMetadata {
            this.minCompileSdk = (findProperty("android.minCompileSdk") as String).toInt()
        }
        minSdk = (findProperty("android.minSdk") as String).toInt()
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
    addRoom()
    addRetrofit2()
    addGson()
    addKoin()
    addCoroutines()
    addOther()
}

apply(from = "uploadLocal.gradle")
apply(from = "../gradle/spotless.gradle")