package dev.alexcawl.mcmultiloader.fabric.access.impl

import dev.alexcawl.mcmultiloader.fabric.access.AccessFeature
import dev.alexcawl.mcmultiloader.fabric.access.impl.AccessConstants.TASK_GROUP
import dev.alexcawl.mcmultiloader.fabric.access.impl.AccessConstants.VALIDATION_MARKER
import dev.alexcawl.mcmultiloader.fabric.access.impl.AccessConstants.VALIDATION_TASK_NAME
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.bootstrap.LoomGradlePluginBootstrap
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.newInstance
import org.gradle.language.base.plugins.LifecycleBasePlugin
import javax.inject.Inject

internal abstract class AccessFeatureImpl @Inject constructor(
    private val project: Project,
    objects: ObjectFactory,
) : AccessFeature {

    override val configuration: AccessConfigurationImpl = objects.newInstance()

    override fun install(
        artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        accessWideners: NamedDomainObjectProvider<ResolvableConfiguration>,
    ) {
        with(configuration) {
            project.configureAccess(fabricAccessWidener, artifacts, directArtifacts, accessWideners)
        }
    }

    private fun Project.configureAccess(
        fabricAccessWidener: Provider<RegularFile>,
        artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        accessWideners: NamedDomainObjectProvider<ResolvableConfiguration>,
    ) {
        val validation = registerAccessWidenerValidation(
            fabricAccessWidener,
            artifacts,
            directArtifacts,
            accessWideners,
        )

        plugins.withType(JavaPlugin::class.java).configureEach {
            tasks.named(JavaPlugin.CLASSES_TASK_NAME) {
                dependsOn(validation)
            }
            tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
                dependsOn(validation)
            }
        }
        plugins.withType(LoomGradlePluginBootstrap::class.java).configureEach {
            val loom = extensions.getByType(LoomGradleExtensionAPI::class.java)
            loom.mixin.useLegacyMixinAp.set(false)
            loom.accessWidenerPath.set(fabricAccessWidener)
        }
    }

    private fun Project.registerAccessWidenerValidation(
        fabricAccessWidener: Provider<RegularFile>,
        artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        accessWideners: NamedDomainObjectProvider<ResolvableConfiguration>,
    ): TaskProvider<ValidateFabricAccessWidenerTask> = tasks.register(
        VALIDATION_TASK_NAME,
        ValidateFabricAccessWidenerTask::class.java,
    ) {
        group = TASK_GROUP
        loaderAccessWidener.set(fabricAccessWidener)
        this.accessWideners.from(accessWideners.lenientArtifactFiles())
        this.artifacts.from(artifacts)
        this.directArtifacts.from(directArtifacts)
        validationMarker.set(layout.buildDirectory.file(VALIDATION_MARKER))
    }

    private fun NamedDomainObjectProvider<ResolvableConfiguration>.lenientArtifactFiles(): Provider<FileCollection> =
        map { configuration ->
            configuration.incoming.artifactView {
                isLenient = true
            }.files
        }
}
