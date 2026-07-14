package dev.alexcawl.mcmultiloader.common.access.impl

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ConsumableConfiguration
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.file.RegularFileProperty
import org.gradle.kotlin.dsl.named

internal fun Project.accessFeature(
    accessWidener: RegularFileProperty,
    accessTransformer: RegularFileProperty,
    mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
) {
    configureFabricAccessWidenerElements(accessWidener, mmlApi, mmlImplementation)
    configureNeoForgeAccessTransformerElements(accessTransformer, mmlApi, mmlImplementation)
}

private const val JAVA_COMPONENT_NAME = "java"
private const val MAVEN_SCOPE_RUNTIME = "runtime"

private fun Project.configureFabricAccessWidenerElements(
    accessWidener: RegularFileProperty,
    mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
) {
    val fabricAccessWidenerElements: NamedDomainObjectProvider<ConsumableConfiguration> =
        fabricAccessWidenerElements(accessWidener, mmlApi, mmlImplementation)
    components.named<AdhocComponentWithVariants>(JAVA_COMPONENT_NAME) {
        addVariantsFromConfiguration(fabricAccessWidenerElements.get()) {
            mapToMavenScope(MAVEN_SCOPE_RUNTIME)
            mapToOptional()
        }
    }
}

private fun Project.configureNeoForgeAccessTransformerElements(
    accessTransformer: RegularFileProperty,
    mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
) {
    val neoForgeAccessTransformerElements: NamedDomainObjectProvider<ConsumableConfiguration> =
        neoForgeAccessTransformerElements(accessTransformer, mmlApi, mmlImplementation)
    components.named<AdhocComponentWithVariants>(JAVA_COMPONENT_NAME) {
        addVariantsFromConfiguration(neoForgeAccessTransformerElements.get()) {
            mapToMavenScope(MAVEN_SCOPE_RUNTIME)
            mapToOptional()
        }
    }
}
