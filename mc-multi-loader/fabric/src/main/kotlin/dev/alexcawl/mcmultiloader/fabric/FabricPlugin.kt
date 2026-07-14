package dev.alexcawl.mcmultiloader.fabric

import dev.alexcawl.mcmultiloader.fabric.configuration.configureMmlImplementation
import dev.alexcawl.mcmultiloader.fabric.configuration.configureMmlRuntimeClasspath
import dev.alexcawl.mcmultiloader.fabric.configuration.fabricAccessWidenerClasspath
import dev.alexcawl.mcmultiloader.fabric.configuration.mmlImplementation
import dev.alexcawl.mcmultiloader.fabric.configuration.mmlRuntimeClasspath
import dev.alexcawl.mcmultiloader.fabric.extension.McFabricLoaderExtension
import dev.alexcawl.mcmultiloader.fabric.extension.impl.McFabricLoaderExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class FabricPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val mmlImplementation = target.mmlImplementation()
        target.configureMmlImplementation(mmlImplementation)
        val mmlRuntimeClasspath = target.mmlRuntimeClasspath(mmlImplementation)
        target.configureMmlRuntimeClasspath(mmlRuntimeClasspath)
        val fabricAccessWidenerClasspath = target.fabricAccessWidenerClasspath(mmlImplementation)
        val extension = target.objects.newInstance<McFabricLoaderExtensionImpl>(fabricAccessWidenerClasspath)
        target.extensions.add<McFabricLoaderExtension>(EXTENSION_NAME, extension)
    }

    companion object {

        const val EXTENSION_NAME = "mcFabricLoader"

        const val VALIDATE_FABRIC_ACCESS_WIDENER_TASK_GROUP = "mc-multi-loader"

        const val VALIDATE_FABRIC_ACCESS_WIDENER_TASK_NAME = "validateFabricAccessWidener"
    }
}
