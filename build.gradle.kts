buildscript {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://storage.googleapis.com/r8-releases/raw")
        }
    }
    dependencies {
        classpath("com.android.tools:r8:8.2.26") //FIX com.android.tools.r8.internal.nc: Sealed classes are not supported as program classes( Java 17 导致的问题)
    }
}

@Suppress("DSL_SCOPE_VIOLATION") //fix libs error
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test)  apply false

    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.android.benchmark) apply false
    id("maven-publish")
    id("io.gitlab.arturbosch.detekt") version "1.23.3"
}

//apply(from = "https://gist.githubusercontent.com/SheTieJun/f4cb1bd33997c2b46d9e3df40b95a02e/raw/c4b826d3ca4415071097b1642c9b80e50f3f1ad0/subprojects-maven-publishing.gradle")

apply(from = "gradle/detekt.gradle")

tasks.register<tools.PrintProjectStructureTask>("printProjectStructure")