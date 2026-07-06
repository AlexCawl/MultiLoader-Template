package dev.alexcawl.mcmultiloader

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateCommonDescriptorTask : DefaultTask() {
    @get:Input
    @get:Optional
    abstract val fabricAccessWidener: Property<String>

    @get:Input
    @get:Optional
    abstract val neoForgeAccessTransformer: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val descriptor = outputDirectory.file(DESCRIPTOR_PATH).get().asFile
        descriptor.parentFile.mkdirs()
        descriptor.writeText(buildString {
            appendLine("schemaVersion=1")
            fabricAccessWidener.orNull?.let { appendLine("fabricAccessWidener=${escape(it)}") }
            neoForgeAccessTransformer.orNull?.let { appendLine("neoForgeAccessTransformer=${escape(it)}") }
        })
    }

    private fun escape(value: String): String = value
        .replace("\\", "\\\\")
        .replace("=", "\\=")
        .replace(":", "\\:")
}
