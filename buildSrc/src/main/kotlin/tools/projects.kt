package tools

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.androidLibrary(
    name: String,
    config: Boolean = false,
    action: LibraryExtension.() -> Unit = {},
) {
    extensions.configure<LibraryExtension> {
        namespace = name
        compileSdk = project.compileSdk
        defaultConfig {
            minSdk = project.minSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            aarMetadata {
                this.minCompileSdk = project.minCompileSdk
            }
            consumerProguardFile("consumer-rules.pro")
        }
        packaging {
            resources.pickFirsts += listOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*kotlin_module",
            )
        }
        testOptions {
            unitTests.isIncludeAndroidResources = true
        }
        lint {
            warningsAsErrors = true
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        buildFeatures {
            buildConfig = config
            aidl = true
            viewBinding = true
            dataBinding = true
        }
        action()
    }
}

fun Project.androidApplication(
    name: String,
    action: ApplicationExtension.() -> Unit = {},
) {
    extensions.configure<ApplicationExtension> {
        namespace = name
        compileSdk = project.compileSdk
        defaultConfig {
            minSdk = project.minSdk
            targetSdk = project.targetSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            applicationId = name
            versionCode = project.versionCode
            versionName = project.versionName
            vectorDrawables.useSupportLibrary = true
        }
        packaging {
            resources.pickFirsts += listOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*kotlin_module",
            )
            resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        testOptions {
            unitTests.isIncludeAndroidResources = true
        }
        lint {
            warningsAsErrors = true
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
            }
        }
        buildFeatures {
            aidl = true
            viewBinding = true
            dataBinding = true
            buildConfig = true
            compose = true
        }
        action()
    }
}

fun Project.androidTest(
    name: String,
    config: Boolean = false,
    action: TestExtension.() -> Unit = {},
) {
    extensions.configure<TestExtension> {
        namespace = name
        compileSdk = project.compileSdk
        defaultConfig {
            minSdk = project.minSdk
            targetSdk = project.targetSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }
        packaging {
            resources.pickFirsts += listOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*kotlin_module",
            )
        }
        testOptions {
            unitTests.isIncludeAndroidResources = true
        }
        lint {
            warningsAsErrors = true
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        buildFeatures {
            buildConfig = config
        }
        action()
    }
}