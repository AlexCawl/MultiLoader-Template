package dev.alexcawl.mcmultiloader

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.assertEquals

class NeoForgePluginIsolationTest {
    @TempDir
    lateinit var projectDir: Path

    @Test
    fun `works without Fabric or Loom on plugin classpath`() {
        write("settings.gradle.kts", "rootProject.name = \"neoforge-isolation\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.neoforge")
            }
            repositories { maven { url = uri("repo") } }
            dependencies { merged("com.example:common:1.0") }
            """.trimIndent()
        )
        writeMavenModule()

        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withArguments("jar", "--stacktrace")
            .withPluginClasspath()
            .build()

        ZipFile(projectDir.resolve("build/libs/neoforge-isolation.jar").toFile()).use { jar ->
            val entry = jar.getEntry("common.txt") ?: error("Missing common.txt")
            assertEquals("common", jar.getInputStream(entry).bufferedReader().use { it.readText() })
        }
    }

    private fun writeMavenModule() {
        val directory = projectDir.resolve("repo/com/example/common/1.0").createDirectories()
        directory.resolve("common-1.0.pom").writeText(
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>common</artifactId>
              <version>1.0</version>
            </project>
            """.trimIndent()
        )
        ZipOutputStream(Files.newOutputStream(directory.resolve("common-1.0.jar"))).use { jar ->
            jar.putNextEntry(ZipEntry("common.txt"))
            jar.write("common".toByteArray())
            jar.closeEntry()
        }
    }

    private fun write(path: String, content: String) {
        val file = projectDir.resolve(path)
        file.parent?.createDirectories()
        file.writeText(content)
    }
}
