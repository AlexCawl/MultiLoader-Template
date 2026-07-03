plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("metadata") {
            id = "dev.alexcawl.metadata"
            implementationClass = "dev.alexcawl.metadata.MetadataPlugin"
        }
    }
}
