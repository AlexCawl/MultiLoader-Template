package dev.alexcawl.mcmultiloader.feature

import dev.alexcawl.mcmultiloader.extension.McMultiLoaderExtension
import dev.alexcawl.mcmultiloader.extension.impl.McMultiLoaderExtensionImpl
import org.gradle.api.Project

const val MC_MULTI_LOADER_EXTENSION_NAME = "mcMultiLoader"

internal fun Project.registerMcMultiLoaderExtension(): McMultiLoaderExtensionImpl {
    return (extensions.findByType(McMultiLoaderExtension::class.java) as? McMultiLoaderExtensionImpl)
        ?: objects.newInstance(McMultiLoaderExtensionImpl::class.java).also { extension ->
            extensions.add(McMultiLoaderExtension::class.java, MC_MULTI_LOADER_EXTENSION_NAME, extension)
        }
}
