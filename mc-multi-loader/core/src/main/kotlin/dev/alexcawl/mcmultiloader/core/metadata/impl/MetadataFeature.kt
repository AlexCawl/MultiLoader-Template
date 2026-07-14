package dev.alexcawl.mcmultiloader.core.metadata.impl

import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.MapProperty
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources

internal fun Project.metadataFeature(
    resourcesExpands: DomainObjectSet<ResourceExpand>,
    jarManifestAttributes: MapProperty<String, String>
) {
    configureResources(resourcesExpands)
    configureJarManifest(jarManifestAttributes)
}

private const val RESOURCE_EXPANDS_PATTERNS_INPUT_PROPERTY = "mcMultiLoader.metadata.resources.pattern"
private const val RESOURCE_EXPANDS_ATTRIBUTES_INPUT_PROPERTY = "mcMultiLoader.metadata.resources.attributes"
private const val JAR_MANIFEST_ATTRIBUTES_INPUT_PROPERTY = "mcMultiLoader.metadata.jarManifest.attributes"

private fun Project.configureResources(resourcesExpands: DomainObjectSet<ResourceExpand>) {
    resourcesExpands.configureEach {
        tasks.named<ProcessResources>(JavaPlugin.PROCESS_RESOURCES_TASK_NAME) {
            inputs.property(RESOURCE_EXPANDS_PATTERNS_INPUT_PROPERTY, patterns)
            inputs.property(RESOURCE_EXPANDS_ATTRIBUTES_INPUT_PROPERTY, attributes)
            filesMatching(patterns.get()) {
                expand(attributes.get())
            }
        }
    }
}

private fun Project.configureJarManifest(jarManifestAttributes: MapProperty<String, String>) {
    tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
        inputs.property(JAR_MANIFEST_ATTRIBUTES_INPUT_PROPERTY, jarManifestAttributes)
        manifest.attributes(jarManifestAttributes.get())
    }
}
