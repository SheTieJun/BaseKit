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
    id("kotlin-parcelize")
    id("maven-publish")
    kotlin("android")
}

android {
    compileSdk = 32
    namespace = "me.shetj.base"
    resourcePrefix = "base_"
    defaultConfig {
        aarMetadata {
            this.minCompileSdk = 32
        }
        minSdk = (24)
        targetSdk = (32)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro","consumer-rules.pro")
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
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    lint {
        abortOnError = true
        checkDependencies = true
        checkOnly.addAll(setOf("NewApi", "HandlerLeak"))
    }

    libraryVariants.all {
        if (buildType.name == "release") {
            assembleProvider.configure {
                doLast {
//                    buildOutputs.find {
//                        it.name == "release"
//                    }?.let { output ->
//                        copy {
//                            from(output.outputFile)
//                            into("../aarLib")
//                        }
//                    }
                }
            }
        }
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