plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        register("common") {
            id = "dev.alexcawl.mcmultiloader.common"
            implementationClass = "dev.alexcawl.mcmultiloader.CommonPlugin"
        }
    }
}
