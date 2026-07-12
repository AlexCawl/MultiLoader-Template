package dev.alexcawl.mcmultiloader.core.metadata.impl

import dev.alexcawl.mcmultiloader.core.metadata.impl.MetadataConstants.JAR_MANIFEST_ATTRIBUTES_INPUT_PROPERTY
import dev.alexcawl.mcmultiloader.core.metadata.impl.MetadataConstants.RESOURCE_EXPANDS_ATTRIBUTES_INPUT_PROPERTY
import dev.alexcawl.mcmultiloader.core.metadata.impl.MetadataConstants.RESOURCE_EXPANDS_PATTERNS_INPUT_PROPERTY
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.MapProperty
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources

internal fun Project.configureResources(resourcesExpands: DomainObjectSet<ResourceExpand>) {
    resourcesExpands.configureEach {
        tasks.named<ProcessResources>(JavaPlugin.PROCESS_RESOURCES_TASK_NAME) {
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

internal fun Project.configureJarManifest(attributes: MapProperty<String, String>) {
    tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
        inputs.property(JAR_MANIFEST_ATTRIBUTES_INPUT_PROPERTY, attributes)
        doFirst {
            manifest.attributes(attributes.get())
        }
    }
}
