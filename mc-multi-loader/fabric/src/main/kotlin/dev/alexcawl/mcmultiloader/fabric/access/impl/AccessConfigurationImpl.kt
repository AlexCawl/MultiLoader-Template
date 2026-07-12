package dev.alexcawl.mcmultiloader.fabric.access.impl

import dev.alexcawl.mcmultiloader.fabric.access.AccessConfiguration
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.withType
import javax.inject.Inject

internal abstract class AccessConfigurationImpl @Inject constructor(
    project: Project,
    private val layout: ProjectLayout,
    artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
    directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
    accessWideners: NamedDomainObjectProvider<ResolvableConfiguration>,
    objects: ObjectFactory,
) : AccessConfiguration {

    private val fabricAccessWidener: RegularFileProperty = objects.fileProperty()

    init {
        with(project) {
            plugins.withType<JavaPlugin> {
                configureAccess(fabricAccessWidener, artifacts, directArtifacts, accessWideners)
            }
        }
    }

    override fun fabricAccessWidener(path: String) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
    }

    override fun fabricAccessWidener(path: Provider<String>) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
    }
}
