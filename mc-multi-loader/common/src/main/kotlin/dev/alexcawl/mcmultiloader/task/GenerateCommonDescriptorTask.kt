package dev.alexcawl.mcmultiloader.task

import dev.alexcawl.mcmultiloader.feature.DESCRIPTOR_PATH
import dev.alexcawl.mcmultiloader.feature.FABRIC_ACCESS_WIDENER_PROPERTY
import dev.alexcawl.mcmultiloader.feature.MAIN_RESOURCES_DIRECTORY
import dev.alexcawl.mcmultiloader.feature.NEOFORGE_ACCESS_TRANSFORMER_PROPERTY
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

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
        val resourcesDirectory = layout.projectDirectory.dir(MAIN_RESOURCES_DIRECTORY).asFile
        val descriptor = outputDirectory.file(DESCRIPTOR_PATH).get().asFile
        descriptor.parentFile.mkdirs()
        descriptor.writeText(buildString {
            appendLine("schemaVersion=1")
            listOf(
                FABRIC_ACCESS_WIDENER_PROPERTY to fabricAccessWidener,
                NEOFORGE_ACCESS_TRANSFORMER_PROPERTY to neoForgeAccessTransformer,
            ).forEach { (name, property) ->
                property.orNull?.asFile?.let { file ->
                    val path = file.relativeTo(resourcesDirectory).invariantSeparatorsPath
                    appendLine("$name=${escape(path)}")
                }
            }
        })
    }

    private fun escape(value: String): String = value
        .replace("\\", "\\\\")
        .replace("=", "\\=")
        .replace(":", "\\:")
}
