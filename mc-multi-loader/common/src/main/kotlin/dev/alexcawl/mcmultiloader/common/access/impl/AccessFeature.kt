package dev.alexcawl.mcmultiloader.common.access.impl

import org.gradle.api.Project
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.file.RegularFileProperty
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.invoke

private const val JAVA_COMPONENT_NAME = "java"
private const val MAVEN_SCOPE_RUNTIME = "runtime"

internal fun Project.configureFabricAccessWidenerElements(
    accessWidener: RegularFileProperty,
) {
    val fabricAccessWidenerElements = fabricAccessWidenerElements(accessWidener)
    components.getByName<AdhocComponentWithVariants>(JAVA_COMPONENT_NAME) {
        fabricAccessWidenerElements {
            addVariantsFromConfiguration(this) {
                mapToMavenScope(MAVEN_SCOPE_RUNTIME)
                mapToOptional()
            }
        }
    }
}

internal fun Project.configureNeoForgeAccessTransformerElements(
    accessTransformer: RegularFileProperty,
) {
    val neoForgeAccessTransformerElements = neoForgeAccessTransformerElements(accessTransformer)
    components.getByName<AdhocComponentWithVariants>(JAVA_COMPONENT_NAME) {
        neoForgeAccessTransformerElements {
            addVariantsFromConfiguration(this) {
                mapToMavenScope(MAVEN_SCOPE_RUNTIME)
                mapToOptional()
            }
        }
    }
}
