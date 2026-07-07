package dev.alexcawl.mcmultiloader

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.assertContains
import kotlin.test.assertEquals

class McMultiLoaderExtensionTest {
    @TempDir
    lateinit var projectDir: Path

    @Test
    fun `configures metadata and access files`() {
        write("settings.gradle.kts", "rootProject.name = \"extension-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.common")
            }
            version = "1.2.3"
            mcMultiLoader {
                metadata {
                    template {
                        template("first.txt") { "value"(providers.provider { "first" }) }
                        template("second.txt") { "value"("second") }
                        template("a/shared.txt", "b/shared.txt") {
                            "value"("shared")
                        }
                        template(providers.provider { "dynamic" }) {
                            "value"("provider")
                        }
                    }
                    jarManifest { "Implementation-Version"(project.version) }
                }
                access {
                    fabricAccessWidener("accesswidener")
                }
            }
            """.trimIndent()
        )
        write("src/main/resources/first.txt", "${'$'}{value}")
        write("src/main/resources/second.txt", "${'$'}{value}")
        write("src/main/resources/a/shared.txt", "${'$'}{value}")
        write("src/main/resources/b/shared.txt", "${'$'}{value}")
        write("src/main/resources/dynamic", "${'$'}{value}")
        write("src/main/resources/accesswidener", "accessWidener v2 named\n")

        runner("jar", "--configuration-cache").build()
        val reused = runner("jar", "--configuration-cache").build()

        assertContains(reused.output, "Configuration cache entry reused")
        JarFile(projectDir.resolve("build/libs/extension-fixture-1.2.3.jar").toFile()).use { jar ->
            assertEquals("first", jar.getInputStream(jar.getJarEntry("first.txt")).bufferedReader().readText())
            assertEquals("second", jar.getInputStream(jar.getJarEntry("second.txt")).bufferedReader().readText())
            assertEquals("shared", jar.getInputStream(jar.getJarEntry("a/shared.txt")).bufferedReader().readText())
            assertEquals("shared", jar.getInputStream(jar.getJarEntry("b/shared.txt")).bufferedReader().readText())
            assertEquals("provider", jar.getInputStream(jar.getJarEntry("dynamic")).bufferedReader().readText())
            assertEquals("1.2.3", jar.manifest.mainAttributes.getValue("Implementation-Version"))
            assertContains(
                jar.getInputStream(jar.getJarEntry(DESCRIPTOR_PATH)).bufferedReader().readText(),
                "fabricAccessWidener=accesswidener"
            )
        }
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
