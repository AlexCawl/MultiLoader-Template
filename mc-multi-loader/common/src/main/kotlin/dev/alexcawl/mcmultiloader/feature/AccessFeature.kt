package dev.alexcawl.mcmultiloader.feature

import dev.alexcawl.mcmultiloader.extension.impl.AccessExtensionImpl
import dev.alexcawl.mcmultiloader.extension.impl.AccessType
import dev.alexcawl.mcmultiloader.task.GenerateCommonDescriptorTask
import org.gradle.api.Project
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.Usage
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar

const val ACCESS_FILE_ATTRIBUTE_NAME = "dev.alexcawl.mcmultiloader.access-file"
const val FABRIC_ACCESS_WIDENER_ELEMENTS_CONFIGURATION = "mcMultiLoaderFabricAccessWidenerElements"
const val NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS_CONFIGURATION = "mcMultiLoaderNeoForgeAccessTransformerElements"
const val FABRIC_ACCESS_WIDENER_RESOLVER_CONFIGURATION = "mergedFabricAccessWidener"
const val NEOFORGE_ACCESS_TRANSFORMER_RESOLVER_CONFIGURATION = "mergedNeoForgeAccessTransformer"
const val FABRIC_ACCESS_WIDENER_KIND = "mc-multi-loader-fabric-aw"
const val NEOFORGE_ACCESS_TRANSFORMER_KIND = "mc-multi-loader-neoforge-at"
const val DESCRIPTOR_PATH = "META-INF/mc-multi-loader/common.properties"
const val FABRIC_ACCESS_WIDENER_PROPERTY = "fabricAccessWidener"
const val NEOFORGE_ACCESS_TRANSFORMER_PROPERTY = "neoForgeAccessTransformer"

internal const val MAIN_RESOURCES_DIRECTORY = "src/main/resources"
private const val JAVA_COMPONENT_NAME = "java"
private const val DESCRIPTOR_TASK_NAME = "generateMcMultiLoaderDescriptor"
private const val DESCRIPTOR_TASK_GROUP = "mc-multi-loader"
private const val DESCRIPTOR_OUTPUT_DIRECTORY = "generated/mc-multi-loader/descriptor"

internal val ACCESS_FILE_ATTRIBUTE: Attribute<String> =
    Attribute.of(ACCESS_FILE_ATTRIBUTE_NAME, String::class.java)

internal fun Project.configureAccess(extension: AccessExtensionImpl) {
    val descriptor = tasks.register(DESCRIPTOR_TASK_NAME, GenerateCommonDescriptorTask::class.java) {
        group = DESCRIPTOR_TASK_GROUP
        fabricAccessWidener.set(extension.fabricAccessWidener)
        neoForgeAccessTransformer.set(extension.neoForgeAccessTransformer)
        outputDirectory.set(layout.buildDirectory.dir(DESCRIPTOR_OUTPUT_DIRECTORY))
    }

    extension.configured.configureEach {
        val type = this
        plugins.withType(JavaPlugin::class.java).configureEach {
            when (type) {
                AccessType.FABRIC_ACCESS_WIDENER -> createAccessFileElements(
                    FABRIC_ACCESS_WIDENER_ELEMENTS_CONFIGURATION,
                    FABRIC_ACCESS_WIDENER_KIND,
                    extension.fabricAccessWidener,
                )

                AccessType.NEOFORGE_ACCESS_TRANSFORMER -> createAccessFileElements(
                    NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS_CONFIGURATION,
                    NEOFORGE_ACCESS_TRANSFORMER_KIND,
                    extension.neoForgeAccessTransformer,
                )
            }
        }
    }

    plugins.withType(JavaPlugin::class.java).configureEach {
        tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java) {
            from(descriptor.flatMap { it.outputDirectory })
        }
    }
}

fun Project.createAccessFileResolver(
    model: ConsumerModel,
    name: String,
    kind: String,
): NamedDomainObjectProvider<ResolvableConfiguration> = configurations.resolvable(name) {
    isTransitive = false
    extendsFrom(model.merged.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType::class.java, kind))
        attribute(ACCESS_FILE_ATTRIBUTE, kind)
    }
}

private fun Project.createAccessFileElements(
    name: String,
    kind: String,
    file: RegularFileProperty,
) {
    val elements = configurations.consumable(name) {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.DOCUMENTATION))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType::class.java, kind))
            attribute(ACCESS_FILE_ATTRIBUTE, kind)
        }
        outgoing.artifact(file) {
            classifier = kind
        }
    }

    (components.getByName(JAVA_COMPONENT_NAME) as AdhocComponentWithVariants)
        .addVariantsFromConfiguration(elements.get()) {
            mapToOptional()
        }
}
