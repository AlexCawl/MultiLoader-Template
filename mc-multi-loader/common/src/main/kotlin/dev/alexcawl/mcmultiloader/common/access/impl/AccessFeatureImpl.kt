package dev.alexcawl.mcmultiloader.common.access.impl

import dev.alexcawl.mcmultiloader.common.access.AccessFeature
import dev.alexcawl.mcmultiloader.common.access.impl.AccessConstants.DESCRIPTOR_OUTPUT_DIRECTORY
import dev.alexcawl.mcmultiloader.common.access.impl.AccessConstants.DESCRIPTOR_TASK_GROUP
import dev.alexcawl.mcmultiloader.common.access.impl.AccessConstants.DESCRIPTOR_TASK_NAME
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.withType
import javax.inject.Inject

internal abstract class AccessFeatureImpl @Inject constructor(
    private val project: Project,
    objects: ObjectFactory,
) : AccessFeature {

    override val configuration: AccessConfigurationImpl = objects.newInstance()

    override fun install() {
        with(project) {
            plugins.withType<JavaPlugin> {
                with(configuration) {
                    configureCommonAccess(fabricAccessWidener, neoForgeAccessTransformer)
                }
            }
        }
    }

    private fun Project.configureCommonAccess(
        fabricAccessWidener: Provider<RegularFile>,
        neoForgeAccessTransformer: Provider<RegularFile>,
    ) {
        val descriptor = tasks.register(
            DESCRIPTOR_TASK_NAME,
            GenerateCommonDescriptorTask::class.java,
        ) {
            group = DESCRIPTOR_TASK_GROUP
            this.fabricAccessWidener.set(fabricAccessWidener)
            this.neoForgeAccessTransformer.set(neoForgeAccessTransformer)
            outputDirectory.set(layout.buildDirectory.dir(DESCRIPTOR_OUTPUT_DIRECTORY))
        }

        tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java) {
            from(descriptor.flatMap { it.outputDirectory })
        }
    }
}
