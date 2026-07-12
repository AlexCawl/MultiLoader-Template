package dev.alexcawl.mcmultiloader.common.access.impl

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import dev.alexcawl.mcmultiloader.core.attributes.AccessModifierType
import dev.alexcawl.mcmultiloader.core.attributes.LoaderType
import org.gradle.api.Project
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

internal fun Project.configureFabricAccessWidenerElements(file: Provider<RegularFile>) {
    val name = McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_ELEMENTS
    if (configurations.findByName(name) != null) return

    val elements = configurations.consumable(name) {
        description = McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_ELEMENTS_DESCRIPTION
        attributes {
            attribute(LoaderType.ATTRIBUTE, LoaderType.FABRIC)
            attribute(AccessModifierType.ATTRIBUTE, AccessModifierType.ACCESS_WIDENER)
        }
        outgoing.artifact(file) {
            type = "access-widener"
            extension = "accesswidener"
        }
    }
    (components.getByName("java") as AdhocComponentWithVariants).addVariantsFromConfiguration(elements.get()) {
        mapToMavenScope("runtime")
        mapToOptional()
    }
}

internal fun Project.configureNeoForgeAccessTransformerElements(file: Provider<RegularFile>) {
    val name = McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS
    if (configurations.findByName(name) != null) return

    val elements = configurations.consumable(name) {
        description = McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS_DESCRIPTION
        attributes {
            attribute(LoaderType.ATTRIBUTE, LoaderType.NEOFORGE)
            attribute(AccessModifierType.ATTRIBUTE, AccessModifierType.ACCESS_TRANSFORMER)
        }
        outgoing.artifact(file) {
            type = "access-transformer"
            extension = "cfg"
        }
    }
    (components.getByName("java") as AdhocComponentWithVariants).addVariantsFromConfiguration(elements.get()) {
        mapToMavenScope("runtime")
        mapToOptional()
    }
}
