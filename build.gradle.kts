// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        gradlePluginPortal()
    }
}

//https://developer.android.com/reference/tools/gradle-api

plugins {
    id("com.android.application") version ("7.4.1") apply false
    id("com.android.library") version ("7.4.1") apply false
    id("org.jetbrains.kotlin.android") version ("1.7.20") apply false
    id("com.diffplug.spotless") version ("6.0.0")
    id("maven-publish")
//    id("com.google.devtools.ksp") version ("1.7.20-1.0.8")//databinding no support
    id("com.android.test") version "7.4.1" apply false
    id("androidx.benchmark") version "1.1.0-beta04" apply false
}

//apply(from = "https://gist.githubusercontent.com/SheTieJun/f4cb1bd33997c2b46d9e3df40b95a02e/raw/c4b826d3ca4415071097b1642c9b80e50f3f1ad0/subprojects-maven-publishing.gradle")