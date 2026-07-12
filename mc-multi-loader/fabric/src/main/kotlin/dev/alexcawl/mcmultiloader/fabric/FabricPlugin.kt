package dev.alexcawl.mcmultiloader.fabric

import dev.alexcawl.mcmultiloader.core.configuration.configureFabricAccessWidenerClasspath
import dev.alexcawl.mcmultiloader.core.configuration.configureMergedDependencies
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.fabric.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.fabric.extension.McFabricLoaderExtension
import dev.alexcawl.mcmultiloader.fabric.extension.impl.McFabricLoaderExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class FabricPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val metadataConfiguration = MetadataConfiguration.create(target)
        val mergedDependencies = target.configureMergedDependencies()
        val accessWideners = target.configureFabricAccessWidenerClasspath(mergedDependencies.dependencies)
        val accessConfiguration = AccessConfiguration.create(
            target,
            mergedDependencies.artifacts,
            mergedDependencies.directArtifacts,
            accessWideners,
        )
        val extension = target.objects.newInstance<McFabricLoaderExtensionImpl>(
            metadataConfiguration,
            accessConfiguration,
        )
        target.extensions.add<McFabricLoaderExtension>(EXTENSION_NAME, extension)
    }

    companion object {

        const val EXTENSION_NAME = "mcFabricLoader"
    }
}
