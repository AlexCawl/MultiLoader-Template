package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.feature.ConsumerModel
import dev.alexcawl.mcmultiloader.feature.configureMcMultiLoaderPlugin
import dev.alexcawl.mcmultiloader.feature.configureMerged
import dev.alexcawl.mcmultiloader.feature.registerMcFabricLoaderExtension
import dev.alexcawl.mcmultiloader.extension.impl.McFabricLoaderExtensionImpl
import dev.alexcawl.mcmultiloader.task.ValidateFabricAccessWidenerTask
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.bootstrap.LoomGradlePluginBootstrap
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin

private const val ACCESS_TASK_GROUP = "mc-multi-loader"
private const val VALIDATE_FABRIC_ACCESS_WIDENER_TASK = "validateMcMultiLoaderFabricAccessWidener"
private const val FABRIC_ACCESS_WIDENER_VALIDATION_MARKER = "mc-multi-loader/access/fabric/validated.marker"

class FabricPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val fabricExtension = target.registerMcFabricLoaderExtension()
        target.configureMcMultiLoaderPlugin {
            val model = configureMerged()
            registerFabricAccessWidenerValidation(model, fabricExtension)

            plugins.withType(LoomGradlePluginBootstrap::class.java).configureEach {
                val loom = extensions.getByType(LoomGradleExtensionAPI::class.java)
                loom.mixin.useLegacyMixinAp.set(false)
                loom.accessWidenerPath.set(fabricExtension.accessExtension.fabricAccessWidener)
            }
        }
    }
}

private fun Project.registerFabricAccessWidenerValidation(
    model: ConsumerModel,
    extension: McFabricLoaderExtensionImpl,
): TaskProvider<ValidateFabricAccessWidenerTask> {
    val validation = tasks.register(
        VALIDATE_FABRIC_ACCESS_WIDENER_TASK,
        ValidateFabricAccessWidenerTask::class.java,
    ) {
        group = ACCESS_TASK_GROUP
        loaderAccessWidener.set(extension.accessExtension.fabricAccessWidener)
        artifacts.from(model.mergedArtifact)
        directArtifacts.from(model.directMergedArtifact)
        validationMarker.set(layout.buildDirectory.file(FABRIC_ACCESS_WIDENER_VALIDATION_MARKER))
    }

    plugins.withType(JavaPlugin::class.java).configureEach {
        tasks.named(JavaPlugin.CLASSES_TASK_NAME) {
            dependsOn(validation)
        }
        tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
            dependsOn(validation)
        }
    }

    return validation
}
