package dev.alexcawl.mcmultiloader.neoforge.configuration

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import dev.alexcawl.mcmultiloader.core.AccessModifierType
import dev.alexcawl.mcmultiloader.core.LoaderType
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.artifacts.ResolvableConfiguration

fun Project.neoForgeAccessTransformerClasspath(
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
): NamedDomainObjectProvider<ResolvableConfiguration> {
    return configurations.resolvable(McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_CLASSPATH) {
        description = McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_CLASSPATH_DESCRIPTION
        attributes {
            attribute(LoaderType.ATTRIBUTE, LoaderType.NEOFORGE)
            attribute(AccessModifierType.ATTRIBUTE, AccessModifierType.ACCESS_TRANSFORMER)
        }
        extendsFrom(mmlImplementation.get())
    }
}
