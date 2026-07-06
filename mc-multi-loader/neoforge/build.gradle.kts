plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    // Workaround for version catalogs in Kotlin Gradle plugin sources.
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(project(":common"))
    compileOnly(libs.neoforge.moddev.gradle.plugin)

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

tasks.pluginUnderTestMetadata {
    pluginClasspath.from(configurations.compileClasspath)
}

gradlePlugin {
    plugins {
        register("neoforge") {
            id = "dev.alexcawl.mcmultiloader.neoforge"
            implementationClass = "dev.alexcawl.mcmultiloader.NeoForgePlugin"
        }
    }
}
