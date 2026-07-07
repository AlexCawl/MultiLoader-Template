package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.access.AccessExtension
import dev.alexcawl.mcmultiloader.metadata.MetadataExtension
import org.gradle.api.Action

interface McMultiLoaderExtension {
    fun metadata(action: Action<in MetadataExtension>)

    fun access(action: Action<in AccessExtension>)
}

