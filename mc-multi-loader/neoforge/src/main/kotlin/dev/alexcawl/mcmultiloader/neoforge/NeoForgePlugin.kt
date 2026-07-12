package dev.alexcawl.mcmultiloader.neoforge

import dev.alexcawl.mcmultiloader.core.configuration.configureMergedDependencies
import dev.alexcawl.mcmultiloader.core.configuration.configureNeoForgeAccessTransformerClasspath
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.neoforge.access.AccessFeature
import dev.alexcawl.mcmultiloader.neoforge.extension.McNeoForgeLoaderExtension
import dev.alexcawl.mcmultiloader.neoforge.extension.impl.McNeoForgeLoaderExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class NeoForgePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val metadataConfiguration: MetadataConfiguration = MetadataConfiguration.create(target)
        val accessFeature = AccessFeature.create(target)
        val mergedDependencies = target.configureMergedDependencies()
        val accessTransformers = target.configureNeoForgeAccessTransformerClasspath(mergedDependencies.dependencies)
        val extension = target.objects.newInstance<McNeoForgeLoaderExtensionImpl>(
            metadataConfiguration,
            accessFeature.configuration,
        )
        target.extensions.add<McNeoForgeLoaderExtension>(EXTENSION_NAME, extension)
        accessFeature.install(mergedDependencies.artifacts, mergedDependencies.directArtifacts, accessTransformers)
    }

    companion object {

        const val EXTENSION_NAME = "mcNeoForgeLoader"
    }
}
