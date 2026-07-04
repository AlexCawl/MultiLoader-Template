package dev.alexcawl.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class RepositoriesConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.repositories.apply {
            exclusiveContent {
                forRepository {
                    maven {
                        name = "Minecraft Libraries"
                        url = target.uri("https://libraries.minecraft.net")
                    }
                }
                filter { includeGroup("org.lwjgl") }
            }
            exclusiveContent {
                forRepository {
                    maven {
                        name = "Sponge"
                        url = target.uri("https://repo.spongepowered.org/repository/maven-public")
                    }
                }
                filter { includeGroupAndSubgroups("org.spongepowered") }
            }
        }
    }
}
