package dev.alexcawl.mcmultiloader.neoforge

import dev.alexcawl.mcmultiloader.core.configuration.configureMergedDependencies
import dev.alexcawl.mcmultiloader.core.metadata.MetadataFeature
import dev.alexcawl.mcmultiloader.neoforge.access.AccessFeature
import dev.alexcawl.mcmultiloader.neoforge.extension.McNeoForgeLoaderExtension
import dev.alexcawl.mcmultiloader.neoforge.extension.impl.McNeoForgeLoaderExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class NeoForgePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val metadataFeature = MetadataFeature.create(target)
        val accessFeature = AccessFeature.create(target)
        val mergedDependencies = target.configureMergedDependencies()
        val extension = target.objects.newInstance<McNeoForgeLoaderExtensionImpl>(
            metadataFeature.configuration,
            accessFeature.configuration,
        )
        target.extensions.add<McNeoForgeLoaderExtension>(EXTENSION_NAME, extension)
        metadataFeature.install()
        accessFeature.install(mergedDependencies.artifacts, mergedDependencies.directArtifacts)
    }

    companion object {

        const val EXTENSION_NAME = "mcNeoForgeLoader"
    }
}
