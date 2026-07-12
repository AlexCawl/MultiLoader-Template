package dev.alexcawl.mcmultiloader.common

import dev.alexcawl.mcmultiloader.common.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.common.extension.McCommonLoaderExtension
import dev.alexcawl.mcmultiloader.common.extension.impl.McCommonLoaderExtensionImpl
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class CommonPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val metadataConfiguration = MetadataConfiguration.create(target)
        val accessConfiguration = AccessConfiguration.create(target)
        val extension = target.objects.newInstance<McCommonLoaderExtensionImpl>(metadataConfiguration, accessConfiguration)
        target.extensions.add<McCommonLoaderExtension>(EXTENSION_NAME, extension)
    }

    companion object {

        const val EXTENSION_NAME = "mcCommonLoader"
    }
}
