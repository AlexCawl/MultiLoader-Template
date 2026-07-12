package dev.alexcawl.mcmultiloader.neoforge.access.impl

import dev.alexcawl.mcmultiloader.neoforge.access.AccessConfiguration
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import javax.inject.Inject

internal abstract class AccessConfigurationImpl @Inject constructor(
    project: Project,
    private val layout: ProjectLayout,
    accessTransformers: NamedDomainObjectProvider<ResolvableConfiguration>,
    objects: ObjectFactory,
) : AccessConfiguration {

    private val neoForgeAccessTransformer: RegularFileProperty = objects.fileProperty()

    init {
        project.configureAccess(
            neoForgeAccessTransformer,
            accessTransformers,
        )
    }

    override fun neoForgeAccessTransformer(path: String) {
        neoForgeAccessTransformer.set(layout.projectDirectory.file(path))
    }

    override fun neoForgeAccessTransformer(path: Provider<String>) {
        neoForgeAccessTransformer.set(layout.projectDirectory.file(path))
    }
}
