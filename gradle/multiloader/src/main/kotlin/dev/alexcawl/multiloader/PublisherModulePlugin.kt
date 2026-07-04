package dev.alexcawl.multiloader

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConsumableConfiguration
import org.gradle.api.attributes.Usage
import org.gradle.kotlin.dsl.named

class PublisherModulePlugin : Plugin<Project> {
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
