package dev.alexcawl.mcmultiloader.feature

import dev.alexcawl.mcmultiloader.extension.impl.MetadataExtensionImpl
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources

const val TEMPLATE_INPUT_PROPERTY_PREFIX = "mcMultiLoader.metadata.template"
const val TEMPLATE_PATHS_INPUT_PROPERTY = "paths"
const val TEMPLATE_ATTRIBUTES_INPUT_PROPERTY = "attributes"
const val JAR_MANIFEST_INPUT_PROPERTY = "mcMultiLoader.metadata.jarManifest"

internal fun Project.configureMetadata(extension: MetadataExtensionImpl) {
    val projectDirectory = layout.projectDirectory.asFile
    var templateIndex = 0
    extension.templates.configureEach {
        val inputPrefix = "$TEMPLATE_INPUT_PROPERTY_PREFIX.${templateIndex++}"
        val paths = files.elements.map { elements ->
            elements.map { element ->
                element.asFile.relativeTo(projectDirectory).invariantSeparatorsPath
            }
        }
        val values = attributes
        plugins.withType(JavaPlugin::class.java).configureEach {
            tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, ProcessResources::class.java) {
                inputs.property("$inputPrefix.$TEMPLATE_PATHS_INPUT_PROPERTY", paths)
                inputs.property("$inputPrefix.$TEMPLATE_ATTRIBUTES_INPUT_PROPERTY", values)
                eachFile {
                    if (path in paths.get()) {
                        expand(values.get())
                    }
                }
            }
        }
    }

    val manifestAttributes = extension.jarManifestAttributes
    plugins.withType(JavaPlugin::class.java).configureEach {
        tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java) {
            inputs.property(JAR_MANIFEST_INPUT_PROPERTY, manifestAttributes)
            doFirst {
                manifest.attributes(manifestAttributes.get())
            }
        }
    }
}
