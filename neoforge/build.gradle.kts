plugins {
    `java-library`
    alias(libs.plugins.neoforge.moddev)
    id("dev.alexcawl.convention.repositories")
    id("dev.alexcawl.mcmultiloader.neoforge")
}

dependencies {
    merged(project(":common"))
}

java {
    toolchain.languageVersion.set(libs.versions.ext.java.map { JavaLanguageVersion.of(it.toInt()) })
}

val neoforgeVersion = libs.versions.ext.neoforge.api.get()
val minecraftVersion = libs.versions.ext.minecraft.current.get()
val minecraftVersionRange = libs.versions.ext.minecraft.range.get()
val modName = properties["mod_name"] as String
val modAuthor = properties["mod_author"] as String
val modId = properties["mod_id"] as String
val modLicense = properties["license"] as String
val credits = properties["credits"] as String
val description = properties["description"] as String

base {
    archivesName = "$modId-${project.name}-$minecraftVersion"
}

mcMultiLoader {
    metadata {
        template {
            template("META-INF/neoforge.mods.toml") {
                "version"(version)
                "minecraft_version_range"(minecraftVersionRange)
                "neoforge_version"(neoforgeVersion)
                "neoforge_loader_version_range"(libs.versions.ext.neoforge.loader.range)
                "mod_name"(modName)
                "mod_author"(modAuthor)
                "mod_id"(modId)
                "license"(modLicense)
                "description"(description)
                "credits"(credits)
            }
            template("$modId.neoforge.mixins.json") {}
        }

        jarManifest {
            "Specification-Title"(modName)
            "Specification-Vendor"(modAuthor)
            "Specification-Version"(version)
            "Implementation-Title"(project.name)
            "Implementation-Version"(version)
            "Implementation-Vendor"(modAuthor)
            "Built-On-Minecraft"(minecraftVersion)
        }
    }
}

neoForge {
    version = neoforgeVersion
    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            ideName = "NeoForge ${name.replaceFirstChar(Char::uppercase)} (${project.path})"
        }
        create("client") {
            client()
            gameDirectory.set(project.layout.projectDirectory.dir("runs/client"))
        }
        create("server") {
            server()
            gameDirectory.set(project.layout.projectDirectory.dir("runs/server"))
        }
        create("data") {
            data()
            gameDirectory.set(project.layout.projectDirectory.dir("runs/datagen"))
            programArgument("--mod")
            programArgument(modId)
            programArgument("--all")
            programArgument("--output")
            programArgument(file("src/generated/resources").absolutePath)
            programArgument("--existing")
            programArgument(file("src/main/resources").absolutePath)
        }
    }
    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}
