package dev.alexcawl.mcmultiloader.neoforge.extension

import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.neoforge.access.AccessConfiguration
import org.gradle.api.Action

@McNeoForgeLoaderDsl
interface McNeoForgeLoaderExtension {

    fun metadata(action: Action<in MetadataConfiguration>)

    fun access(action: Action<in AccessConfiguration>)
}
