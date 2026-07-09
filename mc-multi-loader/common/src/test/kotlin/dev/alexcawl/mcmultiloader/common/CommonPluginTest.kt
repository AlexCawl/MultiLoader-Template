package dev.alexcawl.mcmultiloader.common

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.writeText

class CommonPluginTest {
    @TempDir
    lateinit var projectDir: Path

    @Test
    fun `does not apply Java plugin`() {
        projectDir.resolve("settings.gradle.kts").writeText("rootProject.name = \"common-isolation\"")
        projectDir.resolve("build.gradle.kts").writeText(
            """
            plugins { id("dev.alexcawl.mcmultiloader.common") }
            tasks.register("verifyNoJava") {
                doLast { check(!pluginManager.hasPlugin("java")) }
            }
            """.trimIndent()
        )

        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withArguments("verifyNoJava", "--stacktrace")
            .withPluginClasspath()
            .build()
    }
}
