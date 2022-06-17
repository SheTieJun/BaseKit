// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        gradlePluginPortal()
    }
//    dependencies {
//        classpath "com.github.SheTieJun.lib-plugin:base-plugin:5e9776b731"
//    }
}

plugins {
    id("com.android.application") version ("7.2.0") apply false
    id("com.android.library") version ("7.2.0") apply false
    id("org.jetbrains.kotlin.android") version ("1.6.10") apply false
    id("com.diffplug.spotless") version ("6.0.0")
    id("maven-publish")
    id("com.google.devtools.ksp") version ("1.6.10-1.0.2")
}
