package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.feature.FABRIC_ACCESS_WIDENER_KIND
import dev.alexcawl.mcmultiloader.feature.FABRIC_ACCESS_WIDENER_RESOLVER_CONFIGURATION
import dev.alexcawl.mcmultiloader.feature.configureMcMultiLoaderPlugin
import dev.alexcawl.mcmultiloader.feature.configureMerged
import dev.alexcawl.mcmultiloader.feature.createAccessFileResolver
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.bootstrap.LoomGradlePluginBootstrap
import org.gradle.api.Plugin
import org.gradle.api.Project

class FabricPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureMcMultiLoaderPlugin {
            val model = configureMerged()
            val accessWidener = createAccessFileResolver(
                model,
                FABRIC_ACCESS_WIDENER_RESOLVER_CONFIGURATION,
                FABRIC_ACCESS_WIDENER_KIND,
            )

            plugins.withType(LoomGradlePluginBootstrap::class.java).configureEach {
                val loom = extensions.getByType(LoomGradleExtensionAPI::class.java)
                loom.mixin.useLegacyMixinAp.set(false)
                val accessWidenerFile = providers.provider {
                    accessWidener.get().incoming.artifactView { isLenient = true }.files.files.singleOrNull()
                }
                loom.accessWidenerPath.fileProvider(accessWidenerFile)
            }
        }
    }
}
