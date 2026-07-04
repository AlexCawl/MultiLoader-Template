plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("repositories") {
            id = "dev.alexcawl.convention.repositories"
            implementationClass = "dev.alexcawl.convention.RepositoriesConventionPlugin"
        }
    }
}
