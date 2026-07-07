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
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class McMultiLoaderPluginTest {
    @TempDir
    lateinit var projectDir: Path

    @Test
    fun `merges one project dependency`() {
        writeProjectFixture()

        runner(":app:jar", ":app:copyAccessWidener").build()

        ZipFile(projectDir.resolve("app/build/libs/app.jar").toFile()).use { jar ->
            assertEquals("common-value", jar.readText("common.txt"))
            assertEquals("app-value", jar.readText("app.txt"))
            assertNotNull(jar.getEntry("com/example/Common.class"))
        }
        ZipFile(projectDir.resolve("common/build/libs/common.jar").toFile()).use { jar ->
            assertContains(jar.readText(DESCRIPTOR_PATH), "fabricAccessWidener=accesswidener")
        }
        assertEquals(
            "accessWidener v2 named\n",
            projectDir.resolve("app/build/accessWidener/accesswidener").toFile().readText()
        )
    }

    @Test
    fun `merges one external Maven module`() {
        write("settings.gradle.kts", "rootProject.name = \"external-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            repositories { maven { url = uri("repo") } }
            dependencies { merged("com.example:common:1.0") }
            """.trimIndent()
        )
        writeMavenModule()

        runner("jar").build()

        ZipFile(projectDir.resolve("build/libs/external-fixture.jar").toFile()).use { jar ->
            assertEquals("external", jar.readText("external.txt"))
        }
    }

    @Test
    fun `publishes and consumes common module with access variant`() {
        val producer = projectDir.resolve("producer")
        val consumer = projectDir.resolve("consumer")
        val repository = projectDir.resolve("published")
        write(producer, "settings.gradle.kts", "rootProject.name = \"published-common\"")
        write(
            producer,
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.common")
                `maven-publish`
            }
            group = "com.example"
            version = "1.0"
            mcMultiLoader {
                access { fabricAccessWidener("accesswidener") }
            }
            publishing {
                publications {
                    create<MavenPublication>("maven") { from(components["java"]) }
                }
                repositories { maven { url = uri("${repository.toUri()}") } }
            }
            """.trimIndent()
        )
        write(producer, "src/main/resources/accesswidener", "accessWidener v2 named\n")
        write(producer, "src/main/resources/common.txt", "published")
        write(producer, "src/main/java/com/example/Common.java", "package com.example; public final class Common {}")

        runner(producer, "publish").build()

        write(consumer, "settings.gradle.kts", "rootProject.name = \"published-consumer\"")
        write(
            consumer,
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            repositories { maven { url = uri("${repository.toUri()}") } }
            dependencies { merged("com.example:published-common:1.0") }
            tasks.register<Copy>("copyAccessWidener") {
                from(configurations["mergedFabricAccessWidener"])
                into(layout.buildDirectory.dir("accessWidener"))
            }
            """.trimIndent()
        )

        runner(consumer, "jar", "copyAccessWidener").build()

        ZipFile(consumer.resolve("build/libs/published-consumer.jar").toFile()).use { jar ->
            assertEquals("published", jar.readText("common.txt"))
            assertNotNull(jar.getEntry("com/example/Common.class"))
        }
        val accessFiles = Files.list(consumer.resolve("build/accessWidener")).use { it.toList() }
        assertEquals(1, accessFiles.size)
        assertEquals("accessWidener v2 named\n", accessFiles.single().toFile().readText())
    }

    @Test
    fun `rejects missing merged dependency`() {
        write("settings.gradle.kts", "rootProject.name = \"missing-fixture\"")
        write(
            "build.gradle.kts",
            "plugins { `java-library`; id(\"dev.alexcawl.mcmultiloader.fabric\") }"
        )

        val result = runner("jar").buildAndFail()

        assertContains(result.output, "must contain exactly one dependency, but contains 0")
    }

    @Test
    fun `rejects multiple merged dependencies`() {
        write("settings.gradle.kts", "rootProject.name = \"multiple-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            repositories { maven { url = uri("repo") } }
            dependencies {
                merged("com.example:common:1.0")
                merged("com.example:other:1.0")
            }
            """.trimIndent()
        )
        writeMavenModule("common")
        writeMavenModule("other")

        val result = runner("jar").buildAndFail()

        assertContains(result.output, "must contain exactly one dependency, but contains 2")
    }

    @Test
    fun `reuses configuration cache on happy path`() {
        writeProjectFixture()

        runner(":app:jar", "--configuration-cache").build()
        val reused = runner(":app:jar", "--configuration-cache").build()

        assertContains(reused.output, "Configuration cache entry reused")
    }

    private fun writeProjectFixture() {
        write(
            "settings.gradle.kts",
            """
            rootProject.name = "project-fixture"
            include("common", "app")
            """.trimIndent()
        )
        write(
            "common/build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.common")
            }
            mcMultiLoader {
                access { fabricAccessWidener("accesswidener") }
            }
            """.trimIndent()
        )
        write(
            "app/build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            dependencies { merged(project(":common")) }
            tasks.register<Copy>("copyAccessWidener") {
                from(configurations["mergedFabricAccessWidener"])
                into(layout.buildDirectory.dir("accessWidener"))
            }
            """.trimIndent()
        )
        write("common/src/main/resources/common.txt", "common-value")
        write("common/src/main/resources/accesswidener", "accessWidener v2 named\n")
        write("app/src/main/resources/app.txt", "app-value")
        write("common/src/main/java/com/example/Common.java", "package com.example; public final class Common {}")
    }

    private fun writeMavenModule(artifact: String = "common") {
        val directory = projectDir.resolve("repo/com/example/$artifact/1.0").createDirectories()
        directory.resolve("$artifact-1.0.pom").writeText(
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>$artifact</artifactId>
              <version>1.0</version>
            </project>
            """.trimIndent()
        )
        ZipOutputStream(Files.newOutputStream(directory.resolve("$artifact-1.0.jar"))).use { jar ->
            jar.putNextEntry(ZipEntry("external.txt"))
            jar.write("external".toByteArray())
            jar.closeEntry()
        }
    }

    private fun runner(vararg arguments: String): GradleRunner = GradleRunner.create()
        .withProjectDir(projectDir.toFile())
        .withArguments(*arguments, "--stacktrace")
        .withPluginClasspath()

    private fun runner(directory: Path, vararg arguments: String): GradleRunner = GradleRunner.create()
        .withProjectDir(directory.toFile())
        .withArguments(*arguments, "--stacktrace")
        .withPluginClasspath()

    private fun write(path: String, content: String) {
        write(projectDir, path, content)
    }

    private fun write(directory: Path, path: String, content: String) {
        val file = directory.resolve(path)
        file.parent?.createDirectories()
        file.writeText(content)
    }

    private fun ZipFile.readText(path: String): String {
        val entry = getEntry(path) ?: error("Missing ZIP entry: $path")
        return getInputStream(entry).bufferedReader().use { it.readText() }
    }
}
