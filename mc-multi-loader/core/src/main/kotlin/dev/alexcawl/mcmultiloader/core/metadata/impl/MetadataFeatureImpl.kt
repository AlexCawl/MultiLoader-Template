package dev.alexcawl.mcmultiloader.core.metadata.impl

import dev.alexcawl.mcmultiloader.core.metadata.MetadataFeature
import dev.alexcawl.mcmultiloader.core.metadata.impl.MetadataConstants.JAR_MANIFEST_ATTRIBUTES_INPUT_PROPERTY
import dev.alexcawl.mcmultiloader.core.metadata.impl.MetadataConstants.RESOURCE_EXPANDS_ATTRIBUTES_INPUT_PROPERTY
import dev.alexcawl.mcmultiloader.core.metadata.impl.MetadataConstants.RESOURCE_EXPANDS_PATTERNS_INPUT_PROPERTY
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.MapProperty
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import javax.inject.Inject

internal abstract class MetadataFeatureImpl @Inject constructor(
    private val project: Project,
    objects: ObjectFactory,
) : MetadataFeature {

    override val configuration: MetadataConfigurationImpl = objects.newInstance<MetadataConfigurationImpl>()

    override fun install() {
        with(project) {
            plugins.withType<JavaPlugin> {
                with(configuration) {
                    configureResources(resourcesExpands)
                    configureJarManifest(jarManifestAttributes)
                }
            }
        }
    }

    private fun Project.configureResources(resourcesExpands: DomainObjectSet<ResourceExpand>) {
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

    private fun Project.configureJarManifest(attributes: MapProperty<String, String>) {
        tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
            inputs.property(JAR_MANIFEST_ATTRIBUTES_INPUT_PROPERTY, attributes)
            doFirst {
                manifest.attributes(attributes.get())
            }
        }
    }
}