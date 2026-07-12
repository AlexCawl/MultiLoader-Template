package dev.alexcawl.mcmultiloader.neoforge.access.impl

import dev.alexcawl.mcmultiloader.neoforge.access.AccessFeature
import dev.alexcawl.mcmultiloader.neoforge.access.impl.AccessConstants.EXTRACTION_OUTPUT_DIRECTORY
import dev.alexcawl.mcmultiloader.neoforge.access.impl.AccessConstants.EXTRACTION_TASK_NAME
import dev.alexcawl.mcmultiloader.neoforge.access.impl.AccessConstants.TASK_GROUP
import net.neoforged.moddevgradle.boot.ModDevPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

internal abstract class AccessFeatureImpl @Inject constructor(
    private val project: Project,
    objects: ObjectFactory,
) : AccessFeature {

    override val configuration: AccessConfigurationImpl = objects.newInstance()

    override fun install(
        artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        accessTransformers: NamedDomainObjectProvider<ResolvableConfiguration>,
    ) {
        with(configuration) {
            project.configureAccess(neoForgeAccessTransformer, artifacts, directArtifacts, accessTransformers)
        }
    }

    private fun Project.configureAccess(
        neoForgeAccessTransformer: Provider<RegularFile>,
        artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        accessTransformers: NamedDomainObjectProvider<ResolvableConfiguration>,
    ) {
        val extraction = registerAccessTransformerExtraction(artifacts, directArtifacts, accessTransformers)
        plugins.withType(ModDevPlugin::class.java).configureEach {
            extensions.getByType(NeoForgeExtension::class.java).accessTransformers.from(
                neoForgeAccessTransformer,
                extraction.map { fileTree(it.outputDirectory) },
            )
        }
    }

    private fun Project.registerAccessTransformerExtraction(
        artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        accessTransformers: NamedDomainObjectProvider<ResolvableConfiguration>,
    ): TaskProvider<ExtractNeoForgeAccessTransformersTask> = tasks.register(
        EXTRACTION_TASK_NAME,
        ExtractNeoForgeAccessTransformersTask::class.java,
    ) {
        group = TASK_GROUP
        this.accessTransformers.from(accessTransformers.lenientArtifactFiles())
        this.artifacts.from(artifacts)
        this.directArtifacts.from(directArtifacts)
        outputDirectory.set(layout.buildDirectory.dir(EXTRACTION_OUTPUT_DIRECTORY))
    }

    private fun NamedDomainObjectProvider<ResolvableConfiguration>.lenientArtifactFiles(): Provider<FileCollection> =
        map { configuration ->
            configuration.incoming.artifactView {
                isLenient = true
            }.files
        }
}
