package dev.alexcawl.mcmultiloader.common.access.impl

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import dev.alexcawl.mcmultiloader.core.AccessModifierType
import dev.alexcawl.mcmultiloader.core.LoaderType
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ConsumableConfiguration
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.file.RegularFileProperty

private const val OUTGOING_ACCESS_TRANSFORMER_TYPE = "access-transformer"
private const val OUTGOING_ACCESS_TRANSFORMER_EXTENSION = "cfg"

internal fun Project.neoForgeAccessTransformerElements(
    accessTransformer: RegularFileProperty,
    mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
): NamedDomainObjectProvider<ConsumableConfiguration> {
    return configurations.consumable(McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS) {
        description = McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS_DESCRIPTION
        attributes {
            attribute(LoaderType.ATTRIBUTE, LoaderType.NEOFORGE)
            attribute(AccessModifierType.ATTRIBUTE, AccessModifierType.ACCESS_TRANSFORMER)
        }
        extendsFrom(mmlApi.get(), mmlImplementation.get())
        outgoing.artifact(accessTransformer) {
            type = OUTGOING_ACCESS_TRANSFORMER_TYPE
            extension = OUTGOING_ACCESS_TRANSFORMER_EXTENSION
        }
    }
}
