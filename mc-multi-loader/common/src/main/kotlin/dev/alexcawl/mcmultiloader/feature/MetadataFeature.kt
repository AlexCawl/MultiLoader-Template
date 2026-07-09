package dev.alexcawl.mcmultiloader.feature

import dev.alexcawl.mcmultiloader.extension.impl.ResourceExpand
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.MapProperty
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

private const val RESOURCE_EXPANDS_PATTERNS_INPUT_PROPERTY = "mcMultiLoader.metadata.resources.pattern"
private const val RESOURCE_EXPANDS_ATTRIBUTES_INPUT_PROPERTY = "mcMultiLoader.metadata.resources.attributes"
private const val JAR_MANIFEST_ATTRIBUTES_INPUT_PROPERTY = "mcMultiLoader.metadata.jarManifest.attributes"

internal fun configureMetadata(
    project: Project,
    resourcesExpands: DomainObjectSet<ResourceExpand>,
    jarManifestAttributes: MapProperty<String, String>,
) {
    project.plugins.withType<JavaPlugin> {
        configureResources(project, resourcesExpands)
        configureJarManifest(project, jarManifestAttributes)
    }
}

private fun configureResources(project: Project, resourcesExpands: DomainObjectSet<ResourceExpand>) {
    resourcesExpands.configureEach {
        project.tasks.named<ProcessResources>(JavaPlugin.PROCESS_RESOURCES_TASK_NAME) {
            inputs.property(RESOURCE_EXPANDS_PATTERNS_INPUT_PROPERTY, patterns)
            inputs.property(RESOURCE_EXPANDS_ATTRIBUTES_INPUT_PROPERTY, attributes)
            doFirst {
                filesMatching(patterns.get()) {
                    expand(attributes.get())
                }
            }
        }
    }
}

private fun configureJarManifest(project: Project, attributes: MapProperty<String, String>) {
    project.tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
        inputs.property(JAR_MANIFEST_ATTRIBUTES_INPUT_PROPERTY, attributes)
        doFirst {
            manifest.attributes(attributes.get())
        }
    }
}
