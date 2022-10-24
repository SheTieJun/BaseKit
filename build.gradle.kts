// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("com.android.application") version ("7.2.2") apply false
    id("com.android.library") version ("7.2.2") apply false
    id("org.jetbrains.kotlin.android") version ("1.7.20") apply false
    id("com.diffplug.spotless") version ("6.0.0")
    id("maven-publish")
    id("com.google.devtools.ksp") version ("1.7.20-1.0.6")
}

apply(from = "https://gist.githubusercontent.com/SheTieJun/f4cb1bd33997c2b46d9e3df40b95a02e/raw/0332dd33e6431d2ae38a8e9cee136e8de74df9d0/subprojects-maven-publishing.gradle")