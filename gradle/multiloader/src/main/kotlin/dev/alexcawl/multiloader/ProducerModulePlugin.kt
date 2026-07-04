package dev.alexcawl.multiloader

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConsumableConfiguration
import org.gradle.api.attributes.Usage
import org.gradle.kotlin.dsl.named

private const val ACCESS_TRANSFORMER_PATH = "src/main/resources/META-INF/accesstransformer.cfg"

class ProducerModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.withJavaPlugin {
            java {
                withJavadocJar()
                withSourcesJar()
            }
            val mainSourceSet = target.mainSourceSet()
            val mergedJavaElements = target.mergedJavaElements()
            val mergedResourceElements = target.mergedResourceElements()
            mergedJavaElements.configure {
                outgoing.artifacts(
                    mainSourceSet.flatMap { it.java.sourceDirectories.elements }
                )
            }
            mergedResourceElements.configure {
                outgoing.artifacts(
                    mainSourceSet.flatMap { it.resources.sourceDirectories.elements }
                )
            }
        }
        target.configureNeoforgeAccessTransformer()
    }

    private fun Project.configureNeoforgeAccessTransformer() {
        withNeoforgePlugin {
            neoforge {
                // Automatically enable AccessTransformers if the file exists
                val accessTransformer = file(ACCESS_TRANSFORMER_PATH)
                if (accessTransformer.exists()) {
                    accessTransformers.from(accessTransformer.absolutePath)
                }
            }
        }
    }

    private fun Project.mergedUsage(): Usage {
        return objects.named<Usage>(MERGED_USAGE)
    }

    private fun Project.mergedJavaElements(): NamedDomainObjectProvider<ConsumableConfiguration> {
        return configurations.consumable("mergedJavaElements") {
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, mergedUsage())
                attribute(MergedSource.ATTRIBUTE, MergedSource.JAVA)
            }
        }
    }

    private fun Project.mergedResourceElements(): NamedDomainObjectProvider<ConsumableConfiguration> {
        return configurations.consumable("mergedResourceElements") {
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, mergedUsage())
                attribute(MergedSource.ATTRIBUTE, MergedSource.RESOURCE)
            }
        }
    }
}
