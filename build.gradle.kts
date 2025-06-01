import org.jetbrains.changelog.ChangelogSectionUrlBuilder
import org.jetbrains.changelog.date
import tools.versionName

buildscript {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://storage.googleapis.com/r8-releases/raw")
        }
    }
    dependencies {
        classpath("com.android.tools:r8:8.9.35") //FIX com.android.tools.r8.internal.nc: Sealed classes are not supported as program classes( Java 17 导致的问题)
    }
}

plugins {
    id(libs.plugins.android.application.get().pluginId) apply false
    id(libs.plugins.android.library.get().pluginId) apply false
    id(libs.plugins.android.test.get().pluginId) apply false
    id(libs.plugins.kotlin.android.get().pluginId) apply false
    id(libs.plugins.spotless.get().pluginId) version (libs.versions.spotless)
    id(libs.plugins.android.benchmark.get().pluginId) version (libs.versions.benchmark) apply false
    id("maven-publish")
    id("io.gitlab.arturbosch.detekt") version "1.23.3"
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.changelog)
}

//apply(from = "https://gist.githubusercontent.com/SheTieJun/f4cb1bd33997c2b46d9e3df40b95a02e/raw/c4b826d3ca4415071097b1642c9b80e50f3f1ad0/subprojects-maven-publishing.gradle")

apply(from = "gradle/detekt.gradle")

tasks.register<tools.PrintProjectStructureTask>("printProjectStructure")


changelog {
    version.set(project.versionName)
    path.set(file("CHANGELOG.md").canonicalPath)
    header.set(provider { "[${version.get()}] - ${date()}" })
    headerParserRegex.set("""(\d+\.\d+)""".toRegex())
    introduction.set(
        """
        My awesome project that provides a lot of useful features, like:
        
        - Feature 1
        - Feature 2
        - and Feature 3
        """.trimIndent()
    )
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
    lineSeparator.set("\n")
    combinePreReleases.set(true)
    repositoryUrl = "https://github.com/SheTieJun/BaseKit"
}