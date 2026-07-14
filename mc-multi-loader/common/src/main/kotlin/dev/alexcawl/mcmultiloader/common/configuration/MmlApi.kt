package dev.alexcawl.mcmultiloader.common.configuration

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.withType

private const val NAME = "mmlApi"
private const val DESCRIPTION = "Loader-independent API dependencies included in the MML runtime tree."

internal fun Project.mmlApi(): NamedDomainObjectProvider<DependencyScopeConfiguration> {
    return configurations.dependencyScope(NAME) {
        description = DESCRIPTION
    }
}

internal fun Project.configureMmlApi(mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration>) {
    plugins.withType<JavaPlugin> {
        configurations.named(JavaPlugin.API_CONFIGURATION_NAME) {
            extendsFrom(mmlApi.get())
        }
    }
}
