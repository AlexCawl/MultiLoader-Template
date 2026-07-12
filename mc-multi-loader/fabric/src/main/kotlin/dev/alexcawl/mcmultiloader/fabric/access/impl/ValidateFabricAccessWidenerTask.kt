package dev.alexcawl.mcmultiloader.fabric.access.impl

import dev.alexcawl.mcmultiloader.core.configuration.selectMergedArtifacts
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

private const val CONFIGURE_ACCESS_WIDENER_ERROR =
    "Fabric access widener must be configured with mcFabricLoader.access.fabricAccessWidener(...)."

@CacheableTask
abstract class ValidateFabricAccessWidenerTask : DefaultTask() {

    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val loaderAccessWidener: RegularFileProperty

    @get:Classpath
    abstract val accessWideners: ConfigurableFileCollection

    @get:Classpath
    abstract val artifacts: ConfigurableFileCollection

    @get:Classpath
    abstract val directArtifacts: ConfigurableFileCollection

    @get:OutputFile
    abstract val validationMarker: RegularFileProperty

    @TaskAction
    fun validate() {
        if (!loaderAccessWidener.isPresent) throw GradleException(CONFIGURE_ACCESS_WIDENER_ERROR)

        val loaderFile = loaderAccessWidener.get().asFile
        if (!loaderFile.isFile) {
            throw GradleException("Fabric access widener '${loaderFile.path}' was not found.")
        }

        val actual = readFabricAccessWidener(loaderFile.name, loaderFile.readText())
        val variantExpected = accessWideners.files
            .filter { it.isFile && it.extension != "jar" }
            .map { readFabricAccessWidener(it.name, it.readText()) }
        val fallbackExpected = selectMergedArtifacts(artifacts.files, directArtifacts.files)
            .mapNotNull(::readFabricAccessWidener)
        val expected = (variantExpected + fallbackExpected).distinctBy { it.header to it.body }
        validateFabricAccessWidener(actual, expected)

        validationMarker.get().asFile.apply {
            parentFile.mkdirs()
            writeText("ok\n")
        }
    }
}
