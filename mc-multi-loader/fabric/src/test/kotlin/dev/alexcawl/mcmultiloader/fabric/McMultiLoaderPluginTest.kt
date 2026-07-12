package dev.alexcawl.mcmultiloader.fabric

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_CLASSPATH
import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_ELEMENTS
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
import kotlin.test.assertNull

private const val LOADER_ACCESS_WIDENER_PATH = "META-INF/fabric.accesswidener"

class McMultiLoaderPluginTest {
    @TempDir
    lateinit var projectDir: Path

    @Test
    fun `merges one project dependency`() {
        writeProjectFixture()

        runner(":app:jar").build()

        ZipFile(projectDir.resolve("app/build/libs/app.jar").toFile()).use { jar ->
            assertEquals("common-value", jar.readText("common.txt"))
            assertEquals("app-value", jar.readText("app.txt"))
            assertNotNull(jar.getEntry("com/example/Common.class"))
            assertEquals(
                "accessWidener v2 named\naccessible class com/example/Common\n",
                jar.readText(LOADER_ACCESS_WIDENER_PATH),
            )
        }
        assertEquals("ok\n", projectDir.resolve("app/build/mc-multi-loader/access/fabric/validated.marker").toFile().readText())
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
            mcFabricLoader {
                access { fabricAccessWidener("src/main/resources/$LOADER_ACCESS_WIDENER_PATH") }
            }
            """.trimIndent()
        )
        write("src/main/resources/$LOADER_ACCESS_WIDENER_PATH", "accessWidener v2 named\n")
        writeMavenModule()

        runner("jar").build()

        ZipFile(projectDir.resolve("build/libs/external-fixture.jar").toFile()).use { jar ->
            assertEquals("common", jar.readText("common.txt"))
        }
    }

    @Test
    fun `merges published Maven module directly`() {
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
                `maven-publish`
            }
            group = "com.example"
            version = "1.0"
            publishing {
                publications {
                    create<MavenPublication>("maven") { from(components["java"]) }
                }
                repositories { maven { url = uri("${repository.toUri()}") } }
            }
            """.trimIndent()
        )
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
            mcFabricLoader {
                access { fabricAccessWidener("src/main/resources/$LOADER_ACCESS_WIDENER_PATH") }
            }
            """.trimIndent()
        )
        write(consumer, "src/main/resources/$LOADER_ACCESS_WIDENER_PATH", "accessWidener v2 named\naccessible class com/example/Common\n")

        runner(consumer, "jar").build()

        ZipFile(consumer.resolve("build/libs/published-consumer.jar").toFile()).use { jar ->
            assertEquals("published", jar.readText("common.txt"))
            assertNotNull(jar.getEntry("com/example/Common.class"))
            assertEquals(
                "accessWidener v2 named\naccessible class com/example/Common\n",
                jar.readText(LOADER_ACCESS_WIDENER_PATH),
            )
        }
    }

    @Test
    fun `consumes fabric access widener variant from Gradle module metadata`() {
        write("settings.gradle.kts", "rootProject.name = \"fabric-access-variant\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            repositories { maven { url = uri("repo") } }
            dependencies { merged("com.example:common:1.0") }
            mcFabricLoader {
                access { fabricAccessWidener("src/main/resources/$LOADER_ACCESS_WIDENER_PATH") }
            }
            tasks.register("verifyFabricAccessVariant") {
                dependsOn("validateMcMultiLoaderFabricAccessWidener")
                doLast {
                    val resolvedFiles = configurations.getByName("$FABRIC_ACCESS_WIDENER_CLASSPATH").files
                    val accessWideners = resolvedFiles
                        .filter { it.extension != "jar" }
                        .map { it.readText() }
                    check(accessWideners.any { it.contains("accessible class com/example/Common") }) {
                        "Resolved files: " + resolvedFiles.map { it.name }
                    }
                }
            }
            """.trimIndent()
        )
        write(
            "src/main/resources/$LOADER_ACCESS_WIDENER_PATH",
            "accessWidener v2 named\naccessible class com/example/Common\n",
        )
        writeFabricAccessWidenerGradleModule()

        runner("verifyFabricAccessVariant").build()
    }

    @Test
    fun `merges multiple direct dependencies`() {
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
            mcFabricLoader {
                access { fabricAccessWidener("src/main/resources/$LOADER_ACCESS_WIDENER_PATH") }
            }
            """.trimIndent()
        )
        write(
            "src/main/resources/$LOADER_ACCESS_WIDENER_PATH",
            "accessWidener v2 named\naccessible class com/example/Common\naccessible class com/example/Other\n",
        )
        writeCommonMavenModule("common")
        writeCommonMavenModule("other")

        runner("jar").build()

        ZipFile(projectDir.resolve("build/libs/multiple-fixture.jar").toFile()).use { jar ->
            assertEquals("common", jar.readText("common.txt"))
            assertEquals("other", jar.readText("other.txt"))
        }
    }

    @Test
    fun `does not embed transitive project dependency`() {
        write(
            "settings.gradle.kts",
            """
            rootProject.name = "transitive-project-fixture"
            include("common-base", "common", "app")
            """.trimIndent()
        )
        writeCommonProject("common-base", null, "Base")
        writeCommonProject("common", "common-base", "Common")
        write(
            "app/build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            dependencies { merged(project(":common")) }
            mcFabricLoader {
                access { fabricAccessWidener("src/main/resources/$LOADER_ACCESS_WIDENER_PATH") }
            }
            """.trimIndent()
        )
        write("app/src/main/resources/app.txt", "app-value")
        write(
            "app/src/main/resources/$LOADER_ACCESS_WIDENER_PATH",
            """
            accessWidener v2 named
            accessible class com/example/Common
            """.trimIndent(),
        )

        runner(":app:jar").build()

        ZipFile(projectDir.resolve("app/build/libs/app.jar").toFile()).use { jar ->
            assertEquals("common-value", jar.readText("common.txt"))
            assertNotNull(jar.getEntry("com/example/Common.class"))
            assertNull(jar.getEntry("common-base.txt"))
            assertNull(jar.getEntry("com/example/Base.class"))
            val accessWidener = jar.readText(LOADER_ACCESS_WIDENER_PATH)
            assertEquals(1, Regex("^accessWidener", RegexOption.MULTILINE).findAll(accessWidener).count())
            assertContains(accessWidener, "accessible class com/example/Common")
        }
    }

    @Test
    fun `does not embed transitive Maven artifact`() {
        write("settings.gradle.kts", "rootProject.name = \"plain-transitive-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            repositories { maven { url = uri("repo") } }
            dependencies { merged("com.example:feature:1.0") }
            mcFabricLoader {
                access { fabricAccessWidener("src/main/resources/$LOADER_ACCESS_WIDENER_PATH") }
            }
            """.trimIndent()
        )
        write("src/main/resources/$LOADER_ACCESS_WIDENER_PATH", "accessWidener v2 named\n")
        writeMavenModule("plain-base")
        writeMavenModule("feature", dependencies = listOf("plain-base"))

        runner("jar").build()

        ZipFile(projectDir.resolve("build/libs/plain-transitive-fixture.jar").toFile()).use { jar ->
            assertEquals("feature", jar.readText("feature.txt"))
            assertNull(jar.getEntry("plain-base.txt"))
        }
    }

    @Test
    fun `fails on fabric access widener header mismatch`() {
        write("settings.gradle.kts", "rootProject.name = \"header-mismatch-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            repositories { maven { url = uri("repo") } }
            dependencies {
                merged("com.example:first:1.0")
                merged("com.example:second:1.0")
            }
            mcFabricLoader {
                access { fabricAccessWidener("src/main/resources/$LOADER_ACCESS_WIDENER_PATH") }
            }
            """.trimIndent()
        )
        write(
            "src/main/resources/$LOADER_ACCESS_WIDENER_PATH",
            "accessWidener v2 named\naccessible class com/example/First\naccessible class com/example/Second",
        )
        writeFabricAccessWidenerGradleModule(
            "first",
            "accessWidener v2 named\naccessible class com/example/First\n",
        )
        writeFabricAccessWidenerGradleModule(
            "second",
            "accessWidener v2 intermediary\naccessible class com/example/Second\n",
        )

        val result = runner("jar").buildAndFail()

        assertContains(result.output, "Fabric access widener header mismatch")
    }

    @Test
    fun `fails when fabric access widener is not configured`() {
        write(
            "settings.gradle.kts",
            """
            rootProject.name = "missing-loader-aw-fixture"
            include("common", "app")
            """.trimIndent()
        )
        writeCommonProject("common", null, "Common")
        write(
            "app/build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            dependencies { merged(project(":common")) }
            """.trimIndent()
        )

        val result = runner(":app:jar").buildAndFail()

        assertContains(result.output, "Fabric access widener must be configured with mcFabricLoader.access.fabricAccessWidener(...).")
    }

    @Test
    fun `fails when fabric access widener is missing common entries`() {
        write("settings.gradle.kts", "rootProject.name = \"missing-common-aw-entry-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.fabric")
            }
            repositories { maven { url = uri("repo") } }
            dependencies { merged("com.example:common:1.0") }
            mcFabricLoader {
                access { fabricAccessWidener("src/main/resources/$LOADER_ACCESS_WIDENER_PATH") }
            }
            """.trimIndent()
        )
        write("src/main/resources/$LOADER_ACCESS_WIDENER_PATH", "accessWidener v2 named\n")
        writeFabricAccessWidenerGradleModule()

        val result = runner("jar").buildAndFail()

        assertContains(result.output, "Fabric access widener 'fabric.accesswidener' is missing entries")
        assertContains(result.output, "accessible class com/example/Common")
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
            mcFabricLoader {
                access { fabricAccessWidener("src/main/resources/$LOADER_ACCESS_WIDENER_PATH") }
            }
            """.trimIndent()
        )
        write("common/src/main/resources/common.txt", "common-value")
        write("common/src/main/resources/accesswidener", "accessWidener v2 named\naccessible class com/example/Common")
        write("app/src/main/resources/app.txt", "app-value")
        write("app/src/main/resources/$LOADER_ACCESS_WIDENER_PATH", "accessWidener v2 named\naccessible class com/example/Common\n")
        write("common/src/main/java/com/example/Common.java", "package com.example; public final class Common {}")
    }

    private fun writeCommonProject(name: String, dependency: String?, className: String) {
        val accessWidener = "$name.accesswidener"
        write(
            "$name/build.gradle.kts",
            buildString {
                appendLine("plugins {")
                appendLine("    `java-library`")
                appendLine("}")
                dependency?.let {
                    appendLine("dependencies { implementation(project(\":$it\")) }")
                }
            }
        )
        write("$name/src/main/resources/$name.txt", "${name.removePrefix("common-")}-value")
        write("$name/src/main/resources/$accessWidener", "accessWidener v2 named\naccessible class com/example/$className")
        write(
            "$name/src/main/java/com/example/$className.java",
            "package com.example; public final class $className {}",
        )
    }

    private fun writeCommonMavenModule(
        artifact: String,
        dependencies: List<String> = emptyList(),
        fabricHeader: String = "accessWidener v2 named",
    ) {
        writeMavenModule(
            artifact,
            dependencies,
            mapOf(
                "$artifact.txt" to artifact,
                "$artifact.accesswidener" to "$fabricHeader\naccessible class com/example/${artifact.replaceFirstChar(Char::uppercase)}",
            ),
        )
    }

    private fun writeFabricAccessWidenerGradleModule(
        artifact: String = "common",
        accessWidener: String = "accessWidener v2 named\naccessible class com/example/Common\n",
    ) {
        val directory = projectDir.resolve("repo/com/example/$artifact/1.0").createDirectories()
        directory.resolve("$artifact-1.0.pom").writeText(
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>$artifact</artifactId>
              <version>1.0</version>
              <!-- do_not_remove: published-with-gradle-metadata -->
            </project>
            """.trimIndent()
        )
        ZipOutputStream(Files.newOutputStream(directory.resolve("$artifact-1.0.jar"))).use { jar ->
            jar.putNextEntry(ZipEntry("$artifact.txt"))
            jar.write(artifact.toByteArray())
            jar.closeEntry()
        }
        directory.resolve("$artifact-1.0.accesswidener").writeText(accessWidener)
        directory.resolve("$artifact-1.0.module").writeText(
            """
            {
              "formatVersion": "1.1",
              "component": {
                "group": "com.example",
                "module": "$artifact",
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
                  "files": [{ "name": "$artifact-1.0.jar", "url": "$artifact-1.0.jar" }]
                },
                {
                  "name": "$FABRIC_ACCESS_WIDENER_ELEMENTS",
                  "attributes": {
                    "dev.alexcawl.minecraft.accessModifier": "ACCESS_WIDENER",
                    "dev.alexcawl.minecraft.loader": "FABRIC"
                  },
                  "files": [{ "name": "$artifact-1.0.accesswidener", "url": "$artifact-1.0.accesswidener" }]
                }
              ]
            }
            """.trimIndent()
        )
    }

    private fun writeMavenModule(
        artifact: String = "common",
        dependencies: List<String> = emptyList(),
        entries: Map<String, String> = mapOf("$artifact.txt" to artifact),
    ) {
        val directory = projectDir.resolve("repo/com/example/$artifact/1.0").createDirectories()
        directory.resolve("$artifact-1.0.pom").writeText(
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
        ZipOutputStream(Files.newOutputStream(directory.resolve("$artifact-1.0.jar"))).use { jar ->
            entries.forEach { (path, content) ->
                jar.putNextEntry(ZipEntry(path))
                jar.write(content.toByteArray())
                jar.closeEntry()
            }
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
