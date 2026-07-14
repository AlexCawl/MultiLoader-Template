package dev.alexcawl.mcmultiloader.fabric.configuration

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import dev.alexcawl.mcmultiloader.core.AccessModifierType
import dev.alexcawl.mcmultiloader.core.LoaderType
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.artifacts.ResolvableConfiguration

internal fun Project.fabricAccessWidenerClasspath(
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
): NamedDomainObjectProvider<ResolvableConfiguration> {
    return configurations.resolvable(McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_CLASSPATH) {
        description = McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_CLASSPATH_DESCRIPTION
        attributes {
            attribute(LoaderType.ATTRIBUTE, LoaderType.FABRIC)
            attribute(AccessModifierType.ATTRIBUTE, AccessModifierType.ACCESS_WIDENER)
        }
        extendsFrom(mmlImplementation.get())
    }
}
