package dev.alexcawl.mcmultiloader.core.configuration

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration

fun Project.merged(): NamedDomainObjectProvider<DependencyScopeConfiguration> {
    return configurations.dependencyScope(McMultiLoaderConstants.Configuration.MERGED) {
        description = McMultiLoaderConstants.Configuration.MERGED_DESCRIPTION
        extendsFrom()
    }
}
