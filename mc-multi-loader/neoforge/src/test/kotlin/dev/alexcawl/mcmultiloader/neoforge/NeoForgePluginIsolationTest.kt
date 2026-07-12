package dev.alexcawl.mcmultiloader.neoforge

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS
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
            mcNeoForgeLoader {
                access {
                    neoForgeAccessTransformer("src/main/resources/META-INF/neoforge-accesstransformer.cfg")
                }
            }
            """.trimIndent()
        )
        write(
            "src/main/resources/META-INF/neoforge-accesstransformer.cfg",
            "# NeoForge loader-specific access transformer",
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
    fun `does not extract access transformers from POM-only artifacts`() {
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
        assertEquals(0, outputFiles.size)
    }

    @Test
    fun `consumes access transformer variant from Gradle module metadata`() {
        write("settings.gradle.kts", "rootProject.name = \"neoforge-access-variant\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.neoforge")
            }
            repositories { maven { url = uri("repo") } }
            dependencies { merged("com.example:common:1.0") }
            tasks.register("verifyNeoForgeAccessVariant") {
                dependsOn("extractMcMultiLoaderNeoForgeAccessTransformers")
            }
            """.trimIndent()
        )
        writeNeoForgeAccessTransformerGradleModule()

        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withArguments("verifyNeoForgeAccessVariant", "--stacktrace")
            .withPluginClasspath()
            .build()

        val outputFiles = Files.list(projectDir.resolve("build/mc-multi-loader/access/neoforge")).use { files ->
            files.toList()
        }
        assertEquals(1, outputFiles.size)
        assertContains(outputFiles.single().toFile().readText(), "public com.example.Common value")
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
            ),
        )
    }

    private fun writeNeoForgeAccessTransformerGradleModule() {
        val directory = projectDir.resolve("repo/com/example/common/1.0").createDirectories()
        directory.resolve("common-1.0.pom").writeText(
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>common</artifactId>
              <version>1.0</version>
              <!-- do_not_remove: published-with-gradle-metadata -->
            </project>
            """.trimIndent()
        )
        ZipOutputStream(Files.newOutputStream(directory.resolve("common-1.0.jar"))).use { jar ->
            jar.putNextEntry(ZipEntry("common.txt"))
            jar.write("common".toByteArray())
            jar.closeEntry()
        }
        directory.resolve("common-1.0.cfg").writeText("public com.example.Common value\n")
        directory.resolve("common-1.0.module").writeText(
            """
            {
              "formatVersion": "1.1",
              "component": {
                "group": "com.example",
                "module": "common",
                "version": "1.0"
              },
              "variants": [
                {
                  "name": "runtimeElements",
                  "attributes": {
                    "org.gradle.category": "library",
                    "org.gradle.libraryelements": "jar",
                    "org.gradle.usage": "java-runtime"
                  },
                  "files": [{ "name": "common-1.0.jar", "url": "common-1.0.jar" }]
                },
                {
                  "name": "$NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS",
                  "attributes": {
                    "dev.alexcawl.minecraft.accessModifier": "ACCESS_TRANSFORMER",
                    "dev.alexcawl.minecraft.loader": "NEOFORGE"
                  },
                  "files": [{ "name": "common-1.0.cfg", "url": "common-1.0.cfg" }]
                }
              ]
            }
            """.trimIndent()
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
