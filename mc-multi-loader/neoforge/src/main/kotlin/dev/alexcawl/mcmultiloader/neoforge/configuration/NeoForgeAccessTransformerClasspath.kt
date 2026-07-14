package dev.alexcawl.mcmultiloader.neoforge.configuration

import dev.alexcawl.mcmultiloader.core.AccessModifierType
import dev.alexcawl.mcmultiloader.core.LoaderType
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.artifacts.ResolvableConfiguration

private const val NAME = "neoForgeAccessTransformerClasspath"
private const val DESCRIPTION = "NeoForge access transformers resolved from MML dependencies."

internal fun Project.neoForgeAccessTransformerClasspath(
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
): NamedDomainObjectProvider<ResolvableConfiguration> {
    return configurations.resolvable(NAME) {
        description = DESCRIPTION
        attributes {
            attribute(LoaderType.ATTRIBUTE, LoaderType.NEOFORGE)
            attribute(AccessModifierType.ATTRIBUTE, AccessModifierType.ACCESS_TRANSFORMER)
        }
        extendsFrom(mmlImplementation.get())
    }
}
