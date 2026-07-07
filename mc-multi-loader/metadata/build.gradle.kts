plugins {
    `kotlin-dsl`
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

gradlePlugin {
    plugins {
        register("metadata") {
            id = "dev.alexcawl.mcmultiloader.metadata"
            implementationClass = "dev.alexcawl.mcmultiloader.metadata.MetadataPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
