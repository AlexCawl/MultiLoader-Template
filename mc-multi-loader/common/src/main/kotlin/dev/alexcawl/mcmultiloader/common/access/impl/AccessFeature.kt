package dev.alexcawl.mcmultiloader.common.access.impl

import dev.alexcawl.mcmultiloader.common.access.impl.AccessConstants.DESCRIPTOR_OUTPUT_DIRECTORY
import dev.alexcawl.mcmultiloader.common.access.impl.AccessConstants.DESCRIPTOR_TASK_GROUP
import dev.alexcawl.mcmultiloader.common.access.impl.AccessConstants.DESCRIPTOR_TASK_NAME
import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import dev.alexcawl.mcmultiloader.core.attributes.AccessModifierType
import dev.alexcawl.mcmultiloader.core.attributes.LoaderType
import org.gradle.api.Project
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

internal fun Project.configureCommonAccess(
    fabricAccessWidener: RegularFileProperty,
    neoForgeAccessTransformer: RegularFileProperty,
) {
    val descriptor = tasks.register<GenerateCommonDescriptorTask>(DESCRIPTOR_TASK_NAME) {
        group = DESCRIPTOR_TASK_GROUP
        this.fabricAccessWidener.set(fabricAccessWidener)
        this.neoForgeAccessTransformer.set(neoForgeAccessTransformer)
        outputDirectory.set(layout.buildDirectory.dir(DESCRIPTOR_OUTPUT_DIRECTORY))
    }
    tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
        from(descriptor.flatMap { it.outputDirectory })
    }
}

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
