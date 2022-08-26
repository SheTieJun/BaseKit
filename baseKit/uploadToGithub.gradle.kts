apply(plugin = "maven-publish")

afterEvaluate {

    val properties = java.util.Properties()
    properties.load(java.io.FileInputStream(rootProject.file("local.properties")))

    val archivesBaseName = project.name.toString()
    val gitHubGroupId = project.group.toString()
    val libVersion = project.version.toString()


    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/SheTieJun/BaseKit")
                credentials {
                    username = "SheTieJun"
                    password = properties.getProperty("GITHUB_TOKEN") ?: System.getenv("GITHUB_TOKEN")
                    println("username=$username  && token=$password")
                }
            }
        }

        publications {
            register<MavenPublication>("GithubAAR") {
                from(components["release"])
                groupId = gitHubGroupId
                version = libVersion
                artifactId = archivesBaseName
                pom {
                    distributionManagement {
                        relocation {
                            // New artifact coordinates
                            groupId.set(gitHubGroupId)
                            artifactId.set(archivesBaseName)
                            version.set(libVersion)
                            message.set("first publish")
                        }
                    }
                }
            }
        }
    }
}