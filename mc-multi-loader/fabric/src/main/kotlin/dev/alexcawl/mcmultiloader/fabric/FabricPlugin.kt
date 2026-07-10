package dev.alexcawl.mcmultiloader.fabric

import dev.alexcawl.mcmultiloader.core.configuration.configureMergedDependencies
import dev.alexcawl.mcmultiloader.core.metadata.MetadataFeature
import dev.alexcawl.mcmultiloader.fabric.access.AccessFeature
import dev.alexcawl.mcmultiloader.fabric.extension.McFabricLoaderExtension
import dev.alexcawl.mcmultiloader.fabric.extension.impl.McFabricLoaderExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class FabricPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val metadataFeature = MetadataFeature.create(target)
        val accessFeature = AccessFeature.create(target)
        val mergedDependencies = target.configureMergedDependencies()
        val extension = target.objects.newInstance<McFabricLoaderExtensionImpl>(
            metadataFeature.configuration,
            accessFeature.configuration,
        )
        target.extensions.add<McFabricLoaderExtension>(EXTENSION_NAME, extension)
        metadataFeature.install()
        accessFeature.install(mergedDependencies.artifacts, mergedDependencies.directArtifacts)
    }

    companion object {

        const val EXTENSION_NAME = "mcFabricLoader"
    }
}
