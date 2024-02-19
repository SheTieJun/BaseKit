@Suppress("DSL_SCOPE_VIOLATION") //fix libs error
plugins {
    `kotlin-dsl-base`   //支持kotlin
    id("java")  //支持java
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.1.0")
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-util:9.6")
    implementation("org.ow2.asm:asm-commons:9.2")
    implementation("javax.mail:mail:1.4.5")
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = 17.toString()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
