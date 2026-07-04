package dev.alexcawl.multiloader

import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.bootstrap.LoomGradlePluginBootstrap
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

internal fun interface FabricPluginScope {
    fun fabric(action: LoomGradleExtensionAPI.() -> Unit)
}

internal fun Project.withFabricPlugin(block: FabricPluginScope.() -> Unit) {
    plugins.withType<LoomGradlePluginBootstrap> {
        val fabricPluginExtension = extensions.getByType<LoomGradleExtensionAPI>()
        val fabricPluginScope = FabricPluginScope { action -> fabricPluginExtension.action() }
        fabricPluginScope.block()
    }
}
