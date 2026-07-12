package dev.alexcawl.mcmultiloader.neoforge.access.impl

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class ExtractNeoForgeAccessTransformersTask : DefaultTask() {

    @get:Classpath
    abstract val accessTransformers: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun extract() {
        val output = outputDirectory.get().asFile
        output.deleteRecursively()
        output.mkdirs()
        val entries = accessTransformers.files
            .filter { it.isFile && it.extension != "jar" }
            .map { it.name to it.readText() }

        entries.distinctBy { it.second }.forEachIndexed { index, (path, content) ->
            val target = output.resolve("${index.toString().padStart(3, '0')}-${sanitize(path)}")
            target.writeText(content)
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
