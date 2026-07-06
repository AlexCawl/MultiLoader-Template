package dev.alexcawl.mcmultiloader.metadata

import org.gradle.api.Plugin
import org.gradle.api.Project

class MetadataPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.objects.newInstance(DefaultMetadataExtension::class.java, target)
        target.extensions.add(MetadataExtension::class.java, "mcMultiLoaderMetadata", extension)
    }
}
