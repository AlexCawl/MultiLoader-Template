package dev.alexcawl.mcmultiloader.core.feature

import dev.alexcawl.mcmultiloader.core.extension.McMultiLoaderExtension
import dev.alexcawl.mcmultiloader.core.extension.impl.McMultiLoaderExtensionImpl
import org.gradle.api.Project

fun Project.configureMcMultiLoaderPlugin(
    configureProject: Project.(McMultiLoaderExtension) -> Unit = {},
) {
    val extension = registerMcMultiLoaderExtension()
    with(extension.metadataExtension) {
        configureMetadata(this@configureMcMultiLoaderPlugin, resourcesExpands, jarManifestAttributes)
    }
    configureProject(extension)
}

fun Project.configureCommonMcMultiLoaderPlugin() {
    configureMcMultiLoaderPlugin { extension ->
        configureAccess((extension as McMultiLoaderExtensionImpl).accessExtension)
    }
}
