package dev.alexcawl.mcmultiloader.extension.impl

import dev.alexcawl.mcmultiloader.extension.AccessExtension
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import javax.inject.Inject

internal abstract class FabricAccessExtensionImpl @Inject constructor(
    private val layout: ProjectLayout,
    objects: ObjectFactory,
) : AccessExtension {

    internal val fabricAccessWidener: RegularFileProperty = objects.fileProperty()

    private val ignoredNeoForgeAccessTransformer: RegularFileProperty = objects.fileProperty()

    override fun fabricAccessWidener(path: String) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
    }

    override fun fabricAccessWidener(path: Provider<String>) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
    }

    override fun neoForgeAccessTransformer(path: String) {
        ignoredNeoForgeAccessTransformer.set(layout.projectDirectory.file(path))
    }

    override fun neoForgeAccessTransformer(path: Provider<String>) {
        ignoredNeoForgeAccessTransformer.set(layout.projectDirectory.file(path))
    }
}
