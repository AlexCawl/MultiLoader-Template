plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation(project(":common"))
    compileOnly("net.neoforged:moddev-gradle:2.0.141")

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        register("neoforge") {
            id = "dev.alexcawl.mcmultiloader.neoforge"
            implementationClass = "dev.alexcawl.mcmultiloader.NeoForgePlugin"
        }
    }
}
