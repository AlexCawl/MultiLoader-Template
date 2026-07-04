package dev.alexcawl.multiloader

import net.neoforged.moddevgradle.boot.ModDevPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

internal fun interface NeoforgePluginScope {
    fun neoforge(action: NeoForgeExtension.() -> Unit)
}

internal fun Project.withNeoforgePlugin(block: NeoforgePluginScope.() -> Unit) {
    plugins.withType<ModDevPlugin> {
        val neoforgePluginExtension = extensions.getByType<NeoForgeExtension>()
        val neoforgePluginScope = NeoforgePluginScope { action -> neoforgePluginExtension.action() }
        neoforgePluginScope.block()
    }
}
