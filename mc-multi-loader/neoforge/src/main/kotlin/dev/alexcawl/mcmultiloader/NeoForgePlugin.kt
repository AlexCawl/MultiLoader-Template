package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.feature.ConsumerModel
import dev.alexcawl.mcmultiloader.feature.configureMcMultiLoaderPlugin
import dev.alexcawl.mcmultiloader.feature.configureMerged
import dev.alexcawl.mcmultiloader.task.ExtractNeoForgeAccessTransformersTask
import net.neoforged.moddevgradle.boot.ModDevPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

private const val ACCESS_TASK_GROUP = "mc-multi-loader"
private const val EXTRACT_NEOFORGE_ACCESS_TRANSFORMERS_TASK = "extractMcMultiLoaderNeoForgeAccessTransformers"
private const val NEOFORGE_ACCESS_TRANSFORMER_OUTPUT = "mc-multi-loader/access/neoforge"

class NeoForgePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureMcMultiLoaderPlugin {
            val model = configureMerged()
            val accessTransformers = registerNeoForgeAccessTransformers(model)

            plugins.withType(ModDevPlugin::class.java).configureEach {
                extensions.getByType(NeoForgeExtension::class.java)
                    .accessTransformers.from(accessTransformers.map { fileTree(it.outputDirectory) })
            }
        }
    }
}

private fun Project.registerNeoForgeAccessTransformers(
    model: ConsumerModel,
): TaskProvider<ExtractNeoForgeAccessTransformersTask> = tasks.register(
    EXTRACT_NEOFORGE_ACCESS_TRANSFORMERS_TASK,
    ExtractNeoForgeAccessTransformersTask::class.java,
) {
    group = ACCESS_TASK_GROUP
    artifacts.from(model.mergedArtifact)
    directArtifacts.from(model.directMergedArtifact)
    outputDirectory.set(layout.buildDirectory.dir(NEOFORGE_ACCESS_TRANSFORMER_OUTPUT))
}
