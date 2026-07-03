package dev.alexcawl.metadata

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources

class MetadataPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.add(
            MetadataExtension::class.java,
            "metadata",
            object : MetadataExtension {
                override fun resources(vararg patterns: String, block: MetadataExtension.Scope.() -> Unit) {
                    val properties: Map<String, Any> = resolveProperties(block)
                    target.expandProperties(patterns.toList(), properties)
                }

                override fun jarManifest(block: MetadataExtension.Scope.() -> Unit) {
                    val attributes: Map<String, Any> = resolveProperties(block)
                    target.configureJarManifest(attributes)
                }
            }
        )
    }

    private fun resolveProperties(block: MetadataExtension.Scope.() -> Unit): Map<String, Any> {
        return buildMap {
            val map: MutableMap<String, Any> = this
            val scope: MetadataExtension.Scope = object : MetadataExtension.Scope {
                override operator fun String.invoke(value: Any) {
                    map[this] = value
                }
            }
            scope.block()
        }
    }

    private fun Project.expandProperties(patterns: List<String>, properties: Map<String, Any>) {
        tasks.named<ProcessResources>("processResources") {
            filesMatching(patterns) {
                expand(properties)
            }
            inputs.properties(properties)
        }
    }

    private fun Project.configureJarManifest(attributes: Map<String, Any>) {
        tasks.named<Jar>("jar") {
            manifest.attributes(attributes)
        }
    }
}
