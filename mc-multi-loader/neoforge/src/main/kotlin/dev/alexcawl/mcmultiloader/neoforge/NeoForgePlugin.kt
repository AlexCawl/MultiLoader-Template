package dev.alexcawl.mcmultiloader.neoforge

import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.neoforge.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.neoforge.configuration.configureMmlRuntimeEmbedding
import dev.alexcawl.mcmultiloader.neoforge.configuration.mmlImplementation
import dev.alexcawl.mcmultiloader.neoforge.configuration.mmlRuntimeClasspath
import dev.alexcawl.mcmultiloader.neoforge.configuration.neoForgeAccessTransformerClasspath
import dev.alexcawl.mcmultiloader.neoforge.extension.McNeoForgeLoaderExtension
import dev.alexcawl.mcmultiloader.neoforge.extension.impl.McNeoForgeLoaderExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class NeoForgePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val mmlImplementation = target.mmlImplementation()
        val mmlRuntimeClasspath = target.mmlRuntimeClasspath(mmlImplementation.get())
        target.configureMmlRuntimeEmbedding(
            mmlImplementation,
            mmlRuntimeClasspath,
        )
        val accessTransformers = target.neoForgeAccessTransformerClasspath(mmlImplementation)
        val accessConfiguration = AccessConfiguration.create(
            target,
            accessTransformers,
        )
        val extension = target.objects.newInstance<McNeoForgeLoaderExtensionImpl>(accessConfiguration)
        target.extensions.add<McNeoForgeLoaderExtension>(EXTENSION_NAME, extension)
    }

    companion object {

        const val EXTENSION_NAME = "mcNeoForgeLoader"
    }
}
