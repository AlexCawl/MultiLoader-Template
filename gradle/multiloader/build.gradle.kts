plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("publisher") {
            id = "dev.alexcawl.multiloader.publisher"
            implementationClass = "dev.alexcawl.multiloader.PublisherModulePlugin"
        }
        register("consumer") {
            id = "dev.alexcawl.multiloader.consumer"
            implementationClass = "dev.alexcawl.multiloader.ConsumerModulePlugin"
        }
    }
}
