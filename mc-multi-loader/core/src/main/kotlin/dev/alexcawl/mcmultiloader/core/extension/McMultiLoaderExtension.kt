package dev.alexcawl.mcmultiloader.core.extension

import org.gradle.api.Action

@McMultiLoaderDsl
interface McMultiLoaderExtension {

    fun metadata(action: Action<in MetadataExtension>)

    fun access(action: Action<in AccessExtension>)
}
