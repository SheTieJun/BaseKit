import org.gradle.kotlin.dsl.`kotlin-dsl`
plugins {
    `kotlin-dsl`   //支持kotlin
    id("java")  //支持java
}




tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly("com.android.tools.build:gradle:7.2.2")
    implementation ("org.ow2.asm:asm:9.1")
    implementation ( "org.ow2.asm:asm-util:9.1")
    implementation  ("org.ow2.asm:asm-commons:9.1")
}
