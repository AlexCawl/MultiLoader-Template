plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("producer") {
            id = "dev.alexcawl.multiloader.producer"
            implementationClass = "dev.alexcawl.multiloader.ProducerModulePlugin"
        }
        register("consumer") {
            id = "dev.alexcawl.multiloader.consumer"
            implementationClass = "dev.alexcawl.multiloader.ConsumerModulePlugin"
        }
    }
}
