package dev.alexcawl.mcmultiloader.core.extension.impl

import dev.alexcawl.mcmultiloader.core.extension.AccessExtension
import dev.alexcawl.mcmultiloader.core.extension.McMultiLoaderExtension
import dev.alexcawl.mcmultiloader.core.extension.MetadataExtension
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

internal abstract class McMultiLoaderExtensionImpl @Inject constructor(
    objects: ObjectFactory,
) : McMultiLoaderExtension {

    internal val metadataExtension: MetadataExtensionImpl = objects.newInstance(MetadataExtensionImpl::class)

    internal val accessExtension: AccessExtensionImpl = objects.newInstance(AccessExtensionImpl::class)

    override fun metadata(action: Action<in MetadataExtension>) {
        action.execute(metadataExtension)
    }

    override fun access(action: Action<in AccessExtension>) {
        action.execute(accessExtension)
    }
}