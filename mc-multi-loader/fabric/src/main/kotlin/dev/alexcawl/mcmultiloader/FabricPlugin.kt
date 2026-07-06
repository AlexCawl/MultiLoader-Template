package dev.alexcawl.mcmultiloader

import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Plugin
import org.gradle.api.Project

class FabricPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val model = target.createConsumerModel()
        val accessWidener = target.createAccessFileResolver(
            model,
            "mergedFabricAccessWidener",
            "mc-multi-loader-fabric-aw"
        )

        target.pluginManager.withPlugin("fabric-loom") {
            val loom = target.extensions.getByType(LoomGradleExtensionAPI::class.java)
            loom.mixin.useLegacyMixinAp.set(false)
            val accessWidenerFile = target.providers.provider {
                accessWidener.incoming.artifactView { isLenient = true }.files.files.singleOrNull()
            }
            loom.accessWidenerPath.fileProvider(accessWidenerFile)
        }
    }
}
