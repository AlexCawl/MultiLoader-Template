package dev.alexcawl.mcmultiloader.fabric.extension.impl

import dev.alexcawl.mcmultiloader.core.extension.AccessExtension
import dev.alexcawl.mcmultiloader.fabric.extension.McFabricLoaderExtension
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

internal abstract class McFabricLoaderExtensionImpl @Inject constructor(
    objects: ObjectFactory,
) : McFabricLoaderExtension {

    internal val accessExtension: FabricAccessExtensionImpl = objects.newInstance(FabricAccessExtensionImpl::class)

    override fun access(action: Action<in AccessExtension>) {
        action.execute(accessExtension)
    }
}
