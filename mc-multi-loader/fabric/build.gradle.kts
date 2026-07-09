plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":core"))
    compileOnly(libs.fabric.loom.gradle.plugin)

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

gradlePlugin {
    plugins {
        register("fabric") {
            id = "dev.alexcawl.mcmultiloader.fabric"
            implementationClass = "dev.alexcawl.mcmultiloader.fabric.FabricPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.pluginUnderTestMetadata {
    pluginClasspath.from(configurations.compileClasspath)
}
