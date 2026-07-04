package dev.alexcawl.multiloader

import net.minecraftforge.gradle.userdev.UserDevExtension
import net.minecraftforge.gradle.userdev.UserDevPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

internal fun interface ForgePluginScope {
    fun forge(action: UserDevExtension.() -> Unit)
}

internal fun Project.withForgePlugin(block: ForgePluginScope.() -> Unit) {
    plugins.withType<UserDevPlugin> {
        val forgePluginExtension = extensions.getByType<UserDevExtension>()
        val forgePluginScope = ForgePluginScope { action -> forgePluginExtension.action() }
        forgePluginScope.block()
    }
}
