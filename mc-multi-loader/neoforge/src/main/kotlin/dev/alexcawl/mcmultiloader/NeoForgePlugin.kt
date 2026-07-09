package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.feature.NEOFORGE_ACCESS_TRANSFORMER_KIND
import dev.alexcawl.mcmultiloader.feature.NEOFORGE_ACCESS_TRANSFORMER_RESOLVER_CONFIGURATION
import dev.alexcawl.mcmultiloader.feature.configureMcMultiLoaderPlugin
import dev.alexcawl.mcmultiloader.feature.configureMerged
import dev.alexcawl.mcmultiloader.feature.createAccessFileResolver
import net.neoforged.moddevgradle.boot.ModDevPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class NeoForgePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureMcMultiLoaderPlugin {
            val model = configureMerged()
            val accessTransformer = createAccessFileResolver(
                model,
                NEOFORGE_ACCESS_TRANSFORMER_RESOLVER_CONFIGURATION,
                NEOFORGE_ACCESS_TRANSFORMER_KIND,
            )

            plugins.withType(ModDevPlugin::class.java).configureEach {
                extensions.getByType(NeoForgeExtension::class.java)
                    .accessTransformers.from(accessTransformer)
            }
        }
    }
}
