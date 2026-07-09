plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":core"))

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

gradlePlugin {
    plugins {
        register("common") {
            id = "dev.alexcawl.mcmultiloader.common"
            implementationClass = "dev.alexcawl.mcmultiloader.common.CommonPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
