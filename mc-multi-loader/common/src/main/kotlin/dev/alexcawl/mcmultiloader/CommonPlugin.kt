package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.extension.McMultiLoaderExtension
import dev.alexcawl.mcmultiloader.extension.impl.McMultiLoaderExtensionImpl
import dev.alexcawl.mcmultiloader.feature.configureAccess
import dev.alexcawl.mcmultiloader.feature.configureMetadata
import dev.alexcawl.mcmultiloader.feature.registerMcMultiLoaderExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

open class CommonPlugin : Plugin<Project> {
    final override fun apply(target: Project) {
        val extension = target.registerMcMultiLoaderExtension()
        with(extension) {
            with(metadataExtension) {
                configureMetadata(target, resourcesExpands, jarManifestAttributes)
            }
        }
        configureProject(target, extension)
    }

    protected open fun configureProject(target: Project, extension: McMultiLoaderExtension) {
        target.configureAccess((extension as McMultiLoaderExtensionImpl).accessExtension)
    }
}
