package dev.alexcawl.mcmultiloader.common.access.impl

import dev.alexcawl.mcmultiloader.common.access.AccessConfiguration
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.withType
import javax.inject.Inject

internal abstract class AccessConfigurationImpl @Inject constructor(
    private val project: Project,
    private val layout: ProjectLayout,
    objects: ObjectFactory,
) : AccessConfiguration {

    private val fabricAccessWidener: RegularFileProperty = objects.fileProperty()

    private val neoForgeAccessTransformer: RegularFileProperty = objects.fileProperty()

    override fun fabricAccessWidener(path: String) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
        with(project) {
            plugins.withType<JavaPlugin> {
                configureFabricAccessWidenerElements(fabricAccessWidener)
            }
        }
    }

    override fun fabricAccessWidener(path: Provider<String>) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
        with(project) {
            plugins.withType<JavaPlugin> {
                configureFabricAccessWidenerElements(fabricAccessWidener)
            }
        }
    }

    override fun neoForgeAccessTransformer(path: String) {
        neoForgeAccessTransformer.set(layout.projectDirectory.file(path))
        with(project) {
            plugins.withType<JavaPlugin> {
                configureNeoForgeAccessTransformerElements(neoForgeAccessTransformer)
            }
        }
    }

    override fun neoForgeAccessTransformer(path: Provider<String>) {
        neoForgeAccessTransformer.set(layout.projectDirectory.file(path))
        with(project) {
            plugins.withType<JavaPlugin> {
                configureNeoForgeAccessTransformerElements(neoForgeAccessTransformer)
            }
        }
    }
}
