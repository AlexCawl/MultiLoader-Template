package dev.alexcawl.mcmultiloader.fabric.extension

import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.fabric.access.AccessConfiguration
import org.gradle.api.Action

@McFabricLoaderDsl
interface McFabricLoaderExtension {

    fun access(action: Action<in AccessConfiguration>)

    fun metadata(action: Action<in MetadataConfiguration>)
}
