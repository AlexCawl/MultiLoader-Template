package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.access.AccessExtension
import dev.alexcawl.mcmultiloader.access.DefaultAccessExtension
import dev.alexcawl.mcmultiloader.metadata.DefaultMetadataExtension
import dev.alexcawl.mcmultiloader.metadata.MetadataExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

internal abstract class DefaultMcMultiLoaderExtension @Inject constructor(
    project: Project,
    objects: ObjectFactory
) : McMultiLoaderExtension {
    private val metadataExtension: MetadataExtension = objects.newInstance(DefaultMetadataExtension::class, project)
    internal val accessExtension: DefaultAccessExtension = objects.newInstance(DefaultAccessExtension::class)

    override fun metadata(action: Action<in MetadataExtension>) {
        action.execute(metadataExtension)
    }

    override fun access(action: Action<in AccessExtension>) {
        action.execute(accessExtension)
    }
}
