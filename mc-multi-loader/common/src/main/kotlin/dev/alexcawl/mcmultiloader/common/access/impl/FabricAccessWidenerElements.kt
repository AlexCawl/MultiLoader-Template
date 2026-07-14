package dev.alexcawl.mcmultiloader.common.access.impl

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import dev.alexcawl.mcmultiloader.core.access.AccessModifierType
import dev.alexcawl.mcmultiloader.core.configuration.LoaderType
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ConsumableConfiguration
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.file.RegularFileProperty

private const val OUTGOING_ACCESS_WIDENER_TYPE = "access-widener"
private const val OUTGOING_ACCESS_WIDENER_EXTENSION = "accesswidener"

internal fun Project.fabricAccessWidenerElements(
    accessWidener: RegularFileProperty,
    mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
): NamedDomainObjectProvider<ConsumableConfiguration> {
    return configurations.consumable(McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_ELEMENTS) {
        description = McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_ELEMENTS_DESCRIPTION
        attributes {
            attribute(LoaderType.ATTRIBUTE, LoaderType.FABRIC)
            attribute(AccessModifierType.ATTRIBUTE, AccessModifierType.ACCESS_WIDENER)
        }
        extendsFrom(mmlApi.get(), mmlImplementation.get())
        outgoing.artifact(accessWidener) {
            type = OUTGOING_ACCESS_WIDENER_TYPE
            extension = OUTGOING_ACCESS_WIDENER_EXTENSION
        }
    }
}
