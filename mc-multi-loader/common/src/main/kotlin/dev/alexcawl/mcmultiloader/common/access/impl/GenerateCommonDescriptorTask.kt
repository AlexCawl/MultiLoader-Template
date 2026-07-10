package dev.alexcawl.mcmultiloader.common.access.impl

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

private const val MAIN_RESOURCES_DIRECTORY = "src/main/resources"

abstract class GenerateCommonDescriptorTask @Inject constructor(
    private val layout: ProjectLayout,
) : DefaultTask() {

    @get:InputFile
    @get:Optional
    abstract val fabricAccessWidener: RegularFileProperty

    @get:InputFile
    @get:Optional
    abstract val neoForgeAccessTransformer: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val resources = layout.projectDirectory.dir(MAIN_RESOURCES_DIRECTORY).asFile
        val descriptor = outputDirectory.file(McMultiLoaderConstants.DESCRIPTOR_PATH).get().asFile
        descriptor.parentFile.mkdirs()
        descriptor.writeText(buildString {
            appendLine("schemaVersion=1")
            appendPath(McMultiLoaderConstants.FABRIC_ACCESS_WIDENER_PROPERTY, fabricAccessWidener, resources)
            appendPath(McMultiLoaderConstants.NEOFORGE_ACCESS_TRANSFORMER_PROPERTY, neoForgeAccessTransformer, resources)
        })
    }

    private fun StringBuilder.appendPath(
        name: String,
        property: RegularFileProperty,
        resources: File,
    ) {
        property.orNull?.asFile?.let { file ->
            val path = file.relativeTo(resources).invariantSeparatorsPath
            appendLine("$name=${escape(path)}")
        }
    }

    private fun escape(value: String): String = value
        .replace("\\", "\\\\")
        .replace("=", "\\=")
        .replace(":", "\\:")
}
