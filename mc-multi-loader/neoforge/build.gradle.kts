plugins {
    `kotlin-dsl`
}

dependencies {
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
