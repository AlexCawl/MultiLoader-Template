plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation(project(":common"))
    compileOnly("net.fabricmc:fabric-loom:1.8.13")

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

gradlePlugin {
    plugins {
        register("fabric") {
            id = "dev.alexcawl.mcmultiloader.fabric"
            implementationClass = "dev.alexcawl.mcmultiloader.FabricPlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
