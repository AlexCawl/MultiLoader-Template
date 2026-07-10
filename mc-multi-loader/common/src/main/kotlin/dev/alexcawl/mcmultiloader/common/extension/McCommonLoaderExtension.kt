package dev.alexcawl.mcmultiloader.common.extension

import dev.alexcawl.mcmultiloader.common.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import org.gradle.api.Action

@McCommonLoaderDsl
interface McCommonLoaderExtension {

    fun metadata(action: Action<in MetadataConfiguration>)

    fun access(action: Action<in AccessConfiguration>)
}
