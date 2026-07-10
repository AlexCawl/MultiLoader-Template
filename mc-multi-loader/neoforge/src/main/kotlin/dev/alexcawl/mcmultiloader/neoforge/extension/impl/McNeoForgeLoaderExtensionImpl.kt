package dev.alexcawl.mcmultiloader.neoforge.extension.impl

import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.neoforge.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.neoforge.extension.McNeoForgeLoaderExtension
import org.gradle.api.Action
import javax.inject.Inject

internal abstract class McNeoForgeLoaderExtensionImpl @Inject constructor(
    private val metadataConfiguration: MetadataConfiguration,
    private val accessConfiguration: AccessConfiguration,
) : McNeoForgeLoaderExtension {

    override fun metadata(action: Action<in MetadataConfiguration>) {
        action.execute(metadataConfiguration)
    }

    override fun access(action: Action<in AccessConfiguration>) {
        action.execute(accessConfiguration)
    }
}
