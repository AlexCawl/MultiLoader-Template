package dev.alexcawl.mcmultiloader.fabric.extension

import dev.alexcawl.mcmultiloader.core.extension.AccessExtension
import org.gradle.api.Action

@McFabricLoaderDsl
interface McFabricLoaderExtension {

    fun access(action: Action<in AccessExtension>)
}
