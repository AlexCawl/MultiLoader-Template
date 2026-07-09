package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.extension.impl.McMultiLoaderExtensionImpl
import dev.alexcawl.mcmultiloader.feature.configureAccess
import dev.alexcawl.mcmultiloader.feature.configureMcMultiLoaderPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class CommonPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureMcMultiLoaderPlugin { extension ->
            configureAccess((extension as McMultiLoaderExtensionImpl).accessExtension)
        }
    }
}
