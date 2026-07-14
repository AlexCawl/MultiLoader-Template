package dev.alexcawl.mcmultiloader.core.configuration2

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration

private const val NAME = "mmlApi"
private const val DESCRIPTION = "Loader-independent API dependencies included in the MML runtime tree."

fun Project.mmlApi(): NamedDomainObjectProvider<DependencyScopeConfiguration> {
    return configurations.dependencyScope(NAME) {
        description = DESCRIPTION
    }
}
