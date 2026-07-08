package dev.alexcawl.mcmultiloader

import dev.alexcawl.mcmultiloader.extension.McMultiLoaderExtension
import dev.alexcawl.mcmultiloader.feature.FABRIC_ACCESS_WIDENER_KIND
import dev.alexcawl.mcmultiloader.feature.FABRIC_ACCESS_WIDENER_RESOLVER_CONFIGURATION
import dev.alexcawl.mcmultiloader.feature.configureMerged
import dev.alexcawl.mcmultiloader.feature.createAccessFileResolver
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.bootstrap.LoomGradlePluginBootstrap
import org.gradle.api.Project

class FabricPlugin : CommonPlugin() {
    override fun configureProject(target: Project, extension: McMultiLoaderExtension) {
        val model = target.configureMerged()
        val accessWidener = target.createAccessFileResolver(
            model,
            FABRIC_ACCESS_WIDENER_RESOLVER_CONFIGURATION,
            FABRIC_ACCESS_WIDENER_KIND,
        )

        target.plugins.withType(LoomGradlePluginBootstrap::class.java).configureEach {
            val loom = target.extensions.getByType(LoomGradleExtensionAPI::class.java)
            loom.mixin.useLegacyMixinAp.set(false)
            val accessWidenerFile = target.providers.provider {
                accessWidener.get().incoming.artifactView { isLenient = true }.files.files.singleOrNull()
            }
            loom.accessWidenerPath.fileProvider(accessWidenerFile)
        }
    }
}
