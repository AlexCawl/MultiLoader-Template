package dev.alexcawl.mcmultiloader.core.configuration

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.kotlin.dsl.named

fun Project.mergedArtifact(
    merged: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    usage: Usage = objects.named<Usage>(Usage.JAVA_RUNTIME),
    category: Category = objects.named<Category>(Category.LIBRARY),
): NamedDomainObjectProvider<ResolvableConfiguration> {
    return configurations.resolvable(McMultiLoaderConstants.Configuration.MERGED_ARTIFACT) {
        description = McMultiLoaderConstants.Configuration.MERGED_ARTIFACT_DESCRIPTION
        isTransitive = true
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, usage)
            attribute(Category.CATEGORY_ATTRIBUTE, category)
        }
        extendsFrom(merged.get())
    }
}
