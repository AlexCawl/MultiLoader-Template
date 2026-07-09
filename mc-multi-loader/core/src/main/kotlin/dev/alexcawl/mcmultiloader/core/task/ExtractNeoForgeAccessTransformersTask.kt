package dev.alexcawl.mcmultiloader.core.task

import dev.alexcawl.mcmultiloader.core.feature.DESCRIPTOR_PATH
import dev.alexcawl.mcmultiloader.core.feature.NEOFORGE_ACCESS_TRANSFORMER_PROPERTY
import dev.alexcawl.mcmultiloader.core.feature.selectEmbeddedArtifacts
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Properties
import java.util.zip.ZipFile

@CacheableTask
abstract class ExtractNeoForgeAccessTransformersTask : DefaultTask() {

    @get:Classpath
    abstract val artifacts: ConfigurableFileCollection

    @get:Classpath
    abstract val directArtifacts: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun extract() {
        val output = outputDirectory.get().asFile
        output.deleteRecursively()
        output.mkdirs()
        selectEmbeddedArtifacts(artifacts.files, directArtifacts.files).forEachIndexed { index, artifact ->
            extractAccessTransformer(index, artifact, output)
        }
    }

    private fun extractAccessTransformer(index: Int, artifact: File, outputDirectory: File) {
        ZipFile(artifact).use { zip ->
            val properties = zip.readDescriptor() ?: return
            val path = properties.getProperty(NEOFORGE_ACCESS_TRANSFORMER_PROPERTY) ?: return
            val entry = zip.getEntry(path)
                ?: throw GradleException("NeoForge access transformer '$path' declared by '${artifact.name}' was not found.")
            val output = outputDirectory.resolve("${index.toString().padStart(3, '0')}-${sanitize(path)}")
            output.parentFile.mkdirs()
            zip.getInputStream(entry).use { input ->
                output.outputStream().use { outputStream ->
                    input.copyTo(outputStream)
                }
            }
        }
    }

    private fun ZipFile.readDescriptor(): Properties? {
        val entry = getEntry(DESCRIPTOR_PATH) ?: return null
        return Properties().also { properties ->
            getInputStream(entry).use(properties::load)
        }
    }

    private fun sanitize(path: String): String = path.map { character ->
        when {
            character.isLetterOrDigit() -> character
            character == '.' || character == '_' || character == '-' -> character
            else -> '_'
        }
    }.joinToString("")
}
