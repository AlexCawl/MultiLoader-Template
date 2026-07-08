package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.extension.McMultiLoaderExtension
import dev.alexcawl.mcmultiloader.feature.NEOFORGE_ACCESS_TRANSFORMER_KIND
import dev.alexcawl.mcmultiloader.feature.NEOFORGE_ACCESS_TRANSFORMER_RESOLVER_CONFIGURATION
import dev.alexcawl.mcmultiloader.feature.configureMerged
import dev.alexcawl.mcmultiloader.feature.createAccessFileResolver
import net.neoforged.moddevgradle.boot.ModDevPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project

class NeoForgePlugin : CommonPlugin() {
    override fun configureProject(target: Project, extension: McMultiLoaderExtension) {
        val model = target.configureMerged()
        val accessTransformer = target.createAccessFileResolver(
            model,
            NEOFORGE_ACCESS_TRANSFORMER_RESOLVER_CONFIGURATION,
            NEOFORGE_ACCESS_TRANSFORMER_KIND,
        )

        target.plugins.withType(ModDevPlugin::class.java).configureEach {
            target.extensions.getByType(NeoForgeExtension::class.java)
                .accessTransformers.from(accessTransformer)
        }
    }
}
