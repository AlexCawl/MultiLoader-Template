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
            fabricAccessWidener.set(extension.fabricAccessWidener)
            neoForgeAccessTransformer.set(extension.neoForgeAccessTransformer)
            outputDirectory.set(target.layout.buildDirectory.dir("generated/mc-multi-loader/descriptor"))
        }
        target.plugins.withType(JavaPlugin::class.java).configureEach {
            target.afterEvaluate {
                if (extension.fabricAccessWidener.isPresent) {
                    target.createAccessFileElements(
                        "mcMultiLoaderFabricAccessWidenerElements",
                        "mc-multi-loader-fabric-aw",
                        extension.fabricAccessWidener
                    )
                }
                if (extension.neoForgeAccessTransformer.isPresent) {
                    target.createAccessFileElements(
                        "mcMultiLoaderNeoForgeAccessTransformerElements",
                        "mc-multi-loader-neoforge-at",
                        extension.neoForgeAccessTransformer
                    )
                }
            }
            target.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java) {
                from(descriptor.flatMap { it.outputDirectory })
            }
        }
    }
}
