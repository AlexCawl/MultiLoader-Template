package dev.alexcawl.mcmultiloader.common.extension.impl

import dev.alexcawl.mcmultiloader.common.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.common.extension.McCommonLoaderExtension
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import org.gradle.api.Action
import javax.inject.Inject

internal abstract class McCommonLoaderExtensionImpl @Inject constructor(
    private val metadataConfiguration: MetadataConfiguration,
    private val accessConfiguration: AccessConfiguration,
) : McCommonLoaderExtension {

    override fun metadata(action: Action<in MetadataConfiguration>) {
        action.execute(metadataConfiguration)
    }

    override fun access(action: Action<in AccessConfiguration>) {
        action.execute(accessConfiguration)
    }
}
