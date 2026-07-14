package dev.alexcawl.mcmultiloader.fabric.access.impl

import dev.alexcawl.mcmultiloader.fabric.FabricPlugin.Companion.VALIDATE_FABRIC_ACCESS_WIDENER_TASK_GROUP
import dev.alexcawl.mcmultiloader.fabric.FabricPlugin.Companion.VALIDATE_FABRIC_ACCESS_WIDENER_TASK_NAME
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.bootstrap.LoomGradlePluginBootstrap
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.language.base.plugins.LifecycleBasePlugin

private const val ACCESS_WIDENER_ARTIFACT_TYPE = "access-widener"

internal fun Project.accessFeature(
    fabricAccessWidener: RegularFileProperty,
    fabricAccessWidenerClasspath: NamedDomainObjectProvider<ResolvableConfiguration>,
) {
    val validateFabricAccessWidener = validateFabricAccessWidener(fabricAccessWidener, fabricAccessWidenerClasspath)
    plugins.withType<JavaPlugin> {
        tasks.named(JavaPlugin.CLASSES_TASK_NAME) {
            dependsOn(validateFabricAccessWidener)
        }
        tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
            dependsOn(validateFabricAccessWidener)
        }
    }
    plugins.withType<LoomGradlePluginBootstrap> {
        val loomExtension = extensions.getByType<LoomGradleExtensionAPI>()
        loomExtension.mixin.useLegacyMixinAp.set(false)
        loomExtension.accessWidenerPath.set(fabricAccessWidener)
    }
}

private fun Project.validateFabricAccessWidener(
    fabricAccessWidener: Provider<RegularFile>,
    fabricAccessWidenerClasspath: NamedDomainObjectProvider<ResolvableConfiguration>,
): TaskProvider<ValidateFabricAccessWidenerTask> {
    return tasks.register<ValidateFabricAccessWidenerTask>(VALIDATE_FABRIC_ACCESS_WIDENER_TASK_NAME) {
        group = VALIDATE_FABRIC_ACCESS_WIDENER_TASK_GROUP
        loaderAccessWidener.set(fabricAccessWidener)
        accessWideners.from(fabricAccessWidenerClasspath.accessWideners())
    }
}

private fun NamedDomainObjectProvider<ResolvableConfiguration>.accessWideners(): Provider<FileCollection> {
    return map { configuration: ResolvableConfiguration ->
        val accessWideners: ArtifactView = configuration.incoming.artifactView {
            isLenient = true
            attributes {
                attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ACCESS_WIDENER_ARTIFACT_TYPE)
            }
        }
        accessWideners.files
    }
}
