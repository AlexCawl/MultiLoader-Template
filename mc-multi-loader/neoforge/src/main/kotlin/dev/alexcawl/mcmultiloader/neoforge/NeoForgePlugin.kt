package dev.alexcawl.mcmultiloader.neoforge

import dev.alexcawl.mcmultiloader.core.configuration.configureMergedDependencies
import dev.alexcawl.mcmultiloader.core.configuration.configureNeoForgeAccessTransformerClasspath
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.neoforge.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.neoforge.extension.McNeoForgeLoaderExtension
import dev.alexcawl.mcmultiloader.neoforge.extension.impl.McNeoForgeLoaderExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class NeoForgePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val metadataConfiguration: MetadataConfiguration = MetadataConfiguration.create(target)
        val mergedDependencies = target.configureMergedDependencies()
        val accessTransformers = target.configureNeoForgeAccessTransformerClasspath(mergedDependencies.dependencies)
        val accessConfiguration = AccessConfiguration.create(
            target,
            accessTransformers,
        )
        val extension = target.objects.newInstance<McNeoForgeLoaderExtensionImpl>(
            metadataConfiguration,
            accessConfiguration,
        )
        target.extensions.add<McNeoForgeLoaderExtension>(EXTENSION_NAME, extension)
    }

    companion object {

        const val EXTENSION_NAME = "mcNeoForgeLoader"
    }
}
