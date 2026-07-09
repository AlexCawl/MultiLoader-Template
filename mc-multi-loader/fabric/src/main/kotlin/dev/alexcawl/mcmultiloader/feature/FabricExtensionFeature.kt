package dev.alexcawl.mcmultiloader.feature

import dev.alexcawl.mcmultiloader.extension.McFabricLoaderExtension
import dev.alexcawl.mcmultiloader.extension.impl.McFabricLoaderExtensionImpl
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance

const val MC_FABRIC_LOADER_EXTENSION_NAME = "mcFabricLoader"

internal fun Project.registerMcFabricLoaderExtension(): McFabricLoaderExtensionImpl =
    (extensions.findByType(McFabricLoaderExtension::class.java) as? McFabricLoaderExtensionImpl)
        ?: objects.newInstance(McFabricLoaderExtensionImpl::class.java).also { extension ->
            extensions.add(McFabricLoaderExtension::class.java, MC_FABRIC_LOADER_EXTENSION_NAME, extension)
        }
