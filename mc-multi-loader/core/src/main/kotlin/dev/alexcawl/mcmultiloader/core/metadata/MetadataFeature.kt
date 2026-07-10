package dev.alexcawl.mcmultiloader.core.metadata

import dev.alexcawl.mcmultiloader.core.metadata.impl.MetadataFeatureImpl
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance

interface MetadataFeature {

    val configuration: MetadataConfiguration

    fun install()

    companion object {
        fun create(project: Project): MetadataFeature {
            return project.objects.newInstance<MetadataFeatureImpl>()
        }
    }
}


