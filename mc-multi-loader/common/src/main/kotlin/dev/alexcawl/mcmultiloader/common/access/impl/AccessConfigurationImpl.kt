package dev.alexcawl.mcmultiloader.common.access.impl

import dev.alexcawl.mcmultiloader.common.access.AccessConfiguration
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import javax.inject.Inject

internal abstract class AccessConfigurationImpl @Inject constructor(
    private val layout: ProjectLayout,
    objects: ObjectFactory,
) : AccessConfiguration {

    internal val fabricAccessWidener: RegularFileProperty = objects.fileProperty()

    internal val neoForgeAccessTransformer: RegularFileProperty = objects.fileProperty()

    override fun fabricAccessWidener(path: String) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
    }

    override fun fabricAccessWidener(path: Provider<String>) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
    }

    override fun neoForgeAccessTransformer(path: String) {
        neoForgeAccessTransformer.set(layout.projectDirectory.file(path))
    }

    override fun neoForgeAccessTransformer(path: Provider<String>) {
        neoForgeAccessTransformer.set(layout.projectDirectory.file(path))
    }
}
