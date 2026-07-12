package dev.alexcawl.mcmultiloader.core.configuration

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import dev.alexcawl.mcmultiloader.core.attributes.AccessModifierType
import dev.alexcawl.mcmultiloader.core.attributes.LoaderType
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.language.jvm.tasks.ProcessResources

private const val MANIFEST_EXCLUDE = "META-INF/MANIFEST.MF"
private const val SIGNATURE_EXCLUDE = "META-INF/*.SF"
private const val RSA_SIGNATURE_EXCLUDE = "META-INF/*.RSA"
private const val DSA_SIGNATURE_EXCLUDE = "META-INF/*.DSA"
private const val INTERNAL_METADATA_EXCLUDE = "META-INF/mc-multi-loader/**"

data class MergedDependencies(
    val dependencies: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    val artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
    val directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
)

fun Project.configureMergedDependencies(): MergedDependencies {
    val merged = configureMergedDependencyScope()
    val artifacts = configureMergedArtifactClasspath(merged)
    val directArtifacts = configureDirectMergedArtifactClasspath(merged)
    val commonTrees = providers.provider {
        requireSupportedDependencies(merged.get())
        selectMergedArtifacts(artifacts.get().files, directArtifacts.get().files).map(::zipTree)
    }

    plugins.withType(JavaPlugin::class.java).configureEach {
        configurations.named(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME) {
            extendsFrom(merged.get())
        }
        tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, ProcessResources::class.java) {
            dependsOn(artifacts, directArtifacts)
            from(commonTrees) {
                exclude(MANIFEST_EXCLUDE)
                exclude(SIGNATURE_EXCLUDE)
                exclude(RSA_SIGNATURE_EXCLUDE)
                exclude(DSA_SIGNATURE_EXCLUDE)
                exclude(INTERNAL_METADATA_EXCLUDE)
            }
        }
    }

    return MergedDependencies(merged, artifacts, directArtifacts)
}

fun Project.configureFabricAccessWidenerClasspath(
    merged: NamedDomainObjectProvider<DependencyScopeConfiguration>,
): NamedDomainObjectProvider<ResolvableConfiguration> {
    return configurations.resolvable(McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_CLASSPATH) {
        description = McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_CLASSPATH_DESCRIPTION
        extendsFrom(merged.get())
        attributes {
            attribute(LoaderType.ATTRIBUTE, LoaderType.FABRIC)
            attribute(AccessModifierType.ATTRIBUTE, AccessModifierType.ACCESS_WIDENER)
        }
    }
}

fun Project.configureNeoForgeAccessTransformerClasspath(
    merged: NamedDomainObjectProvider<DependencyScopeConfiguration>,
): NamedDomainObjectProvider<ResolvableConfiguration> {
    return configurations.resolvable(McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_CLASSPATH) {
        description = McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_CLASSPATH_DESCRIPTION
        extendsFrom(merged.get())
        attributes {
            attribute(LoaderType.ATTRIBUTE, LoaderType.NEOFORGE)
            attribute(AccessModifierType.ATTRIBUTE, AccessModifierType.ACCESS_TRANSFORMER)
        }
    }
}

private fun Project.configureMergedDependencyScope(): NamedDomainObjectProvider<DependencyScopeConfiguration> =
    configurations.dependencyScope(McMultiLoaderConstants.Configuration.MERGED) {
        description = McMultiLoaderConstants.Configuration.MERGED_DESCRIPTION
    }

private fun Project.configureMergedArtifactClasspath(
    merged: NamedDomainObjectProvider<DependencyScopeConfiguration>,
): NamedDomainObjectProvider<ResolvableConfiguration> = configurations.resolvable(
    McMultiLoaderConstants.Configuration.MERGED_ARTIFACT,
) {
    description = McMultiLoaderConstants.Configuration.MERGED_ARTIFACT_DESCRIPTION
    isTransitive = true
    extendsFrom(merged.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
    }
}

private fun Project.configureDirectMergedArtifactClasspath(
    merged: NamedDomainObjectProvider<DependencyScopeConfiguration>,
): NamedDomainObjectProvider<ResolvableConfiguration> = configurations.resolvable(
    McMultiLoaderConstants.Configuration.DIRECT_MERGED_ARTIFACT,
) {
    description = McMultiLoaderConstants.Configuration.DIRECT_MERGED_ARTIFACT_DESCRIPTION
    isTransitive = false
    extendsFrom(merged.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
    }
}

private fun requireSupportedDependencies(merged: Configuration) {
    if (merged.dependencies.any { it !is ProjectDependency && it !is ExternalModuleDependency }) {
        throw GradleException(McMultiLoaderConstants.MERGED_DEPENDENCY_TYPE_ERROR.format(merged.name))
    }
}
