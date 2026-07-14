package dev.alexcawl.mcmultiloader.core.configuration2

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ConsumableConfiguration
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.kotlin.dsl.named

private const val NAME = "mmlRuntimeElements"
private const val DESCRIPTION = "Consumable runtime variant for loader-independent MML dependencies."

fun Project.mmlRuntimeElements(
    mmlApi: DependencyScopeConfiguration,
    mmlImplementation: DependencyScopeConfiguration,
    usage: Usage = objects.named<Usage>(Usage.JAVA_RUNTIME),
    category: Category = objects.named<Category>(Category.LIBRARY),
    loaderType: LoaderType = LoaderType.ALL,
): NamedDomainObjectProvider<ConsumableConfiguration> {
    return configurations.consumable(NAME) {
        description = DESCRIPTION
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, usage)
            attribute(Category.CATEGORY_ATTRIBUTE, category)
            attribute(LoaderType.ATTRIBUTE, loaderType)
        }
        extendsFrom(mmlApi, mmlImplementation)
    }
}
