plugins { base }

allprojects {
    group = "dev.alexcawl.mcmultiloader"
    version = "0.1.0-SNAPSHOT"

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.neoforged.net/releases")
    }
}
