package dev.alexcawl.mcmultiloader.common.configuration

import dev.alexcawl.mcmultiloader.core.configuration.LoaderType
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ConsumableConfiguration
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

private const val NAME = "mmlRuntimeElements"
private const val DESCRIPTION = "Consumable runtime variant for loader-independent MML dependencies."

internal fun Project.mmlRuntimeElements(
    mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
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
        extendsFrom(mmlApi.get(), mmlImplementation.get())
    }
}

internal fun Project.configureMmlRuntimeElements(mmlRuntimeElements: NamedDomainObjectProvider<ConsumableConfiguration>) {
    plugins.withType<JavaPlugin> {
        mmlRuntimeElements.configure {
            val mmlRuntimeElements: ConsumableConfiguration = this
            val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            mmlRuntimeElements.outgoing.artifact(jar)
        }
    }
}
