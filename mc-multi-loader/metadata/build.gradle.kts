plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    // Workaround for version catalogs in Kotlin Gradle plugin sources.
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

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
