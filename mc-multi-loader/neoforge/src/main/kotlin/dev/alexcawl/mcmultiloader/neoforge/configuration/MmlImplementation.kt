package dev.alexcawl.mcmultiloader.neoforge.configuration

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration

private const val NAME = "mmlImplementation"
private const val DESCRIPTION = "Loader-independent implementation dependencies included in the MML runtime tree."

internal fun Project.mmlImplementation(): NamedDomainObjectProvider<DependencyScopeConfiguration> {
    return configurations.dependencyScope(NAME) {
        description = DESCRIPTION
    }
}
