package dev.alexcawl.mcmultiloader.feature

import dev.alexcawl.mcmultiloader.extension.impl.AccessExtensionImpl
import dev.alexcawl.mcmultiloader.task.GenerateCommonDescriptorTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar

const val DESCRIPTOR_PATH = "META-INF/mc-multi-loader/common.properties"
const val FABRIC_ACCESS_WIDENER_PROPERTY = "fabricAccessWidener"
const val NEOFORGE_ACCESS_TRANSFORMER_PROPERTY = "neoForgeAccessTransformer"

internal const val MAIN_RESOURCES_DIRECTORY = "src/main/resources"
private const val DESCRIPTOR_TASK_NAME = "generateMcMultiLoaderDescriptor"
private const val DESCRIPTOR_TASK_GROUP = "mc-multi-loader"
private const val DESCRIPTOR_OUTPUT_DIRECTORY = "generated/mc-multi-loader/descriptor"

internal fun Project.configureAccess(extension: AccessExtensionImpl) {
    val descriptor = tasks.register(DESCRIPTOR_TASK_NAME, GenerateCommonDescriptorTask::class.java) {
        group = DESCRIPTOR_TASK_GROUP
        fabricAccessWidener.set(extension.fabricAccessWidener)
        neoForgeAccessTransformer.set(extension.neoForgeAccessTransformer)
        outputDirectory.set(layout.buildDirectory.dir(DESCRIPTOR_OUTPUT_DIRECTORY))
    }

    plugins.withType(JavaPlugin::class.java).configureEach {
        tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java) {
            from(descriptor.flatMap { it.outputDirectory })
        }
    }
}
