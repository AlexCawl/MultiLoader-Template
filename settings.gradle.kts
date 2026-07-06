pluginManagement {
    includeBuild("gradle/convention")
    includeBuild("mc-multi-loader")

    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven {
                    name = "Fabric"
                    url = uri("https://maven.fabricmc.net")
                }
            }
            filter {
                includeGroup("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
    }
}

// This should match the folder name of the project, or else IDEA may complain (see https://youtrack.jetbrains.com/issue/IDEA-317606)
rootProject.name = "MultiLoader-Template"
include("common")
include("fabric")
include("neoforge")
