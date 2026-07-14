package dev.alexcawl.mcmultiloader.neoforge

import dev.alexcawl.mcmultiloader.neoforge.configuration.configureMmlImplementation
import dev.alexcawl.mcmultiloader.neoforge.configuration.configureMmlRuntimeClasspath
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
        target.configureMmlImplementation(mmlImplementation)
        val mmlRuntimeClasspath = target.mmlRuntimeClasspath(mmlImplementation)
        target.configureMmlRuntimeClasspath(mmlRuntimeClasspath)
        val neoForgeAccessTransformerClasspath = target.neoForgeAccessTransformerClasspath(mmlImplementation)
        val extension = target.objects.newInstance<McNeoForgeLoaderExtensionImpl>(neoForgeAccessTransformerClasspath)
        target.extensions.add<McNeoForgeLoaderExtension>(EXTENSION_NAME, extension)
    }

    companion object {

        const val EXTENSION_NAME = "mcNeoForgeLoader"
    }
}
