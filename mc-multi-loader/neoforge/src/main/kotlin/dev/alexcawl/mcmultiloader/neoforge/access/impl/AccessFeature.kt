package dev.alexcawl.mcmultiloader.neoforge.access.impl

import net.neoforged.moddevgradle.boot.ModDevPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

private const val ACCESS_TRANSFORMER_ARTIFACT_TYPE = "access-transformer"

internal fun Project.accessFeature(
    accessTransformer: RegularFileProperty,
    neoForgeAccessTransformerClasspath: NamedDomainObjectProvider<ResolvableConfiguration>,
) {
    plugins.withType<ModDevPlugin> {
        val neoForgeExtension = extensions.getByType<NeoForgeExtension>()
        neoForgeExtension.accessTransformers.from(accessTransformer, neoForgeAccessTransformerClasspath.accessTransformers())
    }
}

private fun NamedDomainObjectProvider<ResolvableConfiguration>.accessTransformers(): Provider<FileCollection> {
    return map { configuration: ResolvableConfiguration ->
        val accessTransformers: ArtifactView = configuration.incoming.artifactView {
            isLenient = true
            attributes {
                attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ACCESS_TRANSFORMER_ARTIFACT_TYPE)
            }
        }
        accessTransformers.files
    }
}
