---
name: "library-publisher"
description: "Guide for preparing and publishing Android libraries. Invoke when user asks about publishing AARs, configuring Maven Publish, or library distribution."
---

# Android Library Publishing Guide

This skill provides a step-by-step guide to preparing, configuring, and publishing Android libraries (AARs) using the Maven Publish plugin.

## 📋 1. Prepare Library for Release

Before publishing, ensure your library is configured correctly in `build.gradle.kts` (or `build.gradle`).

### Enable AAR Build
Ensure the plugin is applied:
```kotlin
plugins {
    id("com.android.library")
    id("maven-publish") // Required for publishing
}
```

### Configure Library Metadata
Set the namespace and resource prefix to avoid conflicts.
```kotlin
android {
    namespace = "com.example.mylibrary"
    resourcePrefix = "lib_" // Prefix for resources (layouts, strings, etc.)
    
    publishing {
        // Publish only release build variant by default
        singleVariant("release") {
            withSourcesJar() // Include source code
            withJavadocJar() // Include JavaDocs
        }
    }
}
```

## 🚀 2. Configure Maven Publish Plugin

Use the `maven-publish` plugin to define *what* and *where* to publish.

### Basic Configuration
Add this to your library module's `build.gradle.kts`:

```kotlin
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                // The coordinates of the library
                groupId = "com.example"
                artifactId = "mylibrary"
                version = "1.0.0"

                // Apply the component (AAR + dependencies)
                from(components["release"])

                // Metadata
                pom {
                    name.set("My Library")
                    description.set("A sample Android library")
                    url.set("https://github.com/example/mylibrary")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("johndoe")
                            name.set("John Doe")
                            email.set("john.doe@example.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/example/mylibrary.git")
                        developerConnection.set("scm:git:ssh://github.com/example/mylibrary.git")
                        url.set("https://github.com/example/mylibrary")
                    }
                }
            }
        }
        
        // Define repositories (Where to publish)
        repositories {
            // 1. Local Folder Repository (Best for distribution via file/zip)
            maven {
                name = "myLocalRepo"
                url = uri(layout.buildDirectory.dir("repo"))
            }
            
            // 2. Remote Repository (Maven Central / Nexus / Artifactory)
            // maven {
            //    name = "myRemoteRepo"
            //    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            //    credentials {
            //        username = System.getenv("OSSRH_USERNAME")
            //        password = System.getenv("OSSRH_PASSWORD")
            //    }
            // }
        }
    }
}
```

## 📦 3. Publishing Commands

Gradle generates publishing tasks based on the publication name and repository name.

### Task Naming Convention
Task name format: `publish<PublicationName>PublicationTo<RepositoryName>Repository`

### Common Tasks

*   **Publish to Local Maven (`~/.m2/repository`)**:
    ```bash
    ./gradlew publishReleasePublicationToMavenLocal
    ```
    *Use this for local testing with other projects on your machine.*

*   **Publish to Defined Repository**:
    ```bash
    # Publishes 'release' publication to 'myLocalRepo' repository
    ./gradlew publishReleasePublicationToMyLocalRepoRepository
    
    # Or publish all defined publications to all defined repositories
    ./gradlew publish
    ```

## 🧩 4. Fused Library (Multi-Module Publishing)

**Fused Library** allows you to package multiple Android library modules into a single AAR. This is useful for hiding implementation details or simplifying dependencies for the consumer.

### Prerequisites
*   AGP 9.0+ recommended.

### Configuration
1.  **Create a new module** (e.g., `myFusedLibrary`).
2.  **Apply the plugin** in `myFusedLibrary/build.gradle.kts`:

```kotlin
plugins {
    id("com.android.fused-library")
    id("maven-publish")
}

androidFusedLibrary {
    namespace = "com.example.myFusedLibrary"
    minSdk = 21
}

dependencies {
    // Include the modules you want to merge
    include(project(":image-rendering"))
    include("com.example:external-lib:1.0.0")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.example"
            artifactId = "my-fused-library"
            version = "1.0.0"
            // MUST use 'fusedLibraryComponent'
            from(components["fusedLibraryComponent"])
        }
    }
}
```

## 🛠 5. Advanced Options

### Multi-Variant Publishing
If you need to publish `debug` and `release` variants (or custom flavors):

```kotlin
android {
    publishing {
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
            allVariants() // Publishes all build variants
        }
    }
}
```

### Publishing Test Fixtures
If your library includes test helpers for consumers:

```kotlin
android {
    testFixtures {
        enable = true
    }
}
```
