package dev.alexcawl.multiloader

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

internal fun interface JavaPluginScope {
    fun java(action: JavaPluginExtension.() -> Unit)
}

internal fun Project.withJavaPlugin(configuration: JavaPluginScope.() -> Unit) {
    plugins.withType<JavaPlugin> {
        val javaPluginExtension = extensions.getByType<JavaPluginExtension>()
        val javaPluginScope = JavaPluginScope { action -> javaPluginExtension.action() }
        javaPluginScope.configuration()
    }
}
