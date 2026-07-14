package dev.alexcawl.mcmultiloader.core.configuration2

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.kotlin.dsl.named

private const val NAME = "mmlRuntimeClasspath"
private const val DESCRIPTION = "Resolved runtime artifacts from MML dependencies."

fun Project.mmlRuntimeClasspath(
    mmlApi: DependencyScopeConfiguration,
    mmlImplementation: DependencyScopeConfiguration,
    usage: Usage = objects.named<Usage>(Usage.JAVA_RUNTIME),
    category: Category = objects.named<Category>(Category.LIBRARY),
    loaderType: LoaderType = LoaderType.ALL,
): NamedDomainObjectProvider<ResolvableConfiguration> {
    return configurations.resolvable(NAME) {
        description = DESCRIPTION
        isTransitive = true
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, usage)
            attribute(Category.CATEGORY_ATTRIBUTE, category)
            attribute(LoaderType.ATTRIBUTE, loaderType)
        }
        extendsFrom(mmlApi, mmlImplementation)
    }
}
