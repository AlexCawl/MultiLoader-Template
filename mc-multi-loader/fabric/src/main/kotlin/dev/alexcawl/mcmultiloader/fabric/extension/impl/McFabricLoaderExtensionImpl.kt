package dev.alexcawl.mcmultiloader.fabric.extension.impl

import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.fabric.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.fabric.extension.McFabricLoaderExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import javax.inject.Inject

internal abstract class McFabricLoaderExtensionImpl @Inject constructor(
    private val project: Project,
    private val accessWideners: NamedDomainObjectProvider<ResolvableConfiguration>,
) : McFabricLoaderExtension {

    override fun metadata(action: Action<in MetadataConfiguration>) {
        MetadataConfiguration.apply(project, action)
    }

    override fun access(action: Action<in AccessConfiguration>) {
        AccessConfiguration.apply(project, action, accessWideners)
    }
}
