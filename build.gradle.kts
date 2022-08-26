// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("com.android.application") version ("7.2.2") apply false
    id("com.android.library") version ("7.2.2") apply false
    id("org.jetbrains.kotlin.android") version ("1.7.10") apply false
    id("com.diffplug.spotless") version ("6.0.0")
    id("maven-publish")
    id("com.google.devtools.ksp") version ("1.7.10-1.0.6")
}

group = "com.github.SheTieJun"
version = "0.0.1"