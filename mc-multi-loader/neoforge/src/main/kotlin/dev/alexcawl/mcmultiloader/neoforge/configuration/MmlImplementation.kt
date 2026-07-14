package dev.alexcawl.mcmultiloader.neoforge.configuration

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

private const val NAME = "mmlImplementation"
private const val DESCRIPTION = "Loader-independent implementation dependencies included in the MML runtime tree."

internal fun Project.mmlImplementation(): NamedDomainObjectProvider<DependencyScopeConfiguration> {
    return configurations.dependencyScope(NAME) {
        description = DESCRIPTION
    }
}

internal fun Project.configureMmlImplementation(mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>) {
    plugins.withType<JavaPlugin> {
        configurations.named(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME) {
            extendsFrom(mmlImplementation.get())
        }
    }
}
