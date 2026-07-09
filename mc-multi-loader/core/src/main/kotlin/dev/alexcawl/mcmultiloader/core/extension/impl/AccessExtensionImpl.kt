package dev.alexcawl.mcmultiloader.core.extension.impl

import dev.alexcawl.mcmultiloader.core.extension.AccessExtension
import org.gradle.api.DomainObjectSet
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import javax.inject.Inject

internal abstract class AccessExtensionImpl @Inject constructor(
    private val layout: ProjectLayout,
    objects: ObjectFactory,
) : AccessExtension {

    internal val configured: DomainObjectSet<AccessType> = objects.domainObjectSet(AccessType::class.java)

    internal val fabricAccessWidener: RegularFileProperty = objects.fileProperty()

    internal val neoForgeAccessTransformer: RegularFileProperty = objects.fileProperty()

    override fun fabricAccessWidener(path: String) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
        configured.add(AccessType.FABRIC_ACCESS_WIDENER)
    }

    override fun fabricAccessWidener(path: Provider<String>) {
        fabricAccessWidener.set(layout.projectDirectory.file(path))
        configured.add(AccessType.FABRIC_ACCESS_WIDENER)
    }

    override fun neoForgeAccessTransformer(path: String) {
        neoForgeAccessTransformer.set(layout.projectDirectory.file(path))
        configured.add(AccessType.NEOFORGE_ACCESS_TRANSFORMER)
    }

    override fun neoForgeAccessTransformer(path: Provider<String>) {
        neoForgeAccessTransformer.set(layout.projectDirectory.file(path))
        configured.add(AccessType.NEOFORGE_ACCESS_TRANSFORMER)
    }
}

internal enum class AccessType {
    FABRIC_ACCESS_WIDENER,
    NEOFORGE_ACCESS_TRANSFORMER,
}
