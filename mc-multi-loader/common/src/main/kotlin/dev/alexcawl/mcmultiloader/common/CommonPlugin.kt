package dev.alexcawl.mcmultiloader.common

import dev.alexcawl.mcmultiloader.core.feature.configureCommonMcMultiLoaderPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class CommonPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureCommonMcMultiLoaderPlugin()
    }
}
