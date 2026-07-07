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
        register("common") {
            id = "dev.alexcawl.mcmultiloader.common"
            implementationClass = "dev.alexcawl.mcmultiloader.CommonPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
