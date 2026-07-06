package dev.alexcawl.mcmultiloader

import net.neoforged.moddevgradle.boot.ModDevPlugin
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

        target.plugins.withType(ModDevPlugin::class.java).configureEach {
            target.extensions.getByType(NeoForgeExtension::class.java)
                .accessTransformers.from(accessTransformer)
        }
    }
}
