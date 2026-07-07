package dev.alexcawl.mcmultiloader

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar

class CommonPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.createBaseExtension()
        val descriptor = target.tasks.register(
            "generateMcMultiLoaderDescriptor",
            GenerateCommonDescriptorTask::class.java
        ) {
            group = "mc-multi-loader"
            fabricAccessWidener.set(extension.accessExtension.fabricAccessWidenerPath)
            neoForgeAccessTransformer.set(extension.accessExtension.neoForgeAccessTransformerPath)
            outputDirectory.set(target.layout.buildDirectory.dir("generated/mc-multi-loader/descriptor"))
        }
        target.plugins.withType(JavaPlugin::class.java).configureEach {
            target.afterEvaluate {
                if (extension.accessExtension.fabricAccessWidenerPath.isPresent) {
                    target.createAccessFileElements(
                        "mcMultiLoaderFabricAccessWidenerElements",
                        "mc-multi-loader-fabric-aw",
                        extension.accessExtension.fabricAccessWidenerPath
                    )
                }
                if (extension.accessExtension.neoForgeAccessTransformerPath.isPresent) {
                    target.createAccessFileElements(
                        "mcMultiLoaderNeoForgeAccessTransformerElements",
                        "mc-multi-loader-neoforge-at",
                        extension.accessExtension.neoForgeAccessTransformerPath
                    )
                }
            }
            target.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java) {
                from(descriptor.flatMap { it.outputDirectory })
            }
        }
    }
}
