package dev.alexcawl.mcmultiloader.task

import dev.alexcawl.mcmultiloader.feature.readFabricAccessWidener
import dev.alexcawl.mcmultiloader.feature.selectEmbeddedArtifacts
import dev.alexcawl.mcmultiloader.feature.validateFabricAccessWidener
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

private const val CONFIGURE_FABRIC_ACCESS_WIDENER_ERROR =
    "Fabric access widener must be configured with mcFabricLoader.access.fabricAccessWidener(...)."

@CacheableTask
abstract class ValidateFabricAccessWidenerTask : DefaultTask() {

    @get:Optional
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val loaderAccessWidener: RegularFileProperty

    @get:Classpath
    abstract val artifacts: ConfigurableFileCollection

    @get:Classpath
    abstract val directArtifacts: ConfigurableFileCollection

    @get:OutputFile
    abstract val validationMarker: RegularFileProperty

    @TaskAction
    fun validate() {
        if (!loaderAccessWidener.isPresent) {
            throw GradleException(CONFIGURE_FABRIC_ACCESS_WIDENER_ERROR)
        }

        val loaderFile = loaderAccessWidener.get().asFile
        if (!loaderFile.isFile) {
            throw GradleException("Fabric access widener '${loaderFile.path}' was not found.")
        }

        val actual = readFabricAccessWidener(loaderFile.name, loaderFile.readText())
        val expected = selectEmbeddedArtifacts(artifacts.files, directArtifacts.files)
            .mapNotNull(::readFabricAccessWidener)
        validateFabricAccessWidener(actual, expected)

        val marker = validationMarker.get().asFile
        marker.parentFile.mkdirs()
        marker.writeText("ok\n")
    }
}
