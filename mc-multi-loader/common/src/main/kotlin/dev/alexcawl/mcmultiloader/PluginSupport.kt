package dev.alexcawl.mcmultiloader

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.Usage
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources

const val DESCRIPTOR_PATH = "META-INF/mc-multi-loader/common.properties"
internal val ACCESS_FILE_ATTRIBUTE: Attribute<String> =
    Attribute.of("dev.alexcawl.mcmultiloader.access-file", String::class.java)

data class ConsumerModel(
    val merged: Configuration,
    val mergedArtifact: Configuration
)

internal fun Project.createBaseExtension(): McMultiLoaderExtension {
    tasks.withType(Jar::class.java).configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    return extensions.findByType(McMultiLoaderExtension::class.java)
        ?: extensions.create("mcMultiLoader", McMultiLoaderExtension::class.java)
}

fun Project.createConsumerModel(): ConsumerModel {
    createBaseExtension()
    val merged = configurations.create("merged") {
        isCanBeConsumed = false
        isCanBeResolved = false
        description = "The single common module merged into this loader artifact."
    }
    val mergedArtifact = configurations.create("mergedArtifact") {
        isCanBeConsumed = false
        isCanBeResolved = true
        isTransitive = false
        extendsFrom(merged)
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
        }
    }

    val commonTrees = providers.provider {
        requireSingleMergedDependency(merged)
        mergedArtifact.files.map(::zipTree)
    }
    plugins.withType(JavaPlugin::class.java).configureEach {
        configurations.named(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME) {
            extendsFrom(merged)
        }
        tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, ProcessResources::class.java) {
            dependsOn(mergedArtifact)
            from(commonTrees) {
                exclude("META-INF/MANIFEST.MF")
                exclude("META-INF/*.SF")
                exclude("META-INF/*.RSA")
                exclude("META-INF/*.DSA")
                exclude("META-INF/mc-multi-loader/**")
            }
        }
    }

    return ConsumerModel(merged, mergedArtifact)
}

internal fun Project.createAccessFileElements(
    name: String,
    kind: String,
    resourcePath: Property<String>
) {
    val elements = configurations.create(name) {
        isCanBeConsumed = true
        isCanBeResolved = false
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.DOCUMENTATION))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType::class.java, kind))
            attribute(ACCESS_FILE_ATTRIBUTE, kind)
        }
        outgoing.artifact(resourcePath.map { layout.projectDirectory.file("src/main/resources/$it") }) {
            classifier = kind
        }
    }

    (components.getByName("java") as AdhocComponentWithVariants)
        .addVariantsFromConfiguration(elements) {
            mapToOptional()
        }
}

fun Project.createAccessFileResolver(
    model: ConsumerModel,
    name: String,
    kind: String
): Configuration = configurations.create(name) {
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
    extendsFrom(model.merged)
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType::class.java, kind))
        attribute(ACCESS_FILE_ATTRIBUTE, kind)
    }
}

private fun requireSingleMergedDependency(merged: Configuration) {
    val dependencies = merged.dependencies.toList()
    if (dependencies.size != 1) {
        throw GradleException("Configuration '${merged.name}' must contain exactly one dependency, but contains ${dependencies.size}.")
    }
    val dependency = dependencies.single()
    if (dependency !is ProjectDependency && dependency !is ExternalModuleDependency) {
        throw GradleException("Configuration '${merged.name}' only supports project or external module dependencies.")
    }
}
