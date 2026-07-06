package dev.alexcawl.mcmultiloader

import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class NeoForgePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val model = target.createConsumerModel()
        val accessTransformer = target.createAccessFileResolver(
            model,
            "mergedNeoForgeAccessTransformer",
            "mc-multi-loader-neoforge-at"
        )

        target.pluginManager.withPlugin("net.neoforged.moddev") {
            target.extensions.getByType(NeoForgeExtension::class.java)
                .accessTransformers.from(accessTransformer)
        }
    }
}
