package dev.alexcawl.mcmultiloader.common

import dev.alexcawl.mcmultiloader.common.access.AccessFeature
import dev.alexcawl.mcmultiloader.common.extension.McCommonLoaderExtension
import dev.alexcawl.mcmultiloader.common.extension.impl.McCommonLoaderExtensionImpl
import dev.alexcawl.mcmultiloader.core.metadata.MetadataFeature
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class CommonPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val metadataFeature = MetadataFeature.create(target)
        val accessFeature = AccessFeature.create(target)
        val extension = target.objects.newInstance<McCommonLoaderExtensionImpl>(metadataFeature.configuration, accessFeature.configuration)
        target.extensions.add<McCommonLoaderExtension>(EXTENSION_NAME, extension)
        metadataFeature.install()
        accessFeature.install()
    }

    companion object {

        const val EXTENSION_NAME = "mcCommonLoader"
    }
}
