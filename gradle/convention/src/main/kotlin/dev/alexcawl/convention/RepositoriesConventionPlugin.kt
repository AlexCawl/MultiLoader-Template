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
            exclusiveContent {
                forRepositories(
                    maven {
                        name = "ParchmentMC"
                        url = target.uri("https://maven.parchmentmc.org/")
                    },
                    maven {
                        name = "NeoForge"
                        url = target.uri("https://maven.neoforged.net/releases")
                    }
                )
                filter { includeGroup("org.parchmentmc.data") }
            }
            maven {
                name = "BlameJared"
                url = target.uri("https://maven.blamejared.com")
            }
        }
    }
}
