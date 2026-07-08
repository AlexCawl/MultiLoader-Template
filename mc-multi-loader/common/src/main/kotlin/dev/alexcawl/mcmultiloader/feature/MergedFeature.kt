package dev.alexcawl.mcmultiloader.feature

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

const val MERGED_CONFIGURATION_NAME = "merged"
const val MERGED_ARTIFACT_CONFIGURATION_NAME = "mergedArtifact"
const val MERGED_CONFIGURATION_DESCRIPTION = "The single common module merged into this loader artifact."
const val MANIFEST_EXCLUDE = "META-INF/MANIFEST.MF"
const val SIGNATURE_EXCLUDE = "META-INF/*.SF"
const val RSA_SIGNATURE_EXCLUDE = "META-INF/*.RSA"
const val DSA_SIGNATURE_EXCLUDE = "META-INF/*.DSA"
const val MC_MULTI_LOADER_METADATA_EXCLUDE = "META-INF/mc-multi-loader/**"
const val MERGED_DEPENDENCY_COUNT_ERROR =
    "Configuration '%s' must contain exactly one dependency, but contains %d."
const val MERGED_DEPENDENCY_TYPE_ERROR =
    "Configuration '%s' only supports project or external module dependencies."

data class ConsumerModel(
    val merged: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    val mergedArtifact: NamedDomainObjectProvider<ResolvableConfiguration>,
)

fun Project.configureMerged(): ConsumerModel {
    val merged = configurations.dependencyScope(MERGED_CONFIGURATION_NAME) {
        description = MERGED_CONFIGURATION_DESCRIPTION
    }
    val mergedArtifact = configurations.resolvable(MERGED_ARTIFACT_CONFIGURATION_NAME) {
        isTransitive = false
        extendsFrom(merged.get())
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
        }
    }
    val commonTrees = providers.provider {
        requireSingleMergedDependency(merged.get())
        mergedArtifact.get().files.map(::zipTree)
    }

    plugins.withType(JavaPlugin::class.java).configureEach {
        configurations.named(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME) {
            extendsFrom(merged.get())
        }
        tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, ProcessResources::class.java) {
            dependsOn(mergedArtifact)
            from(commonTrees) {
                exclude(MANIFEST_EXCLUDE)
                exclude(SIGNATURE_EXCLUDE)
                exclude(RSA_SIGNATURE_EXCLUDE)
                exclude(DSA_SIGNATURE_EXCLUDE)
                exclude(MC_MULTI_LOADER_METADATA_EXCLUDE)
            }
        }
    }

    return ConsumerModel(merged, mergedArtifact)
}

private fun requireSingleMergedDependency(merged: Configuration) {
    val dependencies = merged.dependencies.toList()
    if (dependencies.size != 1) {
        throw GradleException(MERGED_DEPENDENCY_COUNT_ERROR.format(merged.name, dependencies.size))
    }
    val dependency = dependencies.single()
    if (dependency !is ProjectDependency && dependency !is ExternalModuleDependency) {
        throw GradleException(MERGED_DEPENDENCY_TYPE_ERROR.format(merged.name))
    }
}
