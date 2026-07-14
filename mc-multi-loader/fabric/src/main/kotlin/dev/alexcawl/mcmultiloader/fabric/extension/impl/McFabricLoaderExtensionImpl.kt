package dev.alexcawl.mcmultiloader.fabric.extension.impl

import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.fabric.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.fabric.extension.McFabricLoaderExtension
import org.gradle.api.Action
import org.gradle.api.Project
import javax.inject.Inject

internal abstract class McFabricLoaderExtensionImpl @Inject constructor(
    private val project: Project,
    private val accessConfiguration: AccessConfiguration,
) : McFabricLoaderExtension {

    override fun metadata(action: Action<in MetadataConfiguration>) {
        MetadataConfiguration.apply(project, action)
    }

    override fun access(action: Action<in AccessConfiguration>) {
        action.execute(accessConfiguration)
    }
}
