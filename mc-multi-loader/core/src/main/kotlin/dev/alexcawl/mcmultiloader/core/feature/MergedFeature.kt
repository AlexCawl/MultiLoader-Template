package dev.alexcawl.mcmultiloader.core.feature

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
import java.io.File
import java.util.zip.ZipFile

const val MERGED_CONFIGURATION_NAME = "merged"
const val MERGED_ARTIFACT_CONFIGURATION_NAME = "mergedArtifact"
const val DIRECT_MERGED_ARTIFACT_CONFIGURATION_NAME = "directMergedArtifact"
const val MERGED_CONFIGURATION_DESCRIPTION = "Common modules merged into this loader artifact."
const val MANIFEST_EXCLUDE = "META-INF/MANIFEST.MF"
const val SIGNATURE_EXCLUDE = "META-INF/*.SF"
const val RSA_SIGNATURE_EXCLUDE = "META-INF/*.RSA"
const val DSA_SIGNATURE_EXCLUDE = "META-INF/*.DSA"
const val MC_MULTI_LOADER_METADATA_EXCLUDE = "META-INF/mc-multi-loader/**"
const val MERGED_DEPENDENCY_TYPE_ERROR =
    "Configuration '%s' only supports project or external module dependencies."

data class ConsumerModel(
    val merged: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    val mergedArtifact: NamedDomainObjectProvider<ResolvableConfiguration>,
    val directMergedArtifact: NamedDomainObjectProvider<ResolvableConfiguration>,
)

fun Project.configureMerged(): ConsumerModel {
    val merged = configurations.dependencyScope(MERGED_CONFIGURATION_NAME) {
        description = MERGED_CONFIGURATION_DESCRIPTION
    }
    val mergedArtifact = configurations.resolvable(MERGED_ARTIFACT_CONFIGURATION_NAME) {
        isTransitive = true
        extendsFrom(merged.get())
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
        }
    }
    val directMergedArtifact = configurations.resolvable(DIRECT_MERGED_ARTIFACT_CONFIGURATION_NAME) {
        isTransitive = false
        extendsFrom(merged.get())
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
        }
    }
    val commonTrees = providers.provider {
        requireSupportedMergedDependencies(merged.get())
        selectEmbeddedArtifacts(mergedArtifact.get().files, directMergedArtifact.get().files)
            .map(::zipTree)
    }

    plugins.withType(JavaPlugin::class.java).configureEach {
        configurations.named(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME) {
            extendsFrom(merged.get())
        }
        tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, ProcessResources::class.java) {
            dependsOn(mergedArtifact, directMergedArtifact)
            from(commonTrees) {
                exclude(MANIFEST_EXCLUDE)
                exclude(SIGNATURE_EXCLUDE)
                exclude(RSA_SIGNATURE_EXCLUDE)
                exclude(DSA_SIGNATURE_EXCLUDE)
                exclude(MC_MULTI_LOADER_METADATA_EXCLUDE)
            }
        }
    }

    return ConsumerModel(merged, mergedArtifact, directMergedArtifact)
}

internal fun selectEmbeddedArtifacts(
    artifacts: Iterable<File>,
    directArtifacts: Iterable<File>,
): List<File> {
    val directPaths = directArtifacts.map { it.normalizedPath() }.toSet()
    return artifacts.filter { artifact ->
        artifact.normalizedPath() in directPaths || artifact.hasCommonDescriptor()
    }
}

internal fun File.hasCommonDescriptor(): Boolean = isFile && runCatching {
    ZipFile(this).use { zip -> zip.getEntry(DESCRIPTOR_PATH) != null }
}.getOrDefault(false)

private fun requireSupportedMergedDependencies(merged: Configuration) {
    if (merged.dependencies.any { it !is ProjectDependency && it !is ExternalModuleDependency }) {
        throw GradleException(MERGED_DEPENDENCY_TYPE_ERROR.format(merged.name))
    }
}

private fun File.normalizedPath() = toPath().toAbsolutePath().normalize()
