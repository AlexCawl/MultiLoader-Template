package dev.alexcawl.mcmultiloader.fabric.access.impl

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

private const val CONFIGURE_ACCESS_WIDENER_ERROR =
    "Fabric access widener must be configured with mcFabricLoader.access.fabricAccessWidener(...)."

abstract class ValidateFabricAccessWidenerTask : DefaultTask() {

    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val loaderAccessWidener: RegularFileProperty

    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val accessWideners: ConfigurableFileCollection

    @TaskAction
    fun validate() {
        val expected = accessWideners.files
            .map { readFabricAccessWidener(it.name, it.readText()) }
            .filter(FabricAccessWidener::hasAccessEntries)
            .distinctBy { it.header to it.body }
        if (expected.isEmpty()) return

        if (!loaderAccessWidener.isPresent) throw GradleException(CONFIGURE_ACCESS_WIDENER_ERROR)

        val loaderFile = loaderAccessWidener.get().asFile
        if (!loaderFile.isFile) {
            throw GradleException("Fabric access widener '${loaderFile.path}' was not found.")
        }

        val actual = readFabricAccessWidener(loaderFile.name, loaderFile.readText())
        validateFabricAccessWidener(actual, expected)
    }
}
