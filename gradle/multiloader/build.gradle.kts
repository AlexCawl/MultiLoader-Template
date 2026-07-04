plugins {
    `kotlin-dsl`
}

dependencies {
    // Workaround for version catalog working inside precompiled scripts
    // Issue - https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.fabric.loom.gradle.plugin)
    implementation(libs.neoforge.moddev.gradle.plugin)
    implementation(libs.forge.gradle.plugin)
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
