package dev.alexcawl.mcmultiloader.common

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants.DESCRIPTOR_PATH
import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants.Configuration.FABRIC_ACCESS_WIDENER_ELEMENTS
import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants.Configuration.NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS
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
            mcCommonLoader {
                metadata {
                    resources {
                        resource("first.txt") { "value"(providers.provider { "first" }) }
                        resource("second.txt") { "value"("second") }
                        resource("a/shared.txt", "b/shared.txt") {
                            "value"("shared")
                        }
                        resource("dynamic") {
                            "value"("provider")
                        }
                    }
                    jarManifest {
                        "Implementation-Version"(providers.provider { project.version.toString() })
                    }
                }
                access {
                    fabricAccessWidener("src/main/resources/accesswidener")
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

    @Test
    fun `publishes access modifier variants`() {
        write("settings.gradle.kts", "rootProject.name = \"access-variant-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                `maven-publish`
                id("dev.alexcawl.mcmultiloader.common")
            }
            group = "com.example"
            version = "1.0"
            publishing {
                publications {
                    create<MavenPublication>("maven") { from(components["java"]) }
                }
                repositories { maven { url = uri("repo") } }
            }
            mcCommonLoader {
                access {
                    fabricAccessWidener("src/main/resources/accesswidener")
                    neoForgeAccessTransformer("src/main/resources/META-INF/accesstransformer.cfg")
                }
            }
            """.trimIndent()
        )
        write("src/main/resources/accesswidener", "accessWidener v2 named\n")
        write("src/main/resources/META-INF/accesstransformer.cfg", "public com.example.Common value\n")

        runner("publish").build()

        val module = projectDir
            .resolve("repo/com/example/access-variant-fixture/1.0/access-variant-fixture-1.0.module")
            .toFile()
            .readText()
        assertContains(module, FABRIC_ACCESS_WIDENER_ELEMENTS)
        assertContains(module, NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS)
        assertContains(module, "\"dev.alexcawl.minecraft.loader\": \"FABRIC\"")
        assertContains(module, "\"dev.alexcawl.minecraft.loader\": \"NEOFORGE\"")
        assertContains(module, "\"dev.alexcawl.minecraft.accessModifier\": \"ACCESS_WIDENER\"")
        assertContains(module, "\"dev.alexcawl.minecraft.accessModifier\": \"ACCESS_TRANSFORMER\"")
    }

    @Test
    fun `does not create unconfigured access modifier variant`() {
        write("settings.gradle.kts", "rootProject.name = \"missing-access-variant-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins {
                `java-library`
                id("dev.alexcawl.mcmultiloader.common")
            }
            mcCommonLoader {
                access { neoForgeAccessTransformer("src/main/resources/META-INF/accesstransformer.cfg") }
            }
            tasks.register("verifyAccessVariants") {
                doLast {
                    check(configurations.findByName("$FABRIC_ACCESS_WIDENER_ELEMENTS") == null)
                    check(configurations.findByName("$NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS") != null)
                }
            }
            """.trimIndent()
        )
        write("src/main/resources/META-INF/accesstransformer.cfg", "public com.example.Common value\n")

        runner("verifyAccessVariants").build()
    }

    @Test
    fun `does not depend on Java plugin application order`() {
        write("settings.gradle.kts", "rootProject.name = \"plugin-order-fixture\"")
        write(
            "build.gradle.kts",
            """
            plugins { id("dev.alexcawl.mcmultiloader.common") }
            mcCommonLoader {
                access { fabricAccessWidener("src/main/resources/accesswidener") }
            }
            apply(plugin = "java-library")
            """.trimIndent()
        )
        write("src/main/resources/accesswidener", "accessWidener v2 named\n")

        runner("jar").build()

        JarFile(projectDir.resolve("build/libs/plugin-order-fixture.jar").toFile()).use { jar ->
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
