package dev.alexcawl.mcmultiloader.neoforge

import dev.alexcawl.mcmultiloader.core.feature.DESCRIPTOR_PATH
import dev.alexcawl.mcmultiloader.core.feature.NEOFORGE_ACCESS_TRANSFORMER_PROPERTY
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
import kotlin.test.assertContains
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

    @Test
    fun `extracts access transformers from descriptor-bearing artifacts`() {
        write("settings.gradle.kts", "rootProject.name = \"neoforge-access\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.neoforge")
            }
            repositories { maven { url = uri("repo") } }
            dependencies { merged("com.example:feature:1.0") }
            """.trimIndent()
        )
        writeCommonMavenModule("base", accessTransformer = "public com.example.Base value")
        writeCommonMavenModule(
            "feature",
            dependencies = listOf("base"),
            accessTransformer = "public com.example.Feature value",
        )

        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withArguments("extractMcMultiLoaderNeoForgeAccessTransformers", "--stacktrace")
            .withPluginClasspath()
            .build()

        val outputFiles = Files.list(projectDir.resolve("build/mc-multi-loader/access/neoforge")).use { files ->
            files.toList().sortedBy { it.fileName.toString() }
        }
        assertEquals(2, outputFiles.size)
        val content = outputFiles.joinToString("\n") { it.toFile().readText() }
        assertContains(content, "public com.example.Base value")
        assertContains(content, "public com.example.Feature value")
    }

    private fun writeCommonMavenModule(
        artifact: String,
        dependencies: List<String> = emptyList(),
        accessTransformer: String,
    ) {
        writeMavenModule(
            artifact,
            dependencies,
            mapOf(
                "$artifact.txt" to artifact,
                "META-INF/$artifact-accesstransformer.cfg" to accessTransformer,
                DESCRIPTOR_PATH to "schemaVersion=1\n$NEOFORGE_ACCESS_TRANSFORMER_PROPERTY=META-INF/$artifact-accesstransformer.cfg\n",
            ),
        )
    }

    private fun writeMavenModule(
        artifact: String = "common",
        dependencies: List<String> = emptyList(),
        entries: Map<String, String> = mapOf("common.txt" to "common"),
    ) {
        val moduleDirectory = projectDir.resolve("repo/com/example/$artifact/1.0").createDirectories()
        moduleDirectory.resolve("$artifact-1.0.pom").writeText(
            buildString {
                appendLine(
                    """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>$artifact</artifactId>
              <version>1.0</version>
                    """.trimIndent()
                )
                if (dependencies.isNotEmpty()) {
                    appendLine("  <dependencies>")
                    dependencies.forEach { dependency ->
                        appendLine("    <dependency>")
                        appendLine("      <groupId>com.example</groupId>")
                        appendLine("      <artifactId>$dependency</artifactId>")
                        appendLine("      <version>1.0</version>")
                        appendLine("    </dependency>")
                    }
                    appendLine("  </dependencies>")
                }
                appendLine("</project>")
            }
        )
        ZipOutputStream(Files.newOutputStream(moduleDirectory.resolve("$artifact-1.0.jar"))).use { jar ->
            entries.forEach { (path, content) ->
                jar.putNextEntry(ZipEntry(path))
                jar.write(content.toByteArray())
                jar.closeEntry()
            }
        }
    }

    private fun write(path: String, content: String) {
        val file = projectDir.resolve(path)
        file.parent?.createDirectories()
        file.writeText(content)
    }
}
