package dev.alexcawl.mcmultiloader.metadata

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.assertContains
import kotlin.test.assertEquals

class MetadataPluginTest {
    @TempDir
    lateinit var projectDir: Path

    @Test
    fun `expands per-file values and configures manifest`() {
        write("settings.gradle.kts", "rootProject.name = \"metadata-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.metadata")
            }
            version = "1.2.3"
            mcMultiLoaderMetadata {
                resourceTemplates {
                    template("first.txt") { "value"(providers.provider { "first" }) }
                    template("second.txt") { "value"("second") }
                }
                jarManifest { "Implementation-Version"(project.version) }
            }
            """.trimIndent()
        )
        write("src/main/resources/first.txt", "${'$'}{value}")
        write("src/main/resources/second.txt", "${'$'}{value}")

        runner("jar", "--configuration-cache").build()
        val reused = runner("jar", "--configuration-cache").build()

        assertContains(reused.output, "Configuration cache entry reused")

        JarFile(projectDir.resolve("build/libs/metadata-fixture-1.2.3.jar").toFile()).use { jar ->
            assertEquals("first", jar.getInputStream(jar.getJarEntry("first.txt")).bufferedReader().readText())
            assertEquals("second", jar.getInputStream(jar.getJarEntry("second.txt")).bufferedReader().readText())
            assertEquals("1.2.3", jar.manifest.mainAttributes.getValue("Implementation-Version"))
        }
    }

    @Test
    fun `does not apply Java plugin`() {
        write("settings.gradle.kts", "rootProject.name = \"metadata-isolation\"")
        write(
            "build.gradle.kts",
            """
            plugins { id("dev.alexcawl.mcmultiloader.metadata") }
            tasks.register("verifyNoJava") {
                doLast { check(!pluginManager.hasPlugin("java")) }
            }
            """.trimIndent()
        )

        runner("verifyNoJava").build()
    }

    private fun write(path: String, content: String) {
        val file = projectDir.resolve(path)
        file.parent?.createDirectories()
        file.writeText(content)
    }

    private fun runner(vararg arguments: String): GradleRunner = GradleRunner.create()
        .withProjectDir(projectDir.toFile())
        .withArguments(*arguments, "--stacktrace")
        .withPluginClasspath()
}
