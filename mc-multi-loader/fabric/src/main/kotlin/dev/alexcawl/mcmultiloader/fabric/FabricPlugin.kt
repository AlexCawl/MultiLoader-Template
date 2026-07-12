package dev.alexcawl.mcmultiloader.fabric

import dev.alexcawl.mcmultiloader.core.configuration.configureFabricAccessWidenerClasspath
import dev.alexcawl.mcmultiloader.core.configuration.configureMergedDependencies
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.fabric.access.AccessFeature
import dev.alexcawl.mcmultiloader.fabric.extension.McFabricLoaderExtension
import dev.alexcawl.mcmultiloader.fabric.extension.impl.McFabricLoaderExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class FabricPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val metadataConfiguration = MetadataConfiguration.create(target)
        val accessFeature = AccessFeature.create(target)
        val mergedDependencies = target.configureMergedDependencies()
        val accessWideners = target.configureFabricAccessWidenerClasspath(mergedDependencies.dependencies)
        val extension = target.objects.newInstance<McFabricLoaderExtensionImpl>(
            metadataConfiguration,
            accessFeature.configuration,
        )
        target.extensions.add<McFabricLoaderExtension>(EXTENSION_NAME, extension)
        accessFeature.install(mergedDependencies.artifacts, mergedDependencies.directArtifacts, accessWideners)
    }

    companion object {

        const val EXTENSION_NAME = "mcFabricLoader"
    }
}
