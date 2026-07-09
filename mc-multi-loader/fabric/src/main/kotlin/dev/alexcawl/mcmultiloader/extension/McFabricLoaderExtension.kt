package dev.alexcawl.mcmultiloader.extension

import org.gradle.api.Action

@McFabricLoaderDsl
interface McFabricLoaderExtension {

    fun access(action: Action<in AccessExtension>)
}
