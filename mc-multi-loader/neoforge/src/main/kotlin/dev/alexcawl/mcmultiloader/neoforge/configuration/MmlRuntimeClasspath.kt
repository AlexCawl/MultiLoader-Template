package dev.alexcawl.mcmultiloader.neoforge.configuration

import dev.alexcawl.mcmultiloader.core.LoaderType
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources

private const val NAME = "mmlRuntimeClasspath"
private const val DESCRIPTION = "Resolved runtime artifacts from MML dependencies."

internal fun Project.mmlRuntimeClasspath(
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
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
        extendsFrom(mmlImplementation.get())
    }
}

private const val MANIFEST_EXCLUDE = "META-INF/MANIFEST.MF"
private const val SIGNATURE_EXCLUDE = "META-INF/*.SF"
private const val RSA_SIGNATURE_EXCLUDE = "META-INF/*.RSA"
private const val DSA_SIGNATURE_EXCLUDE = "META-INF/*.DSA"
private const val INTERNAL_METADATA_EXCLUDE = "META-INF/mc-multi-loader/**"

internal fun Project.configureMmlRuntimeClasspath(mmlRuntimeClasspath: NamedDomainObjectProvider<ResolvableConfiguration>) {
    val mmlRuntimeTrees = providers.provider {
        mmlRuntimeClasspath.get().files.map(::zipTree)
    }
    plugins.withType(JavaPlugin::class.java).configureEach {
        tasks.named<ProcessResources>(JavaPlugin.PROCESS_RESOURCES_TASK_NAME) {
            dependsOn(mmlRuntimeClasspath)
            from(mmlRuntimeTrees) {
                exclude(MANIFEST_EXCLUDE)
                exclude(SIGNATURE_EXCLUDE)
                exclude(RSA_SIGNATURE_EXCLUDE)
                exclude(DSA_SIGNATURE_EXCLUDE)
                exclude(INTERNAL_METADATA_EXCLUDE)
            }
        }
    }
}
