import tools.DependencyLibs
import tools.addAndroid
import tools.addCoroutines
import tools.addGson
import tools.addKoin
import tools.addOther
import tools.addRetrofit2
import tools.addRoom

plugins {
    id("com.google.devtools.ksp")
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("maven-publish")
}


android {
    compileSdk = 32
    namespace = "me.shetj.base"
    resourcePrefix = "base_"
    defaultConfig {
        aarMetadata {
            minCompileSdk = 21
        }
        minSdk = (21)
        targetSdk = (32)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
        resourceConfigurations.add("zh")
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

    lint {
        abortOnError = false
        checkOnly.addAll(setOf("NewApi", "HandlerLeak"))
    }
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
apply(from = "../spotless.gradle")