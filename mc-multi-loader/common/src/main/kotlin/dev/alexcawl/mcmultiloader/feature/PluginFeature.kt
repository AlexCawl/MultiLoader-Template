package dev.alexcawl.mcmultiloader.feature

import dev.alexcawl.mcmultiloader.extension.McMultiLoaderExtension
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
